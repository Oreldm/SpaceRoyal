package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;


import java.util.HashMap;

public class MyGdxGame extends ApplicationAdapter{
	private final float UPDATE_TIME=1/60f;
	float timer;
	static SpriteBatch batch;
	private Socket socket;
	String id;
	Starship player;
	Texture playerShip;
	Texture friendlyShip;
	HashMap<String, Starship> friendlyPlayers;
	Controller controller;

	@Override
	public void create () {
		batch = new SpriteBatch();
		playerShip = new Texture("Rocket_1.png");
		friendlyShip = new Texture("Rocket_2.png");
		friendlyPlayers = new HashMap<String, Starship>();
		controller = new Controller();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		connectSocket();
		configSocketEvents();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void handleInput(float dt){
		if(player != null) {
			float speed= 400 * dt;
			if(controller.isRightPressed()){
				Gdx.app.log("Movement", "RIGHT");
				player.setPosition(player.getX()+speed, player.getY());
				player.setRotation(90+180);
			}
			else if (controller.isLeftPressed()) {
				Gdx.app.log("Movement", " LEFT");
				player.setPosition(player.getX()-speed, player.getY());
				player.setRotation(90);
			}
			if (controller.isUpPressed() ){
				Gdx.app.log("Movement", " UP PRESSED");
				player.setPosition(player.getX(), player.getY()+speed);
				player.setRotation(0);
			}
			if(controller.isDownPressed()){
				Gdx.app.log("Movement", " DOWN PRESSED");
				player.setPosition(player.getX(), player.getY()-speed);
				player.setRotation(180);
			}

			if(controller.isShootPressed()){
				Gdx.app.log("Movement", " Shoot");
                new Shoot(new Texture("ShootingAsset.png"), player);
				Controller.shootPressed=false;
			}

		}
	}

	public void updateServer(float dt){
		timer +=dt;
		if(timer>= UPDATE_TIME && player !=null && player.hasMoved()){
			JSONObject data = new JSONObject();
			try{
				data.put("x",player.getX());
				data.put("y",player.getY());
				socket.emit("playerMoved", data);
			}catch(Exception e){}
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//if(friendlyPlayers.size()==1) {
			handleInput(Gdx.graphics.getDeltaTime());
		/**} else if (friendlyPlayers.size()>1){
			socket.disconnect(); //should change here what happens when disconnect
		}*/

		updateServer(Gdx.graphics.getDeltaTime());

		batch.begin();
		if(player != null){
			player.draw(batch);
		}
		int i=0;
		ArrayList<Integer> shotsToRemove=new ArrayList<Integer>();
		for(Shoot s : Shoot.shots){
			i++;
			s.draw(batch);
			s.move();
			if(s.getX()> Gdx.graphics.getWidth() || s.getX()<0 || s.getY()>Gdx.graphics.getHeight() || s.getY()<0)
				shotsToRemove.add(i);
		}
		for(Integer remove : shotsToRemove){
			Shoot.shots.remove(remove);
		}
		//Collision Detection


		for(HashMap.Entry<String, Starship> entry : friendlyPlayers.entrySet()){
			entry.getValue().draw(batch);
		}
        batch.end();
		if(Gdx.app.getType() == Application.ApplicationType.Android)
			controller.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		playerShip.dispose();
		friendlyShip.dispose();
	}

	public void connectSocket(){
		try {
			socket = IO.socket("http://ec2-34-241-75-147.eu-west-1.compute.amazonaws.com:8080");
			//socket = IO.socket("http://localhost:8080");
			socket.connect();
		} catch(Exception e){
			System.out.println(e);
		}
	}

	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				player = new Starship(playerShip);
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {

					id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + id);
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting ID");
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					Gdx.app.log("SocketIO", "New Player Connect: " + id);
					friendlyPlayers.put(playerId, new Starship(friendlyShip));
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting New PlayerID");
				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					friendlyPlayers.remove(id);
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId=data.getString("id");
					Double x = data.getDouble("x");
					Double y = data.getDouble("y");
					if(friendlyPlayers.get(playerId)!=null){
						friendlyPlayers.get(playerId).setPosition(x.floatValue(),y.floatValue());
					}
				}catch(Exception e){
				}
			}
		}).on("getPlayers", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray objects = (JSONArray) args[0];
				try {
					for(int i = 0; i < objects.length(); i++){
						Starship coopPlayer = new Starship(friendlyShip);
						Vector2 position = new Vector2();
						position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
						position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
						coopPlayer.setPosition(position.x, position.y);

						friendlyPlayers.put(objects.getJSONObject(i).getString("id"), coopPlayer);
					}
				} catch(JSONException e){

				}
			}
		});
	}


}