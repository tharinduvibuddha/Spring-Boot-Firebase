package io.xiges.firebase.service;

import io.xiges.firebase.config.auth.firebase.FirebaseTokenHolder;


public interface FirebaseService {

	FirebaseTokenHolder parseToken(String idToken);

}
