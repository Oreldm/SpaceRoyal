package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.File;
import java.io.FileOutputStream;

import oracle.jdbc.driver.*;

import static com.mygdx.game.FileHandler.readFromFile;


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
		MyGdxGame.isGameOver=false;
		HealthBar.HP=100;
		MyGdxGame.endGameStr="Game Over!\n Press Back";
		MyGdxGame.socket.disconnect();
		MyGdxGame.canWrite=true;

		try { FileHandler.createFileIfNotExists(); }catch(Exception e){}
		String fileStr= FileHandler.readFromFile(this);
		if(fileStr.length()==0){
			if(MyGdxGame.isWin)
				FileHandler.writeToFile("1,0",this);
			else
				FileHandler.writeToFile("0,1",this);
		}else{
			String wins=fileStr.substring(0,fileStr.indexOf(","));
			String lose=fileStr.substring(fileStr.indexOf(",")+1,fileStr.length());
			Integer winInt=Integer.parseInt(wins);
			Integer loseInt=Integer.parseInt(lose);
			if(MyGdxGame.isWin)
				winInt++;
			else if(MyGdxGame.isLose)
				loseInt++;
			FileHandler.writeToFile(winInt+","+loseInt,this);
		}
		MyGdxGame.isWin=false;
		MyGdxGame.isLose=false;
		finish();
	}


}
