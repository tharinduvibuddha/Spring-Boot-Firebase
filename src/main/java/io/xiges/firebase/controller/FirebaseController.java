package io.xiges.firebase.controller;

import io.xiges.firebase.service.NotificationService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class FirebaseController {

    private final String TOPIC = "FirebaseExample";

    @Autowired
    NotificationService notificationService;

    @RequestMapping(value = "/send",method = RequestMethod.GET,produces = "application/json")
    public ResponseEntity<String > sendNotification() throws JSONException{
        JSONObject body = new JSONObject();
        body.put("to","/topics/"+TOPIC);
        body.put("priority","high");

        JSONObject noitification = new JSONObject();
        noitification.put("body"," Welcome to Firebase! ");

        JSONObject data = new JSONObject();
        data.put("key-1", "JSA Data 1");
        data.put("key-2", "JSA Data 2");

        body.put("notification",noitification);
        body.put("data",data);

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        CompletableFuture<String> pushNotification = notificationService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();

            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }

        return new ResponseEntity<>("Firebase Notification Error!", HttpStatus.BAD_REQUEST);
    }


}
