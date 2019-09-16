package com.example.worldquiz;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    Button finishButton, exitButton;
    TextView result1, result2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        result1 = findViewById(R.id.result1);
        result2 = findViewById(R.id.result2);
        finishButton = findViewById(R.id.finishButton);
        exitButton = findViewById(R.id.exitButton);

        int trueAnswer = Integer.parseInt(getIntent().getStringExtra("trueAnswer"));
        int falseAnswer = Integer.parseInt(getIntent().getStringExtra("falseAnswer"));
        int point = trueAnswer*5 - falseAnswer*2;
        if(point <0) {
            point =0;
            String result = "Well done " + getIntent().getStringExtra("nickname") + ", you have " +
                    trueAnswer + " correct answers and " +
                    falseAnswer + " incorrect answers or " + point + " points in this attempt";
            result1.setText(result);
            String aresult = "Overall in this session, you have " + point + " points";
            result2.setText(aresult);
        }else{
            String result = "Well done " + getIntent().getStringExtra("nickname") + ", you have " +
                    trueAnswer + " correct answers and " +
                    falseAnswer + " incorrect answers or " + point + " points in this attempt";
            result1.setText(result);
            String aresult = "Overall in this session, you have " + point + " points";
            result2.setText(aresult);
        }
        finishButton.setOnClickListener(clickListener);
        exitButton.setOnClickListener(clickListener);


        //SessionDatabase sessiondb = new SessionDatabase(this);
        //Cursor res = sessiondb.getSession(1);
        //res.moveToFirst();
    }
    public final View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.finishButton:
                    finishAction();
                    break;
                case R.id.exitButton:
                    Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
    public void finishAction(){

    }
}
