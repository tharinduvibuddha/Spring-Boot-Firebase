package io.xiges.firebase.config.auth.firebase;

import io.xiges.firebase.service.exception.FirebaseUserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class FirebaseAuthenticationProvider implements AuthenticationProvider{

    @Autowired
    @Qualifier
    private UserDetailsService userDetailsService;


    public boolean supports(Class<?> authentication){
        return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())){
            return null;
        }

        FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationToken.getName());
        if (userDetails == null){
            throw new FirebaseUserNotExistsException();
        }

        authenticationToken = new FirebaseAuthenticationToken(userDetails,authentication.getCredentials(),
                                userDetails.getAuthorities());

        return authenticationToken;
    }


}