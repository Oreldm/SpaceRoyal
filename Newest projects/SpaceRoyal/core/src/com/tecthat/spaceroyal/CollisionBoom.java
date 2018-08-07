package com.tecthat.spaceroyal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class CollisionBoom extends Sprite {
    public static String animationBaseName="Animation_2/Anime_";

    public CollisionBoom(Texture texture, float x, float y){
        super(texture);
        this.setOrigin(this.getWidth()/2,this.getHeight()/2);
        this.setPosition(x,y);
    }

}
