package com.mygdx.game;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class FriendsActivity extends Activity {
    String username="";
    String numberOfFriends="0";
    int numberOfFriendsWithApp=0;
    String facebookString="";
    String statisticsStr="";

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
                        try{
                            JSONObject graphObject=response.getJSONObject();
                            JSONArray friends= (JSONArray)graphObject.get("data");
                            numberOfFriendsWithApp=friends.length();
                            JSONObject summary= (JSONObject)graphObject.get("summary");
                            Integer f = (Integer)summary.get("total_count");
                            numberOfFriends=f.toString();
                            if(Profile.getCurrentProfile()!=null){
                                username = Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName();
                            }

                            facebookString="Hello "+username + ".\n"+"You have "+numberOfFriends+" Friends\n"+"But only "+numberOfFriendsWithApp+" have SpaceRoyal :(";
                            TextView facebookText = (TextView)findViewById(R.id.facebookText);
                            facebookText.setText(facebookString);
                        }
                        catch(Exception e){e.printStackTrace();}

//                        Log.d("Facebook",response.getJSONObject().toString());
//                        Log.d("Facebook",MainMenu.accessToken.getUserId());
                    }
                });

        request.executeAsync();

        String str=FileHandler.readFromFile(this);
        String wins=str.substring(0,str.indexOf(","));
        Log.d("File",wins);
        String lose=str.substring(str.indexOf(",")+1,str.length());
        Log.d("File",lose);
        statisticsStr = "You Have "+wins +" WINS! \n" + "You Lost "+lose+" Times";
        TextView statText=(TextView)findViewById(R.id.statText);
        statText.setText(statisticsStr);
    }


}
