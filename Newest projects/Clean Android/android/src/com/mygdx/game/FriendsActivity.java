package com.mygdx.game;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class FriendsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainMenu.accessToken=AccessToken.getCurrentAccessToken();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        GraphRequest request = GraphRequest.newGraphPathRequest(
                MainMenu.accessToken,
                "/me/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.d("Facebook",response.toString());
                        Log.d("Facebook",MainMenu.accessToken.getUserId());
                        //Here show things about Facebook + Win/Lose
                    }
                });

        request.executeAsync();

        String str=FileHandler.readFromFile(this);
        Log.d("File",str);
    }


}
