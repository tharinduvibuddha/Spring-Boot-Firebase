package io.xiges.firebase.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    private static final String FIREBASE_SERVER_KEY = "AAAAdXCRbMU:APA91bGsOkbVYkRVVLCVg7-A90ERFCrwQTkrodFWifjF2-1eYzt4cfpMEkBqPA59ufWD8eaEl6Z02nWeJGhYvJu07fJrg9iPDLBhjWoRaQt5NM0z22T_nIJRSLJZgOEvf5k-nJM3K9yl";
    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";


    @Async
    public CompletableFuture<String> send(HttpEntity<String> httpEntity){

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> requestInterceptors = new ArrayList<>();
        requestInterceptors.add(new HeaderRequestInterceptor("Authorization","key=" + FIREBASE_SERVER_KEY));
        requestInterceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(requestInterceptors);

        String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL,httpEntity,String.class);

        return CompletableFuture.completedFuture(firebaseResponse);
    }
}
