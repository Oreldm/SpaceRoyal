package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;


import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter{
	private final float UPDATE_TIME=1/60f;
	float timer;
	public static boolean isGameOver=false;
	static SpriteBatch batch;
	public static Socket socket;
	String id;
	Starship player;
	Texture playerShip;
	Texture friendlyShip;
	Texture background;
	HashMap<String, Starship> friendlyPlayers;
	Controller controller;
	ArrayList<Texture> boomArr = new ArrayList<Texture>();
	HashMap<Vector2,Integer> bombToDraw=new HashMap<Vector2, Integer>();
    public static ArrayList<Shoot>enemyShoots=new ArrayList<Shoot>();
    ArrayList<String> enemyShootsToCreate=new ArrayList<String>(); //the string is playerID
	HealthBar hpBar;
	Sprite heartIcon;
	Sprite backHealthBar;
	float sizeOfHealthBar;
	float sizeOfBackHealthBar;
	float basicHeightPosition;
	BitmapFont font;
	public static boolean isFirstTime=true;
	static int loops =0;
	public static String endGameStr="Game Over!\n Press Back";
	public static boolean canWrite=true;
	public static boolean isWin=false;


	@Override
	public void create () {
		font = new BitmapFont();
		batch = new SpriteBatch();
		playerShip = new Texture("Rocket_1.png");
		friendlyShip = new Texture("Rocket_2.png");
		background = new Texture("GameplayBackground.png");
		friendlyPlayers = new HashMap<String, Starship>();
		hpBar=new HealthBar(new Texture(HealthBar.HEALTH_BAR_IMAGE));
		controller = new Controller();
		heartIcon=new Sprite(new Texture("HPIcon.png"));
		heartIcon.setPosition(1,Gdx.graphics.getHeight()-heartIcon.getHeight());
		backHealthBar=new HealthBar(new Texture(HealthBar.HEALTH_BAR_BACK_IMAGE));
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		for(int i=1;i<15;i++){
			boomArr.add(new Texture(CollisionBoom.animationBaseName+i+".png"));
		}
		sizeOfHealthBar=(hpBar.getWidth()-300);
		sizeOfBackHealthBar=(hpBar.getWidth()-295);
		basicHeightPosition=Gdx.graphics.getHeight()-hpBar.getHeight()-heartIcon.getWidth()/4;
		connectSocket();
		player = new Starship(playerShip);
		configSocketEvents();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void handleInput(float dt){
		if(player != null && !isGameOver) {
			float speed= 400 * dt;
			if(controller.isRightPressed()){
				Gdx.app.log("Movement", "RIGHT");
				if(player.getX()+player.getHeight()-player.getWidth()<Gdx.graphics.getWidth())
					player.setPosition(player.getX()+speed, player.getY());
				player.setRotation(90+180);
			}
			else if (controller.isLeftPressed()) {
				Gdx.app.log("Movement", " LEFT");
				if(player.getX()>0)
					player.setPosition(player.getX()-speed, player.getY());
				player.setRotation(90);
			}
			if (controller.isUpPressed() ){
				Gdx.app.log("Movement", " UP PRESSED");
				if(player.getY()+player.getHeight()<Gdx.graphics.getHeight())
					player.setPosition(player.getX(), player.getY()+speed);
				player.setRotation(0);
			}
			if(controller.isDownPressed()){
				Gdx.app.log("Movement", " DOWN PRESSED");
				if(player.getY()>0)
					player.setPosition(player.getX(), player.getY()-speed);
				player.setRotation(180);
			}

			if(controller.isShootPressed()){
				Gdx.app.log("Movement", " Shoot");
                new Shoot(new Texture(Shoot.SHOOT_IMAGE), player);
				Controller.shootPressed=false;
				player.hasShoot=true;

			}

		}
	}

	public void updateServer(float dt){
		timer +=dt;

		if(player!=null && HealthBar.HP<=0){
			JSONObject data = new JSONObject();
			try{
				data.put("hp",HealthBar.HP);
				socket.emit("dead", data);
			}catch(Exception e){}
		}

		//update move of player
		if(timer>= UPDATE_TIME && player !=null && player.hasMoved()){
			JSONObject data = new JSONObject();
			try{
				data.put("x",player.getX());
				data.put("y",player.getY());
				data.put("rotation",player.getRotation());
				socket.emit("playerMoved", data);
			}catch(Exception e){}
		}

		//update shoot
		if(timer>= UPDATE_TIME && player !=null && player.hasShoot){
			player.hasShoot=false;
			JSONObject data = new JSONObject();
			try{
				data.put("x",player.getX());
				data.put("y",player.getY());
				data.put("rotation",player.getRotation());
				socket.emit("shoot", data);
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

		//Placement
		if(loops <100) //100 is because friendlyPlayers might need to load :)
			loops++;
		if(friendlyPlayers.size()>0 && isFirstTime && loops <100){
			isFirstTime=false;
			player.setPosition(Gdx.graphics.getWidth()/2-player.getWidth()/2,Gdx.graphics.getHeight()-player.getHeight());
			player.setRotation(180);
		} else if(isFirstTime && loops ==100)
			isFirstTime=false;
		//PLACEMENT UNTIL HERE



		new Thread(new Runnable() {
			//Thread to update server simultanosly so the game will glide
			@Override
			public void run() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						updateServer(Gdx.graphics.getDeltaTime());
					}
				});
			}
		}).start();

		batch.begin();
		batch.draw(background, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		/**
		 * Health bar
		 */
		//back health bar
		batch.draw(backHealthBar,heartIcon.getWidth()/2,basicHeightPosition,sizeOfBackHealthBar,
				hpBar.getHeight()+14);
		//front health bar
		float barSize=((float)HealthBar.HP/100);
		batch.draw(hpBar,heartIcon.getWidth()/2,basicHeightPosition+7
				,sizeOfHealthBar*barSize,hpBar.getHeight());
		heartIcon.draw(batch);
		/*END OF HEALTH BAR*/


		if(player != null){
			player.draw(batch);
		}

		/**
		 * Drawing Shots That get OUT OF PLAYER
		 * **/
		try{
			for(Shoot s : Shoot.shots){
				s.draw(batch);
				s.move();
				if(s.getX()> Gdx.graphics.getWidth() || s.getX()<0 || s.getY()>Gdx.graphics.getHeight() || s.getY()<0)
					//Shot is outside of screen
					Shoot.shots.remove(s);
				Iterator it = friendlyPlayers.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry pair= (Map.Entry)it.next();
					Starship enemy = (Starship)pair.getValue();
					//Collision Detection
					boolean isCollision=false;
					if(enemy.getRotation()== 0 || enemy.getRotation()==180)
						//enemy is vertical
						isCollision=(s.getX()> enemy.getX() && s.getX()<enemy.getX()+enemy.getWidth()) && (s.getY()<enemy.getY()+enemy.getHeight() && s.getY()>enemy.getY());
					else
						//enemy is horizontal
						isCollision=((s.getX()>enemy.getX() && s.getX()<enemy.getX()+enemy.getHeight()) && (s.getY()>enemy.getY() && s.getY()<enemy.getY()+enemy.getWidth()));

					if(isCollision){
						enemy.setOrigin(enemy.getWidth()/2-enemy.getWidth()/4,enemy.getHeight()/2);

						bombToDraw.put(new Vector2(enemy.getX(),enemy.getY()),1); //adding shot that should be draw
						Shoot.shots.remove(s);
						Gdx.app.log("Collision", "Kaboom");
					}
				}
			}}catch(ConcurrentModificationException e){Shoot.shots = new ArrayList<Shoot>();}

        /*
        * Drawing shots that GET OUT OF ENEMY
        * */
        try{
            for(String id : enemyShootsToCreate){
                enemyShoots.add(new Shoot(new Texture(Shoot.SHOOT_IMAGE),friendlyPlayers.get(id)));
                enemyShootsToCreate.remove(id);
            }
            for(Shoot s : enemyShoots){
                s.draw(batch);
                s.move();
                if(s.getX()> Gdx.graphics.getWidth() || s.getX()<0 || s.getY()>Gdx.graphics.getHeight() || s.getY()<0)
                    //Shot is outside of screen
                    enemyShoots.remove(s);

                boolean isCollision=false;
                if(player.getRotation()== 0 || player.getRotation()==180)
                    //enemy is vertical
                    isCollision=(s.getX()> player.getX() && s.getX()<player.getX()+player.getWidth()) && (s.getY()<player.getY()+player.getHeight() && s.getY()>player.getY());
                else
                    //enemy is horizontal
                    isCollision=((s.getX()>player.getX() && s.getX()<player.getX()+player.getHeight()) && (s.getY()>player.getY() && s.getY()<player.getY()+player.getWidth()));

                if(isCollision){
                    player.setOrigin(player.getWidth()/2-player.getWidth()/4,player.getHeight()/2);
					Random r = new Random();
					int hpToRemove = r.nextInt(19 - 1) + 1;
					HealthBar.HP=HealthBar.HP-hpToRemove;
                    bombToDraw.put(new Vector2(player.getX(),player.getY()),1); //adding bomb that should be draw
                    enemyShoots.remove(s);
                    Gdx.app.log("Collision", "Kaboom");
                }
            }
        }catch(ConcurrentModificationException e){
            enemyShoots = new ArrayList<Shoot>();
            enemyShootsToCreate=new ArrayList<String>(); //the string is playerID
        }


		try{
		for(HashMap.Entry<Vector2, Integer> entry : bombToDraw.entrySet()){ //drawing boom collisions
			if(entry.getValue()<14){
				new CollisionBoom(boomArr.get(entry.getValue()),entry.getKey().x-boomArr.get(entry.getValue()).getWidth()/3,entry.getKey().y).draw(batch);
				bombToDraw.put(entry.getKey(), entry.getValue()+1);
			}
			if(entry.getValue()>=14){ //removing shot from MAP
					bombToDraw.remove(entry.getKey());
			}
		}}catch(ConcurrentModificationException e){bombToDraw=new HashMap<Vector2, Integer>();}

		for(HashMap.Entry<String, Starship> entry : friendlyPlayers.entrySet()){
			entry.getValue().draw(batch);
		}

		if(isGameOver){
        	handleInput(Gdx.graphics.getDeltaTime());
			canWrite=false;
			MyGdxGame.socket.disconnect();
			font.setColor(new Color(Color.WHITE));
			font.getData().setScale(4);
			font.draw(batch, endGameStr,Gdx.graphics.getWidth()/3,Gdx.graphics.getHeight()/2);
		}


        batch.end();

        if(HealthBar.HP<=0){
			updateServer(Gdx.graphics.getDeltaTime());
			if(canWrite)
				endGameStr="You Lose :(\n"+endGameStr;
			isGameOver=true;
			canWrite=false;
		}
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
					Double rotation = data.getDouble("rotation");
					if(friendlyPlayers.get(playerId)!=null){
						friendlyPlayers.get(playerId).setPosition(x.floatValue(),y.floatValue());
						friendlyPlayers.get(playerId).setRotation(rotation.floatValue());
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
		}).on("shoot", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
						String playerId=data.getString("id");
						Double x = data.getDouble("x");
						Double y = data.getDouble("y");
						Double r = data.getDouble("rotation");
						if(friendlyPlayers.get(playerId)!=null){
							//enemyShoots.add(new Shoot(new Texture(Shoot.SHOOT_IMAGE),friendlyPlayers.get(playerId)));
                            enemyShootsToCreate.add(playerId);
						}
				} catch(JSONException e){

				}
			}
		}).on("dead", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
					//CLOSE GAME
					isGameOver=true;
					isWin=true;
					if(endGameStr.equals("Game Over!\n Press Back")) {
						endGameStr = "You WON! \n" + endGameStr;
					}
			}
		});
	}


}