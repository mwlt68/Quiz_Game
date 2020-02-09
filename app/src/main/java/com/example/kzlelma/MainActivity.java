package com.example.kzlelma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String PREF_FILE_NAME = "InfoOfActivity";
    private Button gameStartBtn,getInformationBtn;
    private TextView redAppleCountTV;
    private ImageView soundImageView;
    private ToggleButton gameTypeBtn;
    private ConstraintLayout mainConstaintLayout;
    private List<Question> questions;
    public static List<Question> questionsOfYoung;
    public static List<Question> questionsOfAdult;
    public static int appleCount;
    private boolean doesSoundOpen;
    private MediaPlayer buttonClickSound;
    private String date;
    private MediaPlayer[] gameSounds;
    private Random random = new Random();
    private Animation blinkAnimation;


    public enum GameType{
        young,
        adult
    }
    private  GameType gameType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        doesSoundOpen=true;
        getView();
        blinkAnimation=AnimationUtils.loadAnimation(this,R.anim.blink);
        getMediaPlayers();
        getQuestions();
        getAgeQuestions();
        if (questionsOfYoung== null || questionsOfAdult==null)
            finish();
        getSharedPreferences();
        Log.i("myInfo","Main on create");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("myInfo","Main on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("myInfo","Main on stop");
        writeSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences();
        playRandomGameSound();
        soundImageView.setAnimation(blinkAnimation);
        Log.i("myInfo","Main on resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("myInfo","Main on destroy");

    }

    private void getView(){
        mainConstaintLayout=findViewById(R.id.MainConstarintLayout);
        gameStartBtn=findViewById(R.id.button8);
        gameStartBtn.setOnClickListener(this);
        getInformationBtn=findViewById(R.id.button9);
        getInformationBtn.setOnClickListener(this);
        redAppleCountTV=findViewById(R.id.redAplleCountTV3);
        soundImageView=findViewById(R.id.imageViewSound);
        soundImageView.setOnClickListener(this);
        gameTypeBtn=findViewById(R.id.toggleButton);
        gameTypeBtn.setOnClickListener(this);
    }

    private void getMediaPlayers(){
        buttonClickSound= MediaPlayer.create(this, R.raw.button_sound);
        gameSounds=new MediaPlayer[7];
        gameSounds[0]=MediaPlayer.create(this, R.raw.game_sound1);
        gameSounds[1]=MediaPlayer.create(this, R.raw.game_sound2);
        gameSounds[2]=MediaPlayer.create(this, R.raw.game_sound3);
        gameSounds[3]=MediaPlayer.create(this, R.raw.game_sound4);
        gameSounds[4]=MediaPlayer.create(this, R.raw.game_sound5);
        gameSounds[5]=MediaPlayer.create(this, R.raw.game_sound6);
        gameSounds[6]=MediaPlayer.create(this, R.raw.game_sound7);
    }
    private void playRandomGameSound(){
        if (doesSoundOpen){
            try {
                stopSound();
                int val=random.nextInt(gameSounds.length);
                gameSounds[val].start();
            }
            catch (Exception ex){
                Log.e("myError",ex.getMessage());
            }
        }
    }
    private void playButtonSound(){
        try {
            if (doesSoundOpen)
                buttonClickSound.start();
        }
        catch (Exception ex){
            Log.e("myError",ex.getMessage());
        }
    }
    private void writeSharedPreferences(){
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("appleInfo",appleCount);
        editor.putString("dateInfo",date);
        editor.commit();
    }

    private void getSharedPreferences(){
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        date = sharedPref.getString("dateInfo",getCurrentDate());
        appleCount= sharedPref.getInt("appleInfo",10);
        Boolean isGameTypeYoung= sharedPref.getBoolean("gameTypeInfo",true);
        if (!date.equals(getCurrentDate())){
            appleCount+=10;
            date=getCurrentDate();
        }
        if (isGameTypeYoung){
            gameTypeBtn.setChecked(true);
            gameType=GameType.young;
        }
        else{
            gameTypeBtn.setChecked(false);
            gameType=GameType.adult;
        }
        redAppleCountTV.setText(String.valueOf(appleCount));
    }
    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String getCurrentDateTime = sdf.format(c.getTime());
        return  getCurrentDateTime;
    }

    private void getQuestions(){
        try {
            Context context = this;
            InputStream inputStream = context.getResources().openRawResource(R.raw.data);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line);
            }
            questions=MyJsonParser.jsonParse(total.toString());
        }
        catch (Exception e){
            Log.e("myError","There are error during get questions !");
            finish();
        }

    }

    public void setSoundProcess(){

        if (doesSoundOpen){
            stopSound();
            doesSoundOpen=false;
            soundImageView.setBackgroundResource(R.drawable.music_off);
        }
        else
        {
            doesSoundOpen=true;
            soundImageView.setBackgroundResource(R.drawable.music);
            playRandomGameSound();
        }
    }
    private void stopSound(){
        if (doesSoundOpen){
            for (MediaPlayer mp:gameSounds) {
                if (mp.isPlaying())
                    mp.pause();
            }
        }
    }
    @Override
    public void onClick(View view) {
        if(view==gameStartBtn){
            gameStartEvent();
        }
        else if(view==getInformationBtn){
            showInformationEvent();
        }
        else if(view == gameTypeBtn){
            playButtonSound();
            setGameType();
        }
        else if(view == soundImageView){
            setSoundProcess();
        }

    }
    private void showInformationEvent(){
        playButtonSound();
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNewInformation();
            }
        },900);
    }
    private void gameStartEvent(){
        playButtonSound();
        startGameActivity();
    }

    private void startGameActivity(){
        if (appleCount <= 0) {
            Toast.makeText(getApplicationContext(), "Yeterli Kızıl Elmanız Yok !", Toast.LENGTH_LONG).show();
        }
        else{
            appleCount--;
            stopSound();
            Intent i = new Intent(this,Game.class);
            i.putExtra("date",date);
            i.putExtra("doesSoundOpen",doesSoundOpen);
            if (gameType==GameType.young)
                i.putExtra("gameType",true);
            else
                i.putExtra("gameType",false);

            startActivity(i);
        }

    }
    private void showNewInformation(){
        List<Question> usableQuestion;
        if (gameType == MainActivity.GameType.young)
            usableQuestion=questionsOfYoung;

        else    // GameType.adult
            usableQuestion=questionsOfAdult;
        CustomDialog customDialog= new CustomDialog(this,usableQuestion,doesSoundOpen);
        customDialog.show();
    }

    private void getAgeQuestions(){
        if (questions!= null){
            questionsOfYoung= new ArrayList<>();
            questionsOfAdult= new ArrayList<>();
            for (Question q:questions) {
                if (q.category==GameType.young)
                    questionsOfYoung.add(q);
                else
                    questionsOfAdult.add(q);
            }
        }
    }
    private void setGameType(){
        if (gameType==GameType.adult)
            gameType=GameType.young;
        else
            gameType=GameType.adult;
        putGameTypeToSharedP();
    }
    private void putGameTypeToSharedP(){
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (gameType.equals(GameType.young))
            editor.putBoolean("gameTypeInfo",true);
        else
            editor.putBoolean("gameTypeInfo",false);
        editor.commit();

    }
}
