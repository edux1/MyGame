package com.example.mygame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    LinearLayout linlay;
    SQLManager sql;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sql = new SQLManager(this);

        linlay = new LinearLayout(this);
        linlay.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setGravity(Gravity.CENTER_HORIZONTAL);

        Button gamebt = new Button(this);
        gamebt.setText("PLAY");
        gamebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        GameActivity.class);
                startActivity(intent);
            }
        });

        Button hsbt = new Button(this);
        hsbt.setText("HIGH SCORE");
        hsbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        HighScoreActivity2.class);
                startActivity(intent);
            }
        });

        Button delete = new Button(this);
        delete.setText("DELETE ALL DATA");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sql.delete_table();
            }
        });

        linlay.addView(gamebt);
        linlay.addView(hsbt);
        linlay.addView(delete);

        setContentView(linlay);
    }
}