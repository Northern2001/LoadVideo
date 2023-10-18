package com.galaxy.loadviewdemo;

import android.graphics.Bitmap;

public class TypePosition {
    private int position;
    private Bitmap dotBitmap;

    public TypePosition(int position, Bitmap dotBitmap) {
        this.position = position;
        this.dotBitmap = dotBitmap;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Bitmap getDotBitmap() {
        return dotBitmap;
    }

    public void setDotBitmap(Bitmap dotBitmap) {
        this.dotBitmap = dotBitmap;
    }
}
