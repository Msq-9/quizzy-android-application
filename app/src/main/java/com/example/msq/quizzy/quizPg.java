package com.example.msq.quizzy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class quizPg extends AppCompatActivity  implements GestureDetector.OnGestureListener, View.OnClickListener, Animation.AnimationListener {

    private LinearLayout options[] = new LinearLayout[4];
    private TextView questionL, questionNo, lang, prevQuestion, nextQuestion, questionR;
    private Button option_[] = new Button[4];
    private Button option[] = new Button[4];
    private TextView answers[] = new TextView[4];
    private Button dashboard, bookmark;
    private GestureDetector gestureDetector;
    private static int index;
    private ArrayList<JsonObj> jsonObj;
    private AdView mAdView;
    private ImageView hintBulb;
    private Set<String> bookmarkIDs = new HashSet<>();
    private Set<String> temp = new HashSet<>();

    Animation animFadeIn, quesAnimIn, quesAnimOut, quesFadeIn, quesFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_pg);

        //app id for admob = ca-app-pub-2174142312533088~2596900768
        //ad unit id = ca-app-pub-2174142312533088/7653786328
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        //MobileAds.initialize(this, "ca-app-pub-2174142312533088~2596900768");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        temp.add("abcd");
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("bookmark_sp", MODE_PRIVATE);
        bookmarkIDs = pref1.getStringSet("bookmark_id", temp);

        index = 1;

        option_[0] = findViewById(R.id.option_A);
        option_[1] = findViewById(R.id.option_B);
        option_[2] = findViewById(R.id.option_C);
        option_[3] = findViewById(R.id.option_D);

        option[0] = findViewById(R.id.optionA);
        option[1] = findViewById(R.id.optionB);
        option[2] = findViewById(R.id.optionC);
        option[3] = findViewById(R.id.optionD);

        options[0] = findViewById(R.id.option1);
        options[1] = findViewById(R.id.option2);
        options[2] = findViewById(R.id.option3);
        options[3] = findViewById(R.id.option4);

        answers[0] = findViewById(R.id.answer_1);
        answers[1] = findViewById(R.id.answer_2);
        answers[2] = findViewById(R.id.answer_3);
        answers[3] = findViewById(R.id.answer_4);

        option[0].setOnClickListener(this);
        option[1].setOnClickListener(this);
        option[2].setOnClickListener(this);
        option[3].setOnClickListener(this);

        option_[0].setOnClickListener(this);
        option_[1].setOnClickListener(this);
        option_[2].setOnClickListener(this);
        option_[3].setOnClickListener(this);

        prevQuestion = findViewById(R.id.prev_question);
        questionL = findViewById(R.id.question_left);
        questionR = findViewById(R.id.question_right);
        nextQuestion = findViewById(R.id.next_question);
        questionNo = findViewById(R.id.questionNo);
        lang = findViewById(R.id.language);

        dashboard = findViewById(R.id.dashboard);
        bookmark = findViewById(R.id.bookmark);

        hintBulb = findViewById(R.id.hintBulb);

        bookmark.setOnClickListener(this);

        Typeface typefaceProxima = Typeface.createFromAsset(getAssets(), "fonts/proxima.ttf");
        questionL.setTypeface(typefaceProxima);
        questionR.setTypeface(typefaceProxima);
        prevQuestion.setTypeface(typefaceProxima);
        nextQuestion.setTypeface(typefaceProxima);

        for(int i=0;i<4;i++)
            answers[i].setTypeface(typefaceProxima);
        lang.setTypeface(typefaceProxima);

        jsonObj = getIntent().getParcelableArrayListExtra("jsonObj");

        String Q_id = jsonObj.get(index-1).getQID();
        Iterator<String> iter = bookmarkIDs.iterator();
        while (iter.hasNext()) {
            String s = iter.next();

            if (s.equals(Q_id))
                bookmark.setBackgroundResource(R.drawable.star_two);
            else
                bookmark.setBackgroundResource(R.drawable.star);
        }

        Log.i("In quizePg Total Q's: ", String.valueOf(jsonObj.size()));

        String language = getIntent().getStringExtra("language");


        lang.setText(language);
        questionNo.setText(new StringBuilder().append(String.valueOf(index)).append("/").append(String.valueOf(jsonObj.size())).toString());

        questionL.setText(jsonObj.get(index-1).getQuestion());

        String opt[] = jsonObj.get(index-1).getOptions();
        for(int i=0;i<4;i++)
            answers[i].setText(String.valueOf(opt[i]));

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        quesAnimIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.prev_ques_anim);
        quesAnimOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.next_ques_anim);
        quesFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ques_fade_in);
        quesFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ques_fade_out);

        animFadeIn.setAnimationListener(this);
        quesAnimIn.setAnimationListener(this);
        quesAnimOut.setAnimationListener(this);
        quesFadeIn.setAnimationListener(this);
        quesFadeOut.setAnimationListener(this);

        gestureDetector = new GestureDetector(this);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(quizPg.this, homePg.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        try {
            if(e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 100) {
                //Toast.makeText(this, "left", Toast.LENGTH_SHORT).show();
                swipeLeft();
                return true;
            } else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 100) {
                //Toast.makeText(this, "right", Toast.LENGTH_SHORT).show();
                swipeRight();
                return true;
            }
            else if(e1.getY() - e2.getY() > 100 && Math.abs(velocityY) > 100){
                swipeUp();
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {

        String qNum =  questionNo.getText().toString();
        String[] parts = qNum.split("/");
        int currQnNo = Integer.valueOf(parts[0]);
        String ans = jsonObj.get(currQnNo-1).getAnswer();
        int optNo = Integer.valueOf(ans);

        switch(view.getId()){
            case R.id.option_A:
                updateButton(optNo, 1);
                break;
            case R.id.optionA:
                updateButton(optNo, 1);
                break;

            case R.id.option_B:
                updateButton(optNo, 2);
                break;
            case R.id.optionB:
                updateButton(optNo, 2);
                break;

            case R.id.option_C:
                updateButton(optNo, 3);
                break;
            case R.id.optionC:
                updateButton(optNo, 3);
                break;

            case R.id.option_D:
                updateButton(optNo, 4);
                break;
            case R.id.optionD:
                updateButton(optNo, 4);
                break;
            case R.id.bookmark:
                bookmarkQues();
        }
    }

    private void bookmarkQues() {
        bookmark.setBackgroundResource(R.drawable.star_two);
        String ID = jsonObj.get(index - 1).getQID();
        bookmarkIDs.add(ID);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("bookmark_sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("bookmark_id", bookmarkIDs);
        editor.apply();
    }

    private void swipeRight() {

        hintBulb.setBackgroundResource(R.drawable.bulb_off);

        String Q_id = jsonObj.get(index-1).getQID();
        Iterator<String> iter = bookmarkIDs.iterator();
        while (iter.hasNext()) {
            String s = iter.next();

            if (s.equals(Q_id))
                bookmark.setBackgroundResource(R.drawable.star_two);
            else
                bookmark.setBackgroundResource(R.drawable.star);

        }

        nextQuestion.setAlpha(0);
        prevQuestion.setAlpha(1);

        for (int i = 0; i < 4; i++) {
            option[i].setEnabled(true);
            option_[i].setEnabled(true);

            option[i].setBackgroundResource(R.drawable.options_btn);
            option_[i].setBackgroundResource(R.drawable.abcd_btn);
        }

        index--;
        if (index < 1) {
            index = 1;
            return;
        }

        if (index > jsonObj.size())
            index = jsonObj.size();


        if (index >= 1) {
            questionNo.setText(new StringBuilder().append(String.valueOf(index)).append("/").append(String.valueOf(jsonObj.size())).toString());

            questionR.startAnimation(quesAnimIn);
            prevQuestion.startAnimation(quesFadeIn);

            prevQuestion.setText(jsonObj.get(index - 1).getQuestion());
            //questionR.setText(jsonObj.get(index - 1).getQuestion());

            String opt[] = jsonObj.get(index - 1).getOptions();
            String str;
            for (int i = 0; i < 4; i++) {
                str = String.valueOf(opt[i]);
                answers[i].setText(str);
                answers[i].startAnimation(quesFadeOut);
                answers[i].startAnimation(animFadeIn);
            }
        }

    }

    private void swipeLeft() {

        hintBulb.setBackgroundResource(R.drawable.bulb_off);

        String Q_id = jsonObj.get(index-1).getQID();
        Iterator<String> iter = bookmarkIDs.iterator();
        while (iter.hasNext()) {
            String s = iter.next();

            if (s.equals(Q_id))
                bookmark.setBackgroundResource(R.drawable.star_two);
            else
                bookmark.setBackgroundResource(R.drawable.star);

        }

        prevQuestion.setAlpha(0);
        nextQuestion.setAlpha(1);

        for(int i=0;i<4;i++){
            option[i].setEnabled(true);
            option_[i].setEnabled(true);

            option[i].setBackgroundResource(R.drawable.options_btn);
            option_[i].setBackgroundResource(R.drawable.abcd_btn);
        }

        index++;
        if(index < 1)
            index = 1;

        if(index > jsonObj.size()) {
            index = jsonObj.size();
            return;
        }

        if(index <= jsonObj.size()) {
            questionNo.setText(new StringBuilder().append(String.valueOf(index)).append("/").append(String.valueOf(jsonObj.size())).toString());

            questionL.startAnimation(quesAnimOut);
            nextQuestion.startAnimation(quesFadeIn);

            nextQuestion.setText(jsonObj.get(index - 1).getQuestion());
            //questionL.setText(jsonObj.get(index - 1).getQuestion());

            String opt[] = jsonObj.get(index - 1).getOptions();
            String str;
            for (int i = 0; i < 4; i++) {
                str = String.valueOf(opt[i]);
                answers[i].setText(str);
                answers[i].startAnimation(quesFadeOut);
                answers[i].startAnimation(animFadeIn);
            }

        }

    }

    private void swipeUp() {
        hintBulb.setBackgroundResource(R.drawable.bulb_on);

        option[index - 1].setBackgroundResource(R.drawable.options_btn_yellow);
        option_[index - 1].setBackgroundResource(R.drawable.abcd_btn_yellow);
    }

    public void updateButton(int correctOptNo, int choosenOptNo){

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        if(correctOptNo != choosenOptNo){
            option[choosenOptNo-1].setBackgroundResource(R.drawable.options_btn_red);
            option_[choosenOptNo-1].setBackgroundResource(R.drawable.abcd_btn_red);

            options[choosenOptNo-1].startAnimation(shake);

            option[correctOptNo-1].setBackgroundResource(R.drawable.options_btn_green);
            option_[correctOptNo-1].setBackgroundResource(R.drawable.abcd_btn_green);

            for(int i=0;i<4;i++){
                option[i].setEnabled(false);
                option_[i].setEnabled(false);
            }

        } else {
            option[choosenOptNo-1].setBackgroundResource(R.drawable.options_btn_green);
            option_[choosenOptNo-1].setBackgroundResource(R.drawable.abcd_btn_green);

            for(int i=0;i<4;i++){
                option[i].setEnabled(false);
                option_[i].setEnabled(false);
            }
        }

    }

    @Override
    public void onAnimationStart(Animation animation) {
        if(animation == quesAnimOut)
            questionL.setAlpha(1);
        if(animation == quesAnimIn)
            questionR.setAlpha(1);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == quesAnimOut) {
            questionL.setText(jsonObj.get(index - 1).getQuestion());
            questionL.setAlpha(0);
        }
        if(animation == quesAnimIn) {
            questionR.setAlpha(0);
            questionR.setText(jsonObj.get(index - 1).getQuestion());
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
