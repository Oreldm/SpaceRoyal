package com.mygdx.game;

import android.os.Bundle;
import android.app.Activity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class FaceBookLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_book_login);
    }

}
