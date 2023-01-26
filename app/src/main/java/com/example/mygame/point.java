package com.example.mygame;

import android.util.Log;

public class point {
    double x, y;
    point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    static point sum(point p1, point p2) {
        return new point(p1.x+p2.x, p1.y+p2.y);
    }
    static point sub(point p1, point p2) {
        return new point(p1.x-p2.x, p1.y-p2.y);
    }
    static point prod(double a, point p) {
        return new point(a*p.x, a*p.y);
    }
    static point div(point p, double d) {
        return new point(p.x/d, p.y/d);
    }
    static point polar(double r, double a) {
        return new point(r*Math.cos(a), r*Math.sin(a));
    }

    static double norm(point p) {
        return p.x*p.x+p.y*p.y;
    }
    static double modulo(point p) {
        return Math.sqrt(norm(p));
    }
    static double dist(point p1, point p2) {
        return modulo(sub(p1, p2));
    }
    static point unitary(point p) {
        return point.div(p,point.modulo(p));
    }
    static double dotProd(point p1, point p2) {
        return p1.x*p2.x + p1.y*p2.y;
    }
    static double crossProd(point p1, point p2) {
        return p1.x*p2.y - p1.y*p2.x;
    }

    static point vecToReference(point v, point OX, point OY) {
        return new point(
                point.dotProd(v, OX)/point.norm(OX),
                point.dotProd(v, OY)/point.norm(OY)
        );
    }
    static point vecFromReference(point v, point OX, point OY) {
        return point.sum(
                point.prod(v.x, OX),
                point.prod(v.y, OY)
        );
    }
    static point pointToReference(point p, point O, point OX, point OY) {
        return vecToReference(point.sub(p,O), OX, OY);
    }
    static point pointFromReference(point p, point O, point OX, point OY) {
        return point.sum(vecFromReference(p, OX, OY),O);
    }

    static point orthogonal(point p) {
        return new point(-p.y, p.x);
    }
    static point moveBetweenReferences(point p,
                                       point o1, point ox1, point oy1,
                                       point o2, point ox2, point oy2) {
        return point.pointFromReference(point.pointToReference
                (p,o1,ox1,oy1), o2,ox2,oy2);
    }
}
