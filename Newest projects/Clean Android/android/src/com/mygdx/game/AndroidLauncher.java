package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication {

	public static MyGdxGame game;

	public static AndroidLauncher HighestActivity=null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		HighestActivity=this;
		game=new MyGdxGame();
		initialize(game, config);
	}


	@Override
	public void onBackPressed(){
		//SAVE HERE GAME DATA
		super.onBackPressed();
		MyGdxGame.socket.disconnect();
		finish();
	}
}
