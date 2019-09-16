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

public class HistoryQuiz extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    TextView questionNumber, questionContent;
    EditText answer;
    HistoryDatabase db;
    Button nextQuestion;
    ArrayList<HistoryClass> theList;
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

        db = new HistoryDatabase(this);
        addQuestionGeoraphy();

        theList= new ArrayList<>();
        Cursor data = db.viewAllQuestion();
        if(data.getCount() == 0){
            Toast.makeText(HistoryQuiz.this,"The Database was empty",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()) {
                HistoryClass history = new HistoryClass(data.getString(1), data.getString(2));
                theList.add(history);
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
            Intent result = new Intent(HistoryQuiz.this,ResultActivity.class);
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
        db.addQuestion("The first news paper in the world was started by ?","China");
        db.addQuestion("Who is known as Man of Blood and Iron ?","Bismarck");
        db.addQuestion("The oldest dynasty still ruling in which country   ?","Japan");
        db.addQuestion("Who was among the famous Roman poets ?","Virgil");
        db.addQuestion("The English King who was prepared to exchange his kingdom for a horse was","Richard III");
        db.addQuestion("Which of the following city has highest historical monuments?","Delhi");
        db.addQuestion("Which of the following countries was associated with Khmer Rouge?","Cambodia");
        db.addQuestion("Who began the construction of a wall in ancient China, to keep out invaders from the north, known as the Great wall of China?","Chin Ruler");
        db.addQuestion("Who is the first woman in space ?","Valentina Tereshkova");
        db.addQuestion("In which year Hitler became the Chancellor of Germany ?","1933");
        db.addQuestion("Which company made the first tea bags?","Tetley");
        db.addQuestion("Pearl Harbour is located in","Hawai");
        db.addQuestion("The first President of USA was","George Washington");
        db.addQuestion("'Sphinx' is associated with which of the following civilizations?"," Egyptian Civilization");
        db.addQuestion("What is the world’s oldest surviving republic?","San Marino");
        db.addQuestion("Who was the first to sail round the world ?","Magellan");
        db.addQuestion("What is Portland Bill?","A Lighthouse");
        db.addQuestion("Which of these fields was the lost generation associated with?","Literature");
        db.addQuestion("Who wrote the first history book?","Herodotus");
        db.addQuestion("Of which country was the religion 'Shintoism'?","Japan");

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
