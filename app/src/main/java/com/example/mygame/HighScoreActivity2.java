package com.example.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class HighScoreActivity2 extends AppCompatActivity {

    SQLManager sql;

    void getHighScores() {
        for(int i=0; i<10; i++) {

            int nameID = getResources().getIdentifier("name_"+(i+1), "id", getPackageName());
            int levelID = getResources().getIdentifier("level_"+(i+1), "id", getPackageName());
            int scoreID = getResources().getIdentifier("score_"+(i+1), "id", getPackageName());

            TextView player = (TextView)findViewById(nameID);
            TextView level = (TextView)findViewById(levelID);
            TextView score = (TextView)findViewById(scoreID);

            try {
                player.setText(sql.getPlayer(i));
                level.setText(sql.getLevel(i));
                score.setText(sql.getScore(i));
            }
            catch (Exception e) {
                player.setText("-");
                level.setText("-");
                score.setText("-");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score2);

        sql = new SQLManager(this);
        getHighScores();

        findViewById(R.id.btback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        HighScoreActivity2.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
    }
}