package com.ahmad.ahmad_hajyahya_hw1;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private AppCompatImageView[] obstacles_IMG_hearts;
    private MaterialButton obstacles_BTN_right;
    private MaterialButton obstacles_BTN_left;

    private AppCompatImageView obstacles_IMG_car;
    private AppCompatImageView obstacle;
    private AppCompatImageView obstacle1;
    private int carLane = 1;
    private RelativeLayout[][] cells;
    private int obsCurrentRow = 0;
    private int obsCurrentCol = 0;
    private int obs1CurrentRow = 0;
    private int obs1CurrentCol = 0;
    private Random random;
    private Handler handler ;
    private Handler obsHandler ;
    private Handler obs1Handler ;
    private Runnable runnable;
    private Runnable obsRunnable;
    private Runnable obs1Runnable;
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = new GameManager(this);
        cells = new RelativeLayout[3][3];
        random = new Random();
        handler = new Handler();
        obsHandler = new Handler();
        obs1Handler = new Handler();
        findViews();

        obstacles_IMG_car = new AppCompatImageView(this);
        obstacles_IMG_car.setImageResource(R.drawable.car);
        obstacles_IMG_car.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        cells[2][1].addView(obstacles_IMG_car);

        obstacle = new AppCompatImageView(this);
        obstacle.setImageResource(R.drawable.obstacle);
        obstacle.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        obstacle1 = new AppCompatImageView(this);
        obstacle1.setImageResource(R.drawable.obstacle);
        obstacle1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        obstacles_BTN_right.setOnClickListener(v -> moveRight());
        obstacles_BTN_left.setOnClickListener(v -> moveLeft());

        startGame();
    }

    private void startGame() {
        runnable = new Runnable() {
            @Override
            public void run() {
                randomObstacle();
                handler.postDelayed(this, 3000);


            }
        };
        obsRunnable = new Runnable() {
            @Override
            public void run() {
                moveObstacle();
                moveObstacle1();
                obsHandler.postDelayed(this, 1000);

            }
        };
        obs1Runnable= new Runnable() {
            @Override
            public void run() {
                randomObstacle1();
                handler.postDelayed(this, 2800);
            }
        };
        handler.post(runnable);
        obsHandler.post(obsRunnable);
        obs1Handler.post(obs1Runnable);
    }

    private void moveRight(){
        if (carLane < 2) {
            carLane++;
            moveCar();

        }
    }
    private void moveLeft(){
        if (carLane > 0) {
            carLane--;
            moveCar();
        }
    }
    private void moveCar() {
        checkCollision();
        switch (carLane) {
            case 0:
                cells[2][1].removeView(obstacles_IMG_car);
                cells[2][0].addView(obstacles_IMG_car);
                break;
            case 1:
                cells[2][0].removeView(obstacles_IMG_car);
                cells[2][2].removeView(obstacles_IMG_car);
                cells[2][1].addView(obstacles_IMG_car);
                break;
            case 2:
                cells[2][1].removeView(obstacles_IMG_car);
                cells[2][2].addView(obstacles_IMG_car);
                break;
        }
    }
    private void moveObstacle(){
        if(obsCurrentRow<2){
            cells[obsCurrentRow++][obsCurrentCol].removeView(obstacle);
            cells[obsCurrentRow][obsCurrentCol].addView(obstacle);
        }
        else{
            cells[obsCurrentRow][obsCurrentCol].removeView(obstacle);
            obsCurrentRow = 0;
        }

        checkCollision();
    }
    private void moveObstacle1(){
        if(obs1CurrentRow<2){
            cells[obs1CurrentRow++][obs1CurrentCol].removeView(obstacle1);
            cells[obs1CurrentRow][obs1CurrentCol].addView(obstacle1);
        }
        else{
            cells[obs1CurrentRow][obs1CurrentCol].removeView(obstacle1);
            obs1CurrentRow = 0;
        }
        checkCollision();
    }
    private void checkCollision() {
        if (carLane == obsCurrentCol && obsCurrentRow==2 || carLane == obs1CurrentCol && obs1CurrentRow==2) {
            gameManager.decreaseLive();
            gameManager.vibrate();
            updateLivesUI();
            if (gameManager.getLives() <= 0) {
                // Game Over
                handler.removeCallbacks(runnable);
                obsHandler.removeCallbacks(obsRunnable);
                // Show game over message
                gameManager.lose();
                finish();
            }
        }
    }

    private void randomObstacle(){
        cells[obsCurrentRow][obsCurrentCol].removeView(obstacle);
        int row = 0;
        int col = random.nextInt(3);

        obsCurrentRow = row;
        obsCurrentCol = col;

        cells[row][col].addView(obstacle);

        Log.d("obstacle","obstacle col: " + obsCurrentCol);

    }
    private void randomObstacle1(){
        cells[obs1CurrentRow][obs1CurrentCol].removeView(obstacle1);
        int row = 0;
        int col1 = random.nextInt(3);
        if(col1 == obsCurrentCol){
            if(col1 == 0){
                col1 = col1+1;
            }
            else if(col1 == 1){
                int r = random.nextInt(2);
                col1 = (r!=1)? r : 2;
            }
            else{
                col1 = 0;
            }
        }
        obs1CurrentRow = row;
        obs1CurrentCol = col1;
        cells[row][col1].addView(obstacle1);
        Log.d("obstacle","obstacle1 col: " + obs1CurrentCol);
    }
    private void updateLivesUI() {
        int SZ = obstacles_IMG_hearts.length;

        for (AppCompatImageView obstaclesImgHeart : obstacles_IMG_hearts) {
            obstaclesImgHeart.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < SZ - gameManager.getLives(); i++) {
            obstacles_IMG_hearts[SZ - i - 1].setVisibility(View.INVISIBLE);
        }
    }

    private void findViews() {
        obstacles_BTN_right = findViewById(R.id.obstacles_BTN_right);
        obstacles_BTN_left = findViewById(R.id.obstacles_BTN_left);
        obstacles_IMG_hearts = new AppCompatImageView[] {
                findViewById(R.id.obstacles_IMG_heart1),
                findViewById(R.id.obstacles_IMG_heart2),
                findViewById(R.id.obstacles_IMG_heart3),
        };
        cells[0][0] = findViewById(R.id.cell_0_0);
        cells[0][1] = findViewById(R.id.cell_0_1);
        cells[0][2] = findViewById(R.id.cell_0_2);
        cells[1][0] = findViewById(R.id.cell_1_0);
        cells[1][1] = findViewById(R.id.cell_1_1);
        cells[1][2] = findViewById(R.id.cell_1_2);
        cells[2][0] = findViewById(R.id.cell_2_0);
        cells[2][1] = findViewById(R.id.cell_2_1);
        cells[2][2] = findViewById(R.id.cell_2_2);
    }
}