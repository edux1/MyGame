package com.example.mygame;

import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;

public class BallsManager {
    ArrayList<Ball> listballs;

    BallsManager() {
        listballs = new ArrayList<Ball>();
    }

    void add(Ball ball) {
        listballs.add(ball);
    }

    void draw(Canvas canvas) {
        for (int i = 0; i < listballs.size(); i++)
            listballs.get(i).draw(canvas);
    }

    void changeColor(int color) {
        for(int i = 0; i < listballs.size(); i++)
            listballs.get(i).setColor(color);
    }

    Ball getBall(int i) {
        return listballs.get(i);
    }

    int size() {
        return listballs.size();
    }

    void clear() {
        listballs.clear();
    }
}
