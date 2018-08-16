package com.mygdx.game;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class StoreActivity extends Activity implements BillingProcessor.IBillingHandler{
    // TO CLEAR : adb shell pm clear com.android.vending
    // [Comment for OREL] : I HAVE ADB HERE : C:\Program Files (x86)\LG Electronics\LG PC Suite\adb

    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        bp=new BillingProcessor(this,null, this);
        Button buttonOne = (Button) findViewById(R.id.winBtn);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bp.purchase(StoreActivity.this, "android.test.purchased");


            }
        });
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        try { FileHandler.createFileIfNotExists(); }catch(Exception e){}
        String fileStr= FileHandler.readFromFile(this);
        if(fileStr.length()==0){
            FileHandler.writeToFile("1,0",this);
        }else{
            String wins=fileStr.substring(0,fileStr.indexOf(","));
            String lose=fileStr.substring(fileStr.indexOf(",")+1,fileStr.length());
            Integer winInt=Integer.parseInt(wins);
            Integer loseInt=Integer.parseInt(lose);
            winInt++;
            FileHandler.writeToFile(winInt+","+loseInt,this);
        }

        Toast.makeText(this,"You've purchased win!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this,"Purchase error accured",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
} //
