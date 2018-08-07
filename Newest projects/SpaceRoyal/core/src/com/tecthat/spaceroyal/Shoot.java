package com.tecthat.spaceroyal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Shoot extends Sprite {
    public static ArrayList<Shoot>shots=new ArrayList<Shoot>();

    Vector2 previousPosition;
    public Shoot(Texture texture, Starship starship){
        super(texture);
        this.setRotation(starship.getRotation());
        if(getRotation()==0) //up ~regular
            this.setPosition(starship.getX()+starship.getWidth()/2,starship.getY()+starship.getHeight());
        else if (getRotation()==90) { //left
            this.setPosition(starship.getX()-starship.getHeight()/2+30,starship.getY()+starship.getWidth()/2+24);
        } else if(getRotation()==180){ //down
            this.setPosition(starship.getX()+starship.getWidth()/2,starship.getY()-starship.getWidth()/2-24);
        } else if(getRotation()==270) //right
            this.setPosition(starship.getX()+starship.getHeight()-24,starship.getY()+starship.getWidth()/2+24);

        previousPosition = new Vector2(getX(), getY());
        shots.add(this);
    }

    public void move(){
        int speed = 100;
        if(getRotation()==0){
            this.setPosition(getX(),getY()+speed);
        } else if (getRotation()==90){
            this.setPosition(getX()-speed,getY());
        } else if(getRotation()==180){
            this.setPosition(getX(),getY()-speed);
        }else if(getRotation()==270){
            this.setPosition(getX()+speed,getY());
        }
    }

}
