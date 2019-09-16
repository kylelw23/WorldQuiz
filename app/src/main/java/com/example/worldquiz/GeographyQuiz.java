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

public class GeographyQuiz extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    TextView questionNumber, questionContent;
    EditText answer;
    GeographyDatabase db;
    Button nextQuestion;
    SessionDatabase sessiondb;
    ArrayList<Session> listOfSessions = new ArrayList<>();
    ArrayList<GeoraphyClass> theList;
    int trueAnswer =0;
    int falseAnswer =0;
    int number =0;
    int[] myList = new int[10];
    private final Random random = new Random();
    private final int range = 19;
    private int previous;
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
        boolean isInserted = sessiondb.addSession(newSession.getUserName(),time,newSession.getPoints());

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

        db = new GeographyDatabase(this);
        addQuestionGeoraphy();

        theList= new ArrayList<>();
        Cursor data = db.viewAllQuestion();
        if(data.getCount() == 0){
            Toast.makeText(GeographyQuiz.this,"The Database was empty",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()) {
                GeoraphyClass georaphy = new GeoraphyClass(data.getString(1), data.getString(2));
                theList.add(georaphy);
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
            Intent result = new Intent(GeographyQuiz.this,ResultActivity.class);
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
        db.addQuestion("Where does Vietnam belong to?","South Asia");
        db.addQuestion("How many states does Australia have?","6");
        db.addQuestion("Which country has the largest dimension?","Russia");
        db.addQuestion("Which pole does Australia belong to?","Asia");
        db.addQuestion("Which country is nearest australia?","New Zealand");
        db.addQuestion("How many countries in South Asia?","11");
        db.addQuestion("Which state does Sydney belong to?","New South Wales");
        db.addQuestion("Which state does Melbourne belong to?","Victoria");
        db.addQuestion("Which state does Brisbane belong to?","Queensland");
        db.addQuestion("Which state does Perth belong to?","North Territory");
        db.addQuestion("Which city has Darling Harbour?","Sydney");
        db.addQuestion("What is the main island of Japan?","Honshu");
        db.addQuestion("What city was once called New Amsterdam?","New York City");
        db.addQuestion("What country is often described as being shaped like a boot?","Italy");
        db.addQuestion("The great Victoria Desert is located in","Australia");
        db.addQuestion("The place of origin of an earthquake is called","Focus");
        db.addQuestion("Which is the largest fishing ground in the world?","North sea");
        db.addQuestion("What major river flows through the Grand Canyon?"," Colorado river");
        db.addQuestion(" Which country is the leading egg producer in the world?","China");
        db.addQuestion("Island Aviation Services is the Government run airlines of ?","Maldives");
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
