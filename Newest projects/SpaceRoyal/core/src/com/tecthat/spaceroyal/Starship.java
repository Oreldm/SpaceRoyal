package com.tecthat.spaceroyal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Starship extends Sprite {
    Vector2 previousPosition;
    public Starship(Texture texture){
        super(texture);
        this.setOrigin(getWidth()/2,getHeight()/2);

        previousPosition = new Vector2(getX(), getY());
    }

    public boolean hasMoved(){
        if(previousPosition.x != getX() || previousPosition.y != getY()){
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }

}