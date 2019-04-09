package io.xiges.firebase.service.impl;


import io.xiges.firebase.config.auth.firebase.FirebaseTokenHolder;
import io.xiges.firebase.service.FirebaseService;
import io.xiges.firebase.service.shared.FirebaseParser;
import org.springframework.stereotype.Service;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public FirebaseTokenHolder parseToken(String firebaseToken) {
        return new FirebaseParser().parseToken(firebaseToken);
    }
}
