package io.xiges.firebase.config.auth.firebase;

import io.xiges.firebase.service.FirebaseService;
import io.xiges.firebase.service.exception.FirebaseTokenInvalidException;
import io.xiges.firebase.util.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FirebaseFilter extends OncePerRequestFilter {

    private static String HEADER_NAME = "X-Authorization-Firebase";

    private FirebaseService firebaseService;

    public FirebaseFilter(FirebaseService firebaseService){
        this.firebaseService = firebaseService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String xAuth = httpServletRequest.getHeader(HEADER_NAME);

        if (StringUtil.isBlank(xAuth)){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }else {
            try {
              FirebaseTokenHolder  holder = firebaseService.parseToken(xAuth);

                String userName = holder.getUid();

                Authentication auth = new FirebaseAuthenticationToken(userName,holder);
                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(httpServletRequest,httpServletResponse);
            }catch (FirebaseTokenInvalidException e){
                throw new SecurityException(e);
            }
        }

    }
}
