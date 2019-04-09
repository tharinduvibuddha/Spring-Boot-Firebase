package io.xiges.firebase.service.shared;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;
import io.xiges.firebase.config.auth.firebase.FirebaseTokenHolder;
import io.xiges.firebase.service.exception.FirebaseTokenInvalidException;
import io.xiges.firebase.util.StringUtil;

public class FirebaseParser {

    public FirebaseTokenHolder parseToken(String idToken){
        if(StringUtil.isBlank(idToken)){
            throw new IllegalArgumentException("Firebase Token Blank!!");
        }

        try {
            Task<FirebaseToken> authTask = FirebaseAuth.getInstance().verifyIdToken(idToken);
            Tasks.await(authTask);

            return new FirebaseTokenHolder(authTask.getResult());
        }catch (Exception e){
            throw new FirebaseTokenInvalidException(e.getMessage());
        }
    }
}
