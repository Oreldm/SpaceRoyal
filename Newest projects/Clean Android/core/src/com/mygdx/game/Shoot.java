package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Shoot extends Sprite {
    public static ArrayList<Shoot>shots=new ArrayList<Shoot>();

    Vector2 previousPosition;
    public Shoot(Texture texture, Starship starship){
        super(texture);
        this.setPosition(starship.previousPosition.x/2,starship.previousPosition.y/2);
        previousPosition = new Vector2(getX(), getY());
        shots.add(this);
    }
}
