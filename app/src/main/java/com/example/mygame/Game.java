package com.example.mygame;

public class Game {
    private int score;
    private int level;
    private int hp;
    private int shield;
    private String player;

    Game() {
        this.score = 0;
        this.level = 1;
        this.hp = 3;
        this.shield = 0;
        this.player = "Player";
    }

    int getHP() {
        return this.hp;
    }

    int getShield() {
        return this.shield;
    }

    void gainHP(int hp) {
        this.hp += hp;
    }

    void gainShield(int shield) {
        this.shield += shield;
    }

    void loseHP() {
        this.hp--;
    }

    void loseShield() {
        this.shield--;
    }

    int getScore() {
        return this.score;
    }

    void addScore(int score) {
        this.score += score;
    }

    int getLevel() {
        return this.level;
    }

    void addLevel() {
        this.level++;
    }

    String getPlayer() {
        return this.player;
    }

    void startGame() {
        this.hp = 3;
        this.score = 0;
        this.level = 1;
    }
}
