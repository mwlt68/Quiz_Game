package com.example.kzlelma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

public class Game extends AppCompatActivity implements OnClickListener {

    private ImageView imageView;
    private Group group;
    private ConstraintLayout GameRootLayout;
    private ProgressBar progressBar;
    private TextView questionTV;
    private Button btn0,btn1,btn2,btn3;
    private Button[] answerButtons;
    private TextView appleCountTV;
    private Boolean isGameTypeYoung;
    private String  date;
    private Random random= new Random();
    private CountDownTimer countDownTimer;
    private Question curQuestion;
    private int i=0;
    private int trueCounter;
    private int trueCounterDegree=5;
    private int timeOfQuestion =15000;
    private int timeOfSecond=1000;
    private int trueAnswerWaitTime=1000;
    private int falseAnswerWaitTime=1500;
    private boolean doesSoundOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myInfo","Game on create");
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();
        trueCounter=0;
        getViews();
        getIntentInfo();
        getNewQuestion();
    }

    protected void onPause() {
        super.onPause();
        writeSharedPreferences();
        Log.i("myInfo","Game on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("myInfo","Game on stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("myInfo","Game on resume");

    }

    @Override
    protected void onDestroy(){
        Log.i("myInfo","Game on destroy");
        super.onDestroy();
        countDownTimer.cancel();
    }
    private void getIntentInfo(){
        Intent i=getIntent();
        date=i.getStringExtra("date");
        isGameTypeYoung=i.getBooleanExtra("gameType",false);
        doesSoundOpen=i.getBooleanExtra("doesSoundOpen",false);
    }

    private void writeSharedPreferences(){
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.PREF_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("appleInfo",MainActivity.appleCount);
        editor.putString("dateInfo",date);
        editor.commit();
    }
    private  void getViews(){
        imageView=findViewById(R.id.imageView);
        appleCountTV=findViewById(R.id.redAplleCountTV2);
        group=findViewById(R.id.group2);
        GameRootLayout=findViewById(R.id.GameRootLayout);
        progressBar=findViewById(R.id.progressBar);
        questionTV=findViewById(R.id.QuestionTV);
        answerButtons= new Button[4];
        btn0=findViewById(R.id.answerBtn0);
        answerButtons[0]=btn0;
        btn1=findViewById(R.id.answerBtn1);
        answerButtons[1]=btn1;
        btn2=findViewById(R.id.answerBtn2);
        answerButtons[2]=btn2;
        btn3=findViewById(R.id.answerBtn3);
        answerButtons[3]=btn3;
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }
    public void getNewQuestion(){

        restartAnswerButtons();
        appleCountTV.setText(String.valueOf(MainActivity.appleCount));
        curQuestion=getRandomQuestion();
        showQuestion(curQuestion);
        putAnswersToButtons(curQuestion);
        createCountDownTimer();
        countDownTimer.start();
    }
    private  void createCountDownTimer(){
        i=0;
        countDownTimer=new CountDownTimer(timeOfQuestion,timeOfSecond) {

            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int)i*100/(timeOfQuestion/timeOfSecond));
                i++;
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                setAnswerButtonsEnable(false);
                Handler handler= new Handler();
                Button trueBtn=getTrueButton(curQuestion);
                setDrawableFromId(trueBtn ,R.drawable.btn_false);
                playButtonSound(false);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDoesGameContinueDialog();
                    }
                },falseAnswerWaitTime);
            }
        };
    }
    private void restartAnswerButtons(){
        setAnswerButtonsEnable(true);
        for (Button btn: answerButtons) {
            setDrawableFromId(btn,R.drawable.btn_normal);
        }
    }
    private void answerControl(Button clickedButton){
        countDownTimer.cancel();
        Handler handler= new Handler();
        Button trueBtn=getTrueButton(curQuestion);
        setAnswerButtonsEnable(false);
        if (trueBtn.equals(clickedButton)){                     // True answer
            setDrawableFromId(clickedButton , R.drawable.btn_true);
            trueCounter++;
            if (trueCounter == trueCounterDegree)
            {
                trueCounter=0;
                MainActivity.appleCount++;
            }
            playButtonSound(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getNewQuestion();
                }
            },trueAnswerWaitTime);
        }
        else{                                                   // False Answer

            setDrawableFromId(clickedButton , R.drawable.btn_false);                // paint to false answer
            setDrawableFromId(trueBtn , R.drawable.btn_true);                      //paint to true answer
            playButtonSound(false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDoesGameContinueDialog();
                }
            },falseAnswerWaitTime);
        }
    }
    private void showDoesGameContinueDialog(){
        trueCounter=0;
        Game_Continue_Dialog game_continue_dialog= new Game_Continue_Dialog(this,doesSoundOpen);
        game_continue_dialog.show();
    }
    private Button getTrueButton(Question question){
        for (Button btn:answerButtons) {
            if (question.trueAnswer.equals(btn.getText()))
                return btn;
        }
        return null;
    }
    private void setAnswerButtonsEnable(boolean value){
        for (Button btn :answerButtons) {
            btn.setEnabled(value);
        }
    }
    private void setDrawableFromId(Button btn , int id){
        btn.setBackgroundResource(id);
    }
    private void showQuestion(Question question){
        try {
            questionTV.setText(question.question);
            if (question.imageName.equals("null")){
                hideImageView();
            }
            else {
                int resourceId=getImageResourceId(question);
                showImageView();
                imageView.setImageResource(resourceId);
            }
        }catch (Exception e){
            Log.e("myError","Show Question in Game.java Error");
        }
    }
    private void putAnswersToButtons(Question question){

        int rndTrueAnswerVal=random.nextInt(4);
        ArrayList<Integer> result=getRandomFalseAswerNumber(question.falseAnswer.length);
        int k=0;
        for (int j = 0; j < 4; j++) {
            if (j==rndTrueAnswerVal)
            {
                answerButtons[j].setText(question.trueAnswer);
            }
            else{
                int index=result.get(k);
                String falseAnswer=question.falseAnswer[index];
                answerButtons[j].setText(falseAnswer);
                k++;
            }
        }
    }
    private ArrayList<Integer> getRandomFalseAswerNumber(int lenghtOfFalseAnswer){
        int rndVal;
        ArrayList<Integer> result= new ArrayList<Integer>(4);
        int j=0;
        while (j<3) {
            rndVal=random.nextInt(lenghtOfFalseAnswer);
            boolean canAdd=true;
            for (int x:result) {
                if (x==rndVal){
                    canAdd=false;
                    break;
                }
            }
            if (canAdd){
                result.add(rndVal);
                j++;
            }

        }
        return  result;
    }
    private int getImageResourceId(Question question){
        int result=0;
        if (question.imageName.equals("null"))
            return result;
        try {
            result= this.getResources().getIdentifier(question.imageName, "raw", this.getPackageName());
            return result;
        }
        catch (Exception e){
            Log.e("myError"," getImageResourceId in main Activity");
            return  result;
        }
    }
    private Question getRandomQuestion(){
        int rndValue=0;
        if (isGameTypeYoung){
            rndValue=random.nextInt(MainActivity.questionsOfYoung.size());
            return MainActivity.questionsOfYoung.get(rndValue);

        }
        else{
            rndValue=random.nextInt(MainActivity.questionsOfAdult.size());
            return MainActivity.questionsOfAdult.get(rndValue);
        }
    }
    private void hideImageView(){
        imageView.setVisibility(View.GONE);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(GameRootLayout);
        float biasValue= 1.0f;
        constraintSet.setVerticalBias(R.id.group2,biasValue);
        constraintSet.applyTo(GameRootLayout);
    }
    private void showImageView(){
        imageView.setVisibility(View.VISIBLE);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(GameRootLayout);
        float biasValue= 0.4f;
        constraintSet.setVerticalBias(R.id.group2,biasValue);
        constraintSet.applyTo(GameRootLayout);
    }
    private void playButtonSound(boolean isTrue){
        try {
            MediaPlayer buttonTrueSound= new MediaPlayer();
            MediaPlayer buttonFalseSound= new MediaPlayer();
            buttonTrueSound= MediaPlayer.create(this, R.raw.true_sound);
            buttonFalseSound= MediaPlayer.create(this, R.raw.false_sound);
            if (doesSoundOpen){
                if (isTrue)
                    buttonTrueSound.start();
                else
                    buttonFalseSound.start();
            }
        }
        catch (Exception ex){
            Log.e("myError",ex.getMessage());
        }
    }
    @Override
    public void onClick(View view) {
        for (Button btn: answerButtons) {
            if (btn == view)
                answerControl(btn);
        }
    }
}
