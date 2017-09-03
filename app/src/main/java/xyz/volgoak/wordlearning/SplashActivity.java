package xyz.volgoak.wordlearning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create dbAdapter for load database if it's empty
        FirebaseAuth.getInstance().signInAnonymously();
        new WordsDbAdapter();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
