package com.example.kzlelma;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class Game_Continue_Dialog extends Dialog implements android.view.View.OnClickListener {
    public Activity activity;
    public Dialog dialog;
    private Button continueBtn,finishBtn;
    private TextView textView;
    private Boolean doesSoundOpen;
    private Animation bounceAnimation;
    public Game_Continue_Dialog(Activity a,boolean doesSoundOpen) {
        super(a);
        this.activity = a;
        this.doesSoundOpen=doesSoundOpen;
        bounceAnimation= AnimationUtils.loadAnimation(activity,R.anim.bounce);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.does_game_continue_dialog);
        getViews();
        checkAppleCount();
    }
    private void checkAppleCount(){
        if (MainActivity.appleCount < 1){
            continueBtn.setVisibility(View.GONE);
            textView.setText("Üzgünüm,yeterli kızıl elmanız yok !");
        }
    }
    private void getViews(){
        continueBtn=findViewById(R.id.GCD_continue_btn);
        finishBtn=findViewById(R.id.GCD_finish_btn);
        finishBtn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
        textView=findViewById(R.id.GCD_QuestionTV);
    }
    private void playButtonSound(){
        try {
            if (doesSoundOpen){
                MediaPlayer buttonClickSound = MediaPlayer.create(activity, R.raw.button_sound);
                buttonClickSound.start();
            }
        }
        catch (Exception ex){
            Log.e("myError",ex.getMessage());
        }
    }
    @Override
    public void onClick(View v) {
        if (v==continueBtn){
            MainActivity.appleCount--;
            playButtonSound();
            final Game x=(Game) activity;
            continueBtn.startAnimation(bounceAnimation);
            Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    x.getNewQuestion();
                }
            },650);


        }
        else if(v==finishBtn){
            playButtonSound();
            finishBtn.startAnimation(bounceAnimation);
            Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                }
            },650);

        }
        else{
            activity.finish();
        }

    }
}