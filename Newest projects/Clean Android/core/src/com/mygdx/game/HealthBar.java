package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class HealthBar extends Sprite {

    public static final String HEALTH_BAR_IMAGE="Loader.png";
    public static final String HEALTH_BAR_BACK_IMAGE="LoadingBack.png";
    public static int HP=100;

    public HealthBar(Texture texture){
        super(texture);
    }

}
