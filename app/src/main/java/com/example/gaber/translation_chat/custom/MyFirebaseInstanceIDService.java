package com.example.gaber.translation_chat.custom;

        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gaber on 26/08/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedtoken= FirebaseInstanceId.getInstance().getToken();


    }




}
