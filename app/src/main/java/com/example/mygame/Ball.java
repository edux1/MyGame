package com.example.mygame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

public class Ball {
    private point c;
    private double r;
    private boolean thereisdestination;
    private point destination;
    private double speed;
    private int type;
    private int color;
    private boolean collided;
    private int boost;

    private static final int PLAYER = 0;
    private static final int ENEMY = 1;
    private static final int BOOST = 2;
    private static final int STATIC = 3;

    private static final int NONE = 0;
    private static final int SPEED = 1;     // 25%
    private static final int SHIELD = 2;    // 24%
    private static final int SIZE = 3;      // 24%
    private static final int HP = 4;        // 25%
    private static final int HP3 = 5;       // 1%
    private static final int SHIELD3 = 6;   // 1%

    Ball(double radius, point center, double speed, int type) {
        this.r = radius;
        this.c = center;
        this.speed = speed;
        this.type = type;
        this.boost = NONE;
        if(type == PLAYER) this.color = Color.BLUE;
        else if(type == ENEMY || type == STATIC) this.color = Color.RED;
        else if(type == BOOST) {
            // Generate a number between 0 - 999
            int random = new Random().nextInt(100);
            if(random < 25) {
                this.boost = SPEED;
                this.color = Color.CYAN;
            }
            else if(random < 49) {
                this.boost = SHIELD;
                this.color = Color.rgb(220,220,220);
            }
            else if(random < 73) {
                this.boost = SIZE;
                this.color = Color.rgb(255,191,0);
            }
            else if(random < 98) {
                this.boost = HP;
                this.color = Color.rgb(143,188,143);
            }
            else if(random < 99) {
                this.boost = HP3;
                this.color = Color.GREEN;
            }
            else if(random < 100) {
                this.boost = SHIELD3;
                this.color = Color.rgb(105,105,105);
            }


        }
        thereisdestination = false;
        collided = false;
    }

    void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.color);
        canvas.drawCircle((float)c.x, (float)c.y, (float)r, paint);
    }

    void setDestination(point p) {
        //Log.e("---> X: ", Double.toString(p.x));
        //Log.e("---> Y: ", Double.toString(p.y));
        if(type == PLAYER) {
            if(p.x < r) p.x = r;
            else if(p.x > 4-r) p.x = 4-r;
            if(p.y < -2+r) p.y = -2+r;
            else if(p.y > 4-r) p.y = 4-r;
        }
        destination = p;
        thereisdestination = true;
    }

    void unsetDestination() {
        thereisdestination = false;
    }

    void moveAux(double delta) {
        if(!thereisdestination) return;
        double dist = delta * speed;
        if(point.dist(c, destination) < dist) {
            c = destination;
            return;
        }
        point dir = point.unitary(point.sub(destination, c));
        c = point.sum(c, point.prod(dist, dir));
    }

    void move(double delta) {
        double dist = delta * speed;
        int steps = (int) Math.floor(dist/(r/4))+1;
        for (int i=0; i<steps; i++)
            moveAux(delta/steps);
    }

    boolean collision(Ball b) {
        // This calculate if two circles overlap
        double distance = Math.sqrt(Math.pow(this.c.x - b.c.x, 2) + Math.pow(this.c.y - b.c.y, 2));
        if( (distance <= this.r - b.r) || (distance <= b.r - this.r) || (distance < this.r + b.r)) {
            return true;
        }
        return false;
    }

    void setColor(int color) {
        this.color = color;
    }

    int getColor() {
        return this.color;
    }

    void setCollided(boolean collided) {
        this.collided = collided;
    }

    boolean getCollided() {
        return this.collided;
    }

    int getBoost() {
        return this.boost;
    }

    double getSpeed() {
        return this.speed;
    }

    void giveSpeed(double speed) {
        this.speed += speed;
    }

    void reduceRadius(double radius) {
        this.r -= radius;
    }

    double getSize() {
        return this.r;
    }
}
