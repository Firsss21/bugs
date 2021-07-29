package com.study.bugs.logic;

import androidx.annotation.NonNull;

import com.study.bugs.R;


public class Bug {
    float posX;
    float posY;
    boolean dead;
    double dirX;
    double dirY;
    int image;

    public Bug(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        this.image = getRandImage();
        getNewDirection();
    }

    private int getRandImage() {
        int rand = (int) (Math.random() * 10);
        int image = R.drawable.bug;
        image = rand > 0 ? R.drawable.bug : image;
        image = rand > 2 ? R.drawable.bug_bl : image;
        image = rand > 4 ? R.drawable.bug_br : image;
        image = rand > 6 ? R.drawable.bug_dred : image;
        image = rand > 8 ? R.drawable.bug_gr : image;
        return image;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bug: posX = " + this.posX + ", posY = " + this.posY + ", dead = " + dead;
    }

    private void getNewDirection() {
        this.dirX = -4 + Math.random() * (8 + 4);
        this.dirY = -4 + Math.random() * (8 + 4);
    }

}