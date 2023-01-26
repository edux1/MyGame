package com.example.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class HighScoreActivity extends AppCompatActivity {

    SQLManager sql;
    Game game;
    Handler edittextHandler;
    Runnable edittextRunnable;

    void getHighScores() {
        int fix = 0;
        for(int i=0; i<10; i++) {

            int nameID = getResources().getIdentifier("name"+(i+1), "id", getPackageName());
            int levelID = getResources().getIdentifier("level"+(i+1), "id", getPackageName());
            int scoreID = getResources().getIdentifier("score"+(i+1), "id", getPackageName());

            TextView player = (TextView)findViewById(nameID);
            TextView level = (TextView)findViewById(levelID);
            TextView score = (TextView)findViewById(scoreID);
            try {
                if(Integer.parseInt(sql.getScore(i)) <= game.getScore() && fix == 0) {
                    fix = 1;
                }
            }
            catch (Exception e) {
                if(fix == 0) fix = 1;
                else if(fix == 1) fix = 2;
            }

            if(fix == 0) {
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
            else if(fix == 1) {
                edittextHandler = new Handler();
                edittextRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String name = ((EditText)findViewById(R.id.name)).getText().toString();
                        player.setText(name);
                        edittextHandler.postDelayed(edittextRunnable, 50);
                    }
                };
                edittextHandler.postDelayed(edittextRunnable, 50);

                int rankID = getResources().getIdentifier("rank"+(i+1), "id", getPackageName());
                TextView rank = (TextView)findViewById(rankID);
                rank.setTextColor(Color.YELLOW);

                player.setTextColor(Color.YELLOW);
                player.setText(game.getPlayer());
                level.setTextColor(Color.YELLOW);
                level.setText(String.valueOf(game.getLevel()));
                score.setTextColor(Color.YELLOW);
                score.setText(String.valueOf(game.getScore()));
                fix = 2;
            }
            else {
                try {
                    player.setText(sql.getPlayer(i-1));
                    level.setText(sql.getLevel(i-1));
                    score.setText(sql.getScore(i-1));
                }
                catch (Exception e) {
                    player.setText("-");
                    level.setText("-");
                    score.setText("-");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        sql = new SQLManager(this);
        Log.e("--->", "NICE! SQLManager working!");
        game = GameActivity.game;
        TextView tv2 = (TextView)findViewById(R.id.text_2);
        tv2.setText("Your score was " + game.getScore() +
                " at level " + game.getLevel() + "!");

        getHighScores();


        try {
            if(game.getScore() < Integer.parseInt(sql.getScore(9))) {
                TextView tv4 = (TextView)findViewById(R.id.text_4);
                EditText et = (EditText)findViewById(R.id.name);

                tv4.setVisibility(View.GONE);
                et.setVisibility(View.GONE);
            }
        }
        catch (Exception e) { }


        /*findViewById(R.id.btmenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(
                        HighScoreActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });*/

        findViewById(R.id.btplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = ((EditText)findViewById(R.id.name)).getText().toString();
                sql.saveGame(playerName, game.getLevel(), game.getScore());
                Log.e("---> COUNT", String.valueOf(sql.table_size()));
                if(sql.table_size() > 10) {
                    sql.delete_last_row();
                    Log.e("---> COUNT", String.valueOf(sql.table_size()));
                }
                Intent intent = new Intent(
                        HighScoreActivity.this,
                        GameActivity.class);
                startActivity(intent);
            }
        });

    }
}