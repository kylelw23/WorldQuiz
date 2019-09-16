package com.example.worldquiz;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LiteratureQuiz extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    TextView questionNumber, questionContent;
    EditText answer;
    LiteratureDatabase db;
    Button nextQuestion;
    ArrayList<LiteratureClass> theList;
    int trueAnswer =0;
    int falseAnswer =0;
    int number =0;
    private final Random random = new Random();
    private final int range = 19;
    private int previous;
    SessionDatabase sessiondb;
    int[] myList = new int[10];
    ArrayList<Session> listOfSessions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geography_quiz);
        sessiondb = new SessionDatabase(this);
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.ROOT);
        Date dateobj = new Date();
        String time = df.format(dateobj);
        String nickname = getIntent().getStringExtra("NICKNAME");
        Session newSession = new Session(nickname,time,"0");
        sessiondb.addSession(newSession.getUserName(),time,newSession.getPoints());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Initialize drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        questionNumber = findViewById(R.id.questionNumber);
        questionContent = findViewById(R.id.questionContent);
        nextQuestion = findViewById(R.id.nextQuestion);
        answer = findViewById(R.id.answerText);

        db = new LiteratureDatabase(this);
        addQuestionGeoraphy();
        // Adding questions and answer from database to Literature Array List
        theList= new ArrayList<>();
        Cursor data = db.viewAllQuestion();
        if(data.getCount() == 0){
            Toast.makeText(LiteratureQuiz.this,"The Database was empty",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()) {
                LiteratureClass literature = new LiteratureClass(data.getString(1), data.getString(2));
                theList.add(literature);
            }
        }

        String kobiet = "Question 1:";
        questionNumber.setText(kobiet);
        int rand = nextRnd();
        questionContent.setText(theList.get(rand).getQuestion());
        myList[0] = rand;
        nextQuestion.setOnClickListener(clickListener);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.changeQuiz:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.pointEarned:
                pointEarned();
                break;
            case R.id.exitOption:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public final View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            nextQuestion();
        }
    };

    public void nextQuestion(){
        ++number;
        if(number == 10){
            if(answer.getText().toString().toLowerCase().trim().equals(theList.get(myList[number-1]).getAnswer().toLowerCase().trim())){
                ++trueAnswer;
            }else{falseAnswer++;}
            answer.setText("");
            Intent result = new Intent(LiteratureQuiz.this,ResultActivity.class);
            result.putExtra("trueAnswer",Integer.toString(trueAnswer));
            result.putExtra("falseAnswer",Integer.toString(falseAnswer));
            result.putExtra("nickname",getIntent().getStringExtra("NICKNAME"));
            startActivity(result);
        }
        if(number <= 9) {
            int rand = nextRnd();
            myList[number]=rand;
            String ques="Question " + (number+ 1)+ ":";
            questionNumber.setText(ques);
            questionContent.setText(theList.get(rand).getQuestion());
//            showMessage("The answer is",theList.get(myList[number-1]).getAnswer().toLowerCase().trim() );
//            showMessage("Your answer is ",answer.getText().toString().toLowerCase().trim() );
            if(answer.getText().toString().toLowerCase().trim().equals(theList.get(myList[number-1]).getAnswer().toLowerCase().trim())){
                ++trueAnswer;
            }else{falseAnswer++;}
            answer.setText("");
        }
    }
    public void pointEarned(){
        String name = getIntent().getStringExtra("NAME");
        String nickname = getIntent().getStringExtra("NICKNAME");
        if(name == null && nickname == null ) {
            Cursor res = sessiondb.viewAllSession();
            if (res.getCount() == 0) {
                showMessage("Points earned", "No session found");
            }else {
                while(res.moveToNext()) {
                    Session session = new Session(res.getString(1), res.getString(2), res.getString(3));
                    listOfSessions.add(session);
                }
                int points = Integer.parseInt(listOfSessions.get(listOfSessions.size()-1).getPoints());
                String sessionDate;
                String firstMsg = "Hi " + nickname + ", You have earned " + points + " in the following Sessions";
                String secondMsg ="";
                for(int i=0; i<=listOfSessions.size(); i++) {
                    points = Integer.parseInt(listOfSessions.get(i).getPoints());
                    sessionDate = listOfSessions.get(i).getDate();
                    secondMsg = "Session started on " + sessionDate + "-- points earned " + points+'\n';
                }
                showMessage("Points earned", firstMsg + '\n' + secondMsg);
            }
        }else{
            showMessage("Error","Name and nickname is empty");
        }
    }
    public void addQuestionGeoraphy(){
        db.addQuestion("Shakespeare was born in","Warwickshire");
        db.addQuestion("Total number of sonnets written by Shakespeare","154");
        db.addQuestion("Total number of plays written by Shakespeare","38");
        db.addQuestion("With which theatre in London Shakespeare was associated with","The Globe");
        db.addQuestion("In which city the play of Shakespeare 'Romeo and Juliet' is set in","Verona");
        db.addQuestion("What is the name of the storyteller of 'One Thousand and One Nights'","Scheherazade");
        db.addQuestion("Which one is the first tragedy play of Shakespeare","Titus Andronicus");
        db.addQuestion("Which one is the first science-fiction novel","Frankenstein");
        db.addQuestion("From 1st January 2007, how many digits contains in ISBN (International Standard Book Number)","13");
        db.addQuestion("Who was a blind poet","Homer");
        db.addQuestion("How many novels combine the Harry Potter series collection","7");
        db.addQuestion("Who is the first ever winner of the Nobel Prize in Literature","Sully Prudhomme");
        db.addQuestion("Which one is the world's longest-running play","The Mousetrap");
        db.addQuestion("Who is known as the national poet of England","William Shakespeare");
        db.addQuestion("Who is the author of the famous novel 'War and Peace'","Leo Tolstoy");
        db.addQuestion("How many lines does a Shakespearean sonnet have","14");
        db.addQuestion("Which one is the world's longest novel","Remembrance of Things Past");
        db.addQuestion("Which country awarded the Pulitzer Prize","USA");
        db.addQuestion("What is the name of the storyteller of 'One Thousand and One Nights'","Scheherazade");
        db.addQuestion("Pulitzer Prize was first awarded in the year","1917");
    }
    int nextRnd() {
        if (previous == 0) return previous = random.nextInt(range) + 1;
        final int rnd = random.nextInt(range-1) + 1;
        return previous = (rnd < previous? rnd : rnd + 1);
    }
    public void showMessage(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }
}
