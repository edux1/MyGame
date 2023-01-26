package com.example.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    LinearLayout linlay;
    ImageView imageView;
    TextView lvltext, infoText, boostText;
    Handler textHandler, ballHandler, lvlHandler, checkHandler, collisionHandler, waitingHandler, boostHandler;
    Runnable textRunnable, ballRunnable, lvlRunnable, checkRunnable, collisionRunnable, waitingRunnable, boostRunnable;
    int width, height, size, countdown = 3;
    Bitmap bitmap, space;
    Canvas canvas;
    Ball ball;
    CoordinatesManager cm;
    long curmilliseconds;
    BallsManager bm, statics, boost;
    State state = State.WAITING;
    int blinkcounter = 0;
    Button menu;

    public static Game game;

    public enum State {
        WAITING,
        STARTING_LVL,
        PLAYING_LVL,
        PREPARING_NEXT_LVL,
        ENDED
    }

    public static final int PLAYER = 0;
    public static final int ENEMY = 1;
    public static final int BOOST = 2;
    public static final int STATIC = 3;

    public static final int LEVEL = 7;
    public static final int BALLS = 30;

    private static final int SPEED = 1;     // 25%
    private static final int SHIELD = 2;    // 24%
    private static final int SIZE = 3;      // 24%
    private static final int HP = 4;        // 25%
    private static final int HP3 = 5;       // 1%
    private static final int SHIELD3 = 6;   // 1%

    private static final double SPEEDBOOST = 0.3;
    private static final int SHIELDBOOST = 1;
    private static final double SIZEBOOST = 0.01;
    private static final int HPBOOST = 1;
    private static final int HP3BOOST = 3;
    private static final int SHIELD3BOOST = 3;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("--->", "BACK BUTTON");
            checkHandler.removeCallbacks(checkRunnable);
            waitingHandler.removeCallbacks(waitingRunnable);
            ballHandler.removeCallbacks(ballRunnable);
            boostHandler.removeCallbacks(boostRunnable);
            lvlHandler.removeCallbacks(lvlRunnable);
            Intent intent = new Intent(
                    GameActivity.this,
                    MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    void draw() {
        canvas.drawColor(Color.YELLOW);
        Matrix matrix = new Matrix();
        point o = cm.world2screen(new point(0,0));
        point ox = cm.vecworld2vecscreen(new point(1,0));
        point oy = cm.vecworld2vecscreen(new point(0,1));
        matrix.setValues(new float[]
                {
                        (float)ox.x,(float)oy.x,(float)o.x,
                        (float)ox.y,(float)oy.y,(float)o.y,
                        0,0,1
                });
        canvas.save();
        canvas.setMatrix(matrix);
        ball.draw(canvas);
        bm.draw(canvas);
        statics.draw(canvas);
        boost.draw(canvas);
        canvas.restore();
        imageView.invalidate();
    }

    void generateBall() {
        // Create a random radius between 0.5 - 0.1

        double radius = new Random().nextDouble();
        double maxRadius = 0.5;
        double minRadius = 0.1 + (0.025 * (game.getLevel()-1));
        if (minRadius >= maxRadius) minRadius = maxRadius;
        radius = minRadius + (maxRadius - minRadius) * radius;

        // Create a random x between (4.0 - radius) - (radius)
        double xOrigin = new Random().nextDouble();
        double xMax = 4.0;
        double xMin = 0.0;
        xOrigin = xMin + (xMax - xMin ) * xOrigin;

        double y = 6.0;
        double speed = 5.0 + game.getLevel() * 0.1;

        bm.add(new Ball(radius, new point(xOrigin,y), speed, ENEMY));

        double xDestination = new Random().nextDouble();
        xDestination = radius + ((xMax - radius) - radius ) * xDestination;
        bm.getBall(bm.size()-1).setDestination(new point(xDestination, -4.0));
        game.addScore(100);

        if(game.getLevel() >= LEVEL) {
            double rad = new Random().nextDouble();
            double minRad = 0.1 + (0.025 * (game.getLevel()-1));
            if(minRad >= maxRadius) minRad = maxRadius;
            rad = minRad + (maxRadius - minRad) * rad;

            double yOrigin = new Random().nextDouble();
            double yMax = 6.0;
            double yMin = 0.0;
            yOrigin = (yMin + (yMax - yMin) * yOrigin) - 2.0;

            double x = 6.0;

            bm.add(new Ball(rad, new point(x, yOrigin), speed, ENEMY));

            double yDestination = new Random().nextDouble();
            yDestination = (rad + ((yMax - rad) - rad) * yDestination) - 2.0;
            bm.getBall(bm.size()-1).setDestination(new point(-1.0, yDestination));
            game.addScore(150);
        }

    }

    void generateBoostBall() {
        double radius = 0.2;

        double x1 = 1.0, x2 = 2.0, x3 = 3.0;
        double yOrigin = 6.0, yDestination = -4.0;
        double speed = 3.0;

        boost.add(new Ball(radius, new point(x1,yOrigin), speed, BOOST));
        boost.add(new Ball(radius, new point(x2,yOrigin), speed, BOOST));
        boost.add(new Ball(radius, new point(x3,yOrigin), speed, BOOST));


        boost.getBall(0).setDestination(new point(x1, yDestination));
        boost.getBall(1).setDestination(new point(x2, yDestination));
        boost.getBall(2).setDestination(new point(x3, yDestination));
    }

    void collided(Ball b) {
        collisionHandler.postDelayed(collisionRunnable, 0);
        if (game.getShield() > 0)
            game.loseShield();
        else
            game.loseHP();

        if(game.getHP() == 0) {
            gameOver();
            Log.e("GAME OVER", "GAME OVER BRO");
            Log.e("STATE", String.valueOf(state));
            checkHandler.removeCallbacks(checkRunnable);
            waitingHandler.removeCallbacks(waitingRunnable);
            ballHandler.removeCallbacks(ballRunnable);
            boostHandler.removeCallbacks(boostRunnable);
            lvlHandler.removeCallbacks(lvlRunnable);
            Intent intent = new Intent(
                    GameActivity.this,
                    HighScoreActivity.class);
            startActivity(intent);
        }
    }

    void gameOver(){
        infoText.setText("HP: " + game.getHP() + "\t\t\t Shield: " + game.getShield() +
                "\t\t\t Size: " + String.format("%.2f", ball.getSize()) + "\t\t\t Speed: " + String.format("%.2f", ball.getSpeed()) +
                "\t\t\t Score: " + game.getScore());
        lvltext.setText("Level " + game.getLevel() + ". Game Over!");
        bm.clear();
        state = State.ENDED;
    }

    void check_collisions() {
        collisionHandler = new Handler();
        collisionRunnable = new Runnable() {
            @Override
            public void run() {
                if(ball.getColor() == Color.BLUE) ball.setColor(Color.TRANSPARENT);
                else {
                    ball.setColor(Color.BLUE);
                    blinkcounter++;
                    if(blinkcounter == 10) {
                        ball.setCollided(false);
                        blinkcounter = 0;
                    }
                }
                if(ball.getCollided() == true) collisionHandler.postDelayed(collisionRunnable, 100);
            }
        };

        if(bm.size() > 0)
            for(int i=0; i< bm.size(); i++)
                if(ball.collision(bm.getBall(i)))
                    if(ball.getCollided() != true) {
                        ball.setCollided(true);
                        collided(ball);
                    }

        if(boost.size() > 0)
            for(int i=0; i<boost.size(); i++)
                if(ball.collision(boost.getBall(i))) {
                    int boostObtained = boost.getBall(i).getBoost();
                    if(boostObtained == SPEED) boostText.setText("Speed boost obtained. +"+SPEEDBOOST+" speed!");
                    else if(boostObtained == SHIELD) boostText.setText("Shield boost obtained. +"+SHIELDBOOST+" shield!");
                    else if(boostObtained == SIZE) boostText.setText("Size boost obtained. -"+SIZEBOOST+" size!");
                    else if(boostObtained == HP) boostText.setText("HP boost obtained. +"+HPBOOST+" HP!");
                    else if(boostObtained == HP3) boostText.setText("HP boost obtained. +"+HP3BOOST+" HP!");
                    else if(boostObtained == SHIELD3) boostText.setText("Shield boost obtained. +"+SHIELD3BOOST+" shield!");

                    giveBoost(boostObtained);
                    boost.clear();
                    textHandler.postDelayed(textRunnable, 3000);

                }
    }

    void giveBoost(int boost) {
        switch (boost) {
            case SPEED:
                ball.giveSpeed(SPEEDBOOST);
                break;
            case SHIELD:
                if(game.getShield() < 3)
                    game.gainShield(SHIELDBOOST);
                else {
                    boostText.setText("Maximum Shield Points! Can't get more shield.");
                    textHandler.postDelayed(textRunnable, 3000);
                }
                break;
            case SIZE:
                if(ball.getSize() > 0.03)
                    ball.reduceRadius(SIZEBOOST);
                else {
                    boostText.setText("Minimum Size Points! Can't get smaller.");
                    textHandler.postDelayed(textRunnable, 3000);
                }
                break;
            case HP:
                if(game.getHP() < 5)
                    game.gainHP(HPBOOST);
                else {
                    boostText.setText("Maximum HP! Can't get more HP.");
                    textHandler.postDelayed(textRunnable, 3000);
                }
                break;
            case HP3:
                if(game.getHP() > 2) {
                    game.gainHP(5-game.getHP());
                    boostText.setText("Maximum HP! Can't get more than 5 HP.");
                    textHandler.postDelayed(textRunnable, 3000);
                }
                else {
                    game.gainHP(HP3BOOST);
                }
                break;
            case SHIELD3:
                if(game.getShield() > 0) {
                    game.gainShield(3-game.getShield());
                    boostText.setText("Maximum Shield Points! Can't get more than 3 Shield Points.");
                    textHandler.postDelayed(textRunnable, 4000);
                }
                else
                    game.gainShield(SHIELD3BOOST);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linlay = new LinearLayout(this);
        linlay.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setGravity(Gravity.CENTER);
        imageView = new ImageView(this);
        linlay.addView(imageView);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        width = p.x;
        height = p.y;
        size = (int)(width*0.9);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(size,(int)(size*1.5)));
        bitmap = Bitmap.createBitmap(size, (int)(size*1.5), Bitmap.Config.ARGB_8888);
        //space = BitmapFactory.decodeFile("../../../../res/drawable/space.png");
        imageView.setImageBitmap(bitmap);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
        bm = new BallsManager();
        statics = new BallsManager();
        boost = new BallsManager();
        game = new Game();

        cm = new CoordinatesManager();
        ball = new Ball(0.2, new point(0.5,0.5), 3.0, PLAYER);
        statics.add(new Ball(0.2, new point(2.0, 4.0), 0.0, STATIC));
        cm.ball = ball;
        cm.size = size;
        cm.setCamera(new point(2,2),
                new point(Math.max(2.0, 2.0), 0));

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP ||
                        (event.getPointerCount() != 1 && event.getPointerCount() != 2)) {
                    cm.touch();
                }
                else if (event.getPointerCount() == 1) {
                    cm.touch(new point(event.getX(), event.getY()));
                }
                draw();
                return true;
            }
        });

        draw();

        curmilliseconds = System.currentTimeMillis();
        ballHandler = new Handler();
        ballRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("ballRunnable", "STILL RUNNING");
                long nextcurmilliseconds = System.currentTimeMillis();
                double delta = (nextcurmilliseconds-curmilliseconds)/1000.0;
                curmilliseconds = nextcurmilliseconds;
                ball.move(delta);
                for(int i=0; i<bm.size(); i++)
                    bm.getBall(i).move(delta);
                for(int i=0; i<boost.size(); i++)
                    boost.getBall(i).move(delta);
                draw();
                ballHandler.postDelayed(ballRunnable, 10);
            }
        };
        ballHandler.postDelayed(ballRunnable, 10);

        infoText = new TextView(this);
        infoText.setText("HP: " + game.getHP() + "\t\t\t Shield: " + game.getShield() +
                "\t\t\t Size: " + String.format("%.2f", ball.getSize()) + "\t\t\t Speed: " + String.format("%.2f", ball.getSpeed()) +
                "\t\t\t Score: " + game.getScore());
        infoText.setGravity(Gravity.CENTER_HORIZONTAL);
        linlay.addView(infoText);

        boostText = new TextView(this);
        boostText.setText("");
        boostText.setGravity(Gravity.CENTER_HORIZONTAL);
        linlay.addView(boostText);

        textHandler = new Handler();
        textRunnable = new Runnable() {
            @Override
            public void run() {
                boostText.setText("");
            }
        };

        lvltext = new TextView(this);
        lvltext.setText("Ready to play?");
        lvltext.setGravity(Gravity.CENTER_HORIZONTAL);
        linlay.addView(lvltext);

        checkHandler = new Handler();
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("checkRunnable", String.valueOf(state));
                check_collisions();
                if(state != State.ENDED) {
                    Log.e("State", String.valueOf(state));
                    infoText.setText("HP: " + game.getHP() + "\t\t\t Shield: " + game.getShield() +
                            "\t\t\t Size: " + String.format("%.2f", ball.getSize()) + "\t\t\t Speed: " + String.format("%.2f", ball.getSpeed()) +
                            "\t\t\t Score: " + game.getScore());
                    checkHandler.postDelayed(checkRunnable, 10);
                }
            }
        };

        waitingHandler = new Handler();
        waitingRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("waitingRunnable", "STILL RUNNING");
                statics.draw(canvas);

                if(state == State.STARTING_LVL) {
                    if(statics.listballs.get(0).getColor() != Color.TRANSPARENT) {
                        statics.changeColor(Color.TRANSPARENT);
                    }
                    else {
                        statics.changeColor(Color.RED);
                    }
                    waitingHandler.postDelayed(waitingRunnable, 300);
                }
                else if(state != State.ENDED){
                    statics.changeColor(Color.TRANSPARENT);
                    waitingHandler.postDelayed(waitingRunnable, 1000);
                }

            }
        };

        boostHandler = new Handler();
        boostRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("boostRunnable", "STILL RUNNING");
                boost.draw(canvas);

                if(state == State.PREPARING_NEXT_LVL)
                    boostHandler.postDelayed(boostRunnable, 10);
                else if(state != State.ENDED)
                    boostHandler.postDelayed(boostRunnable, 1000);
            }
        };


        lvlHandler = new Handler();
        lvlRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("lvlRunnable", "STILL RUNNING");
                if(state == State.STARTING_LVL) {
                    lvltext.setText("Level " + game.getLevel() + ". Starting in " + countdown + " seconds.");
                    countdown--;
                    if(countdown == 0) {
                        boost.clear();
                        lvltext.setText("Level " + game.getLevel() + ". Good luck!");

                        state = State.PLAYING_LVL;
                        countdown = 3;
                    }
                    lvlHandler.postDelayed(lvlRunnable, 1000);
                    checkHandler.postDelayed(checkRunnable, 1000);
                    boostHandler.postDelayed(boostRunnable, 10);

                }
                else if(state == State.PLAYING_LVL) {
                    if(bm.size() < BALLS + 3 * game.getLevel()) {
                        generateBall();
                        infoText.setText("HP: " + game.getHP() + "\t\t\t Shield: " + game.getShield() +
                                "\t\t\t Size: " + String.format("%.2f", ball.getSize()) + "\t\t\t Speed: " + String.format("%.2f", ball.getSpeed()) +
                                "\t\t\t Score: " + game.getScore());
                        lvlHandler.postDelayed(lvlRunnable, 500);
                    }
                    else {
                        state = State.PREPARING_NEXT_LVL;
                        lvlHandler.postDelayed(lvlRunnable, 1500);
                    }

                }
                else if(state == State.PREPARING_NEXT_LVL){
                    bm.clear();
                    generateBoostBall();
                    lvltext.setText("Good Job! You finished level " + game.getLevel() + ". Choose a boost!");
                    state = State.STARTING_LVL;
                    game.addLevel();
                    if(game.getLevel() == LEVEL)
                        statics.add(new Ball(0.2, new point(4.0, 1.0), 0.0, STATIC));
                    lvlHandler.postDelayed(lvlRunnable, 5000);
                }
            }
        };

        Button bt = new Button(this);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);

                if (state == State.WAITING) {
                    state = State.STARTING_LVL;
                    game.startGame();
                    lvlHandler.postDelayed(lvlRunnable, 10);
                    waitingHandler.postDelayed(waitingRunnable, 10);
                }
                else if (state != State.ENDED){
                    textHandler.postDelayed(textRunnable, 1000);
                }
            }
        });
        bt.setText("START");
        linlay.addView(bt);

        menu = new Button(this);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        GameActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
        menu.setText("MENU");
        linlay.addView(menu);

        setContentView(linlay);
    }
}