package com.study.bugs.logic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.View;

import com.study.bugs.R;
import com.study.bugs.Utils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameField extends View {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final int BUGS_ON_THE_FIELD = 7;
    private final int SPEED = 10;
    private final float BUGS_X_SIZE = 160f;
    private final float BUGS_Y_SIZE = 160f;
    private int SCREEN_HEIGHT, SCREEN_WIDTH;

    private MediaPlayer mp;
    private int score = 0;
    private int record;

    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private Set<Bug> bugs = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public GameField(Context context) {

        super(context);
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.setBackgroundResource(R.drawable.bg_game);
        this.SCREEN_HEIGHT = displayMetrics.heightPixels - 200;
        this.SCREEN_WIDTH = displayMetrics.widthPixels;
        this.record = Utils.loadSettings("record", context);

        engine();
        clickListener(context);
    }

    private void clickListener(Context context) {
        this.setOnTouchListener((v, event) -> {
            boolean isMiss = true;

            for (Bug bug : bugs) {
                if (clickCollision(event.getX(), event.getY(), bug) && !bug.dead) {
                    score++;
                    bug.dead = true;
                    isMiss = false;
                    playSound(R.raw.crunch, context);

                    if (score > record) {
                        record = score;
                        Utils.saveSettings(record, this.getContext());
                    }

                    break;
                }
            }
            if (isMiss) {
                playSound(R.raw.miss, context);
                if (score > 0)
                    score--;
            }

            return false;
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawScore(canvas);
        drawBugs(canvas);
        invalidate();
    }

    private void playSound(int sound, Context context) {
        mp = MediaPlayer.create(context, sound);
        mp.setOnCompletionListener(mpl -> mpl.release());
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = MediaPlayer.create(context, sound);
            mp.setOnCompletionListener(mpl -> mpl.release());
        }
        mp.start();
    }

    private void engine() {
        scheduler.scheduleAtFixedRate(() -> {
            if (bugs.size() < BUGS_ON_THE_FIELD) {
                Bug bug = getBug();
                bugs.add(bug);
                scheduler.scheduleAtFixedRate(() -> {
                    if (!bug.dead) {
                        if (!checkCollision(bug)) {
                            bug.posX += bug.dirX;
                            bug.posY += bug.dirY;
                        }
                    } else {
                        bug.image = R.drawable.bug_dead;
                        scheduler.schedule(() -> {
                            bugs.remove(bug);
                        }, 800, TimeUnit.MILLISECONDS);
                    }
                }, 0, SPEED, TimeUnit.MILLISECONDS);
            }
        }, 0, 700, TimeUnit.MILLISECONDS);
    }

    private void drawScore(Canvas canvas) {
        Paint p1 = new Paint();
        p1.setTextSize(60);
        canvas.drawText("SCORE : " + score, 10, 50, p1);
    }

    public void drawBugs(Canvas c) {
        for (Bug bug : bugs) {
            Bitmap b = BitmapFactory.decodeResource(getResources(), bug.image);
//            float angle = 0;
//            if (bug.dirX > 0 && bug.dirY > 0) {
//                bug.angle = 180;
//            }
//            if (bug.dirX < 0 && bug.dirY < 0) {
//                bug.angle = -90;
//            }
//            if (bug.dirX < 0 && bug.dirY > 0) {
//                bug.angle = -180;
//            }
//            if (bug.dirX > 0 && bug.dirY < 0) {
//                bug.angle = 90;
//            }
            Matrix matrix = new Matrix();
            matrix.postScale(0.3f, 0.3f);
            matrix.postTranslate(bug.posX, bug.posY);
            c.drawBitmap(b, matrix, null);
        }
    }

    private boolean respawnCollision(float x, float y, Bug unit) {

        if (unit != null &&
                x < (unit.posX + BUGS_Y_SIZE) &&
                (x + BUGS_X_SIZE) > unit.posX &&
                y < (unit.posY + BUGS_Y_SIZE) &&
                (y + BUGS_Y_SIZE) > unit.posY) {
            return true;
        }

        if (y + BUGS_Y_SIZE >= SCREEN_HEIGHT) {
            return true;
        }
        if (x + BUGS_X_SIZE >= SCREEN_WIDTH) {
            return true;
        }
        if (x <= 0) {
            return true;
        }
        if (y <= 0) {
            return true;
        }

        return false;
    }

    private boolean bugCollision(Bug bug1, Bug bug2) {

        if (bug1.posX + bug1.dirX < bug2.posX + BUGS_Y_SIZE &&
                bug1.posX + bug1.dirX + BUGS_Y_SIZE > bug2.posX &&
                bug1.posY + bug1.dirY < bug2.posY + BUGS_Y_SIZE &&
                bug1.posY + bug1.dirY + BUGS_Y_SIZE > bug2.posY && !bug1.equals(bug2)) {
            return true;
        }
        return false;
    }

    private boolean checkCollision(Bug in_bug) {
        for (Bug bug : bugs) {
            if (bugCollision(in_bug, bug)) {
                double tempX = in_bug.dirX;
                double tempY = in_bug.dirY;
                in_bug.dirX = bug.dirX;
                in_bug.dirY = bug.dirY;
                bug.dirX = tempX;
                bug.dirY = tempY;
                return true;
            }
        }

        if (in_bug.posY + BUGS_Y_SIZE + in_bug.dirY >= SCREEN_HEIGHT) {
            in_bug.dirY = in_bug.dirY >= 0 ? in_bug.dirY * -1 : in_bug.dirY;
            return true;
        }
        if (in_bug.posX + BUGS_X_SIZE + in_bug.dirX >= SCREEN_WIDTH) {
            in_bug.dirX = in_bug.dirX >= 0 ? in_bug.dirX * -1 : in_bug.dirX;
            return true;
        }
        if (in_bug.posX + in_bug.dirX <= 0) {
            in_bug.dirX = in_bug.dirX <= 0 ? in_bug.dirX * -1 : in_bug.dirX;
            return true;
        }
        if (in_bug.posY + in_bug.dirY <= 0) {
            in_bug.dirY = in_bug.dirY <= 0 ? in_bug.dirY * -1 : in_bug.dirY;
            return true;
        }
        return false;
    }

    private boolean clickCollision(float x, float y, Bug unit) {
        if (x < unit.posX + BUGS_Y_SIZE &&
                x > unit.posX &&
                y < unit.posY + BUGS_Y_SIZE &&
                y > unit.posY) {
            return true;
        }
        return false;
    }

    private Bug getBug() {

        float posX = (float) (Math.random() * SCREEN_WIDTH);
        float posY = (float) (Math.random() * SCREEN_HEIGHT);

        if (bugs.size() == 0) {
            if (respawnCollision(posX, posY, null)) {
                return getBug();
            } else {
                return new Bug(posX, posY);
            }
        }

        for (Bug bug : bugs) {
            if (respawnCollision(posX, posY, bug)) {
                return getBug();
            }
        }
        return new Bug(posX, posY);
    }

}
