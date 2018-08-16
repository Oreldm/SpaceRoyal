package com.mygdx.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class MainMenu extends Activity {

    Button startGame;
    Intent intent;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    LoginButton loginButton;
    public static AccessToken accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HealthBar.HP=100;
        setContentView(R.layout.activity_main_menu);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL,"user_friends","read_insights"));
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = AccessToken.getCurrentAccessToken();
                Log.d("Facebook",accessToken.toString());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn)
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","user_friends","read_insights","user_status",
                    "friends_status","user_photos","friends_photos","user_location","friends_location"));

        startGame = (Button)findViewById(R.id.btn_play);

        // handle set start click
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, AndroidLauncher.class);
                MainMenu.this.startActivity(intent);
            }
        });

        Button friendsBtn = (Button)findViewById(R.id.btn_frnds);

        // handle set start click
        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, FriendsActivity.class);
                MainMenu.this.startActivity(intent);
            }
        });

        Button storeButton = (Button)findViewById(R.id.btn_store);

        // handle set start click
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, StoreActivity.class);
                MainMenu.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume(){
        super.onResume();
        HealthBar.HP=100;
        MyGdxGame.isFirstTime=true;
        MyGdxGame.loops =0;
    }
}
