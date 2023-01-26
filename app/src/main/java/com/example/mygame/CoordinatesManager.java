package com.example.mygame;

import android.util.Log;

public class CoordinatesManager {
    int size;
    // Camera location and orientation
    point O,OX,OY;
    Ball ball;

    int numfingers = 0;
    point worldfinger;

    void setCamera(point O, point OX) {
        this.O = O;
        this.OX = OX;
        OY = new point(-OX.y, OX.x);
    }

    point veccanonic2vecscreen(point v) {
        return new point(v.x*size/2.0, -v.y*size/2.0);
    }

    point vecscreen2veccanonic(point v) {
        return new point(v.x/(size/2.0), -v.y/(size/2.0));
    }

    point canonic2screen(point p) {
        return veccanonic2vecscreen(point.sum(p, new point(1, -1)));
    }

    point screen2canonic(point p) {
        return point.sub(vecscreen2veccanonic(p), new point(1,-1));
    }

    point vecworld2veccamera(point v) {
        return new point(
                point.dotProd(v, OX)/point.norm(OX),
                point.dotProd(v, OY)/point.norm(OY)
        );
    }

    point veccamera2vecworld(point v) {
        return point.sum(
                point.prod(v.x, OX),
                point.prod(v.y, OY)
        );
    }

    point world2camera(point p) {
        return vecworld2veccamera(point.sub(p,O));
    }

    point camera2world(point p) {
        return point.sum(veccamera2vecworld(p),O);
    }

    point world2screen(point p) {
        return canonic2screen(world2camera(p));
    }

    point screen2world(point p) {
        return camera2world(screen2canonic(p));
    }

    point vecworld2vecscreen(point p) {
        return veccanonic2vecscreen(vecworld2veccamera(p));
    }

    point vecscreen2vecworld(point p) {
        return veccamera2vecworld(vecscreen2veccanonic(p));
    }

    void touch() {
        numfingers = 0;
        ball.unsetDestination();
    }

    void touch(point screenfinger) {
        point newworldfinger = screen2world(screenfinger);
        if(numfingers != 1) {
            numfingers = 1;
            worldfinger = newworldfinger;
        }
        else {
            ball.setDestination(worldfinger);
            worldfinger = newworldfinger;
        }
    }
}
