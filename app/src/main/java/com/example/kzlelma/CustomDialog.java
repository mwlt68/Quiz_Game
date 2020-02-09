package com.example.kzlelma;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class CustomDialog extends Dialog implements
    android.view.View.OnClickListener {
    Random random= new Random();
    public Activity activity;
    public Dialog dialog;
    private Button okayBtn;
    private TextView questionTV,answerTV;
    private ImageView imageView;
    private List<Question> questions;
    private boolean doesSoundOpen;
    private Animation bounceAnimation;
    public CustomDialog(Activity a,List<Question> questions,Boolean doesSoundOpen) {
        super(a);
        this.activity = a;
        this.questions = questions;
        this.doesSoundOpen=doesSoundOpen;
        bounceAnimation= AnimationUtils.loadAnimation(activity,R.anim.bounce);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_custom_dialog);
        getViews();
        putQuestionToDialog(getRandomQuestion());
    }
    private void putQuestionToDialog(Question q){
        try {
            questionTV.setText(q.question);
            answerTV.setText(q.trueAnswer);
            if (q.imageName.equals("null")){
                imageView.setVisibility(View.GONE);
            }
            else {
                int resourceId=getImageResourceId(q);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(resourceId);
            }
        }catch (Exception e){
            Log.e("myError","Custom dialog put Question To Dialog Error");
        }
    }
    private int getImageResourceId(Question question){
        int result=0;
        if (question.imageName.equals("null"))
            return result;
        try {
            result= activity.getResources().getIdentifier(question.imageName, "raw", activity.getPackageName());
            return result;
        }
        catch (Exception e){
            Log.e("myError"," getImageResourceId in main Activity");
            return  result;
        }
    }
    private  void getViews(){
        okayBtn=findViewById(R.id.customDialogBtn);
        okayBtn.setOnClickListener(this);
        questionTV=findViewById(R.id.CD_QuestionTV);
        answerTV=findViewById(R.id.CD_AnswerTV);
        imageView=findViewById(R.id.CD_imageView);
    }
    private Question getRandomQuestion(){
        int rndValue=random.nextInt(questions.size());
        return questions.get(rndValue);
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
        if (v==okayBtn){
            okayBtn.startAnimation(bounceAnimation);
            playButtonSound();
            Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    putQuestionToDialog(getRandomQuestion());
                }
            },650);

        }
        else{
            dismiss();
        }
    }
}