package xyz.volgoak.wordlearning.screens.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import xyz.volgoak.wordlearning.screens.main.MainActivity;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create dbAdapter for load database if it's empty
        // TODO: 1/7/18 init ot create db from here
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
