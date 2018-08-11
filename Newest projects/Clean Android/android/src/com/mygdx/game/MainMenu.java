package com.mygdx.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity {
    Button startGame;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        startGame = (Button)findViewById(R.id.btn_play);

        // handle set start click
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, AndroidLauncher.class);
                MainMenu.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        HealthBar.HP=100;
        MyGdxGame.isFirstTime=true;
        MyGdxGame.loops =0;
    }
}
