package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

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
/*
	@Override
	public void onPause(){
		super.onPause();
		if(HealthBar.HP<=0) {
			MyGdxGame.socket.disconnect();
			finish();
		}
	}
*/





	@Override
	public void onBackPressed(){
		super.onBackPressed();
		MyGdxGame.socket.disconnect();
		finish();
	}
}
