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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    EditText name, nickname;
    Button historyBtn, geographyBtn, literatureBtn;
    SessionDatabase sessiondb;
    ArrayList<Session> listOfSessions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.changeQuiz);
        }
        name = findViewById(R.id.name);
        nickname = findViewById(R.id.nickname);

        historyBtn = findViewById(R.id.history);
        geographyBtn = findViewById(R.id.geography);
        literatureBtn = findViewById(R.id.literature);

        historyBtn.setOnClickListener(clicklistenner);
        geographyBtn.setOnClickListener(clicklistenner);
        literatureBtn.setOnClickListener(clicklistenner);
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
                finish();
                System.exit(0);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void pointEarned(){
        if(name.getText().toString().isEmpty() && nickname.getText().toString().isEmpty()) {
            Cursor res = sessiondb.viewAllSession();
            if (res.getCount() == 0) {
                showMessage("Points earned", "No session found");
                return;
            }else {
                while(res.moveToNext()) {
                    Session session = new Session(res.getString(1), res.getString(2), res.getString(3));
                    listOfSessions.add(session);
                }
                int points = Integer.parseInt(listOfSessions.get(listOfSessions.size()-1).getPoints());
                String sessionDate = "Concac";
                String firstMsg = "Hi " + nickname.getText().toString() + ", You have earned " + points + " in the following Sessions";
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
    public final View.OnClickListener clicklistenner = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(name.getText().toString().isEmpty() && nickname.getText().toString().isEmpty()){
                showMessage("Error","Please insert the name and nickname");
            }else {
                switch (v.getId()) {
                    case R.id.history:
                        makeHistory();
                        break;
                    case R.id.geography:
                        makeGeoraphy();
                        break;
                    case R.id.literature:
                        makeLiterature();
                        break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public void makeHistory(){
        Intent history = new Intent(MainActivity.this, HistoryQuiz.class);
        history.putExtra("NAME",name.getText().toString());
        history.putExtra("NICKNAME",nickname.getText().toString());
        startActivity(history);
    }
    public void makeGeoraphy(){
        Intent georaphy = new Intent(MainActivity.this, GeographyQuiz.class);
        georaphy.putExtra("NAME",name.getText().toString());
        georaphy.putExtra("NICKNAME",nickname.getText().toString());
        startActivity(georaphy);
    }
    public void makeLiterature(){
        Intent literature = new Intent(MainActivity.this, LiteratureQuiz.class);
        literature.putExtra("NAME",name.getText().toString());
        literature.putExtra("NICKNAME",nickname.getText().toString());
        startActivity(literature);
    }
    public void showMessage(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }
}
