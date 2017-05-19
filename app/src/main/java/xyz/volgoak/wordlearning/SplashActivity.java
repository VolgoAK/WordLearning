package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create dbAdapter for load database if it's empty
        new WordsDbAdapter();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}