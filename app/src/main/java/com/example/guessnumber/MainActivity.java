package com.example.guessnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.InputDevice;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int nombre = 0, essay = 0 , timeleft;

    boolean checkstart=false;
    String historique="";
    Button confirm,reset,show;
    TextView result,hist,counter;
    EditText guessnumero;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        confirm=(Button) findViewById(R.id.confirm);
        reset=(Button) findViewById(R.id.reset);
        result=(TextView) findViewById(R.id.result);
        hist=(TextView) findViewById(R.id.historique);
        counter=(TextView) findViewById(R.id.counter);
        guessnumero=(EditText) findViewById(R.id.num);
        show=findViewById(R.id.showData);
        if (checkstart==false)
        {
            reset.setText("Start");
            guessnumero.setVisibility(View.INVISIBLE);
            confirm.setVisibility(View.INVISIBLE);
        }
        CountDownTimer countdownjeux = new CountDownTimer(100000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText( + millisUntilFinished / 1000 +"S remaining");
                timeleft = (int) (millisUntilFinished / 1000);
            }

            public void onFinish() {
                counter.setText("done!");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Score");
                builder.setMessage("Failed 0 Score :(");
                AlertDialog alert = builder.create();
                alert.show();
            }

        };

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startnewgame();
                countdownjeux.start();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                essay=essay+1;
                int newchif;
                newchif=Integer.valueOf(guessnumero.getText().toString());
                if (newchif > nombre) {
                    result.setText("Inférieur");
                } else if (newchif < nombre) {
                    result.setText("Supérieur");
                } else {
                    result.setText(nombre +" : Bravooo :) ");
                    countdownjeux.cancel();
                    int score=timeleft-essay;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Score");
                    if (score>50){
                        builder.setMessage("Great you wiiiin with  :"+ score +" Score ");
                        final EditText name = new EditText(MainActivity.this);
                        name.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(name);

                        // Set up the buttons
                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String scoreFinal=Integer.toString(score);
                                String nameFinal = name.getText().toString();
                                Boolean checkinsert= db.insertData(nameFinal,scoreFinal);
                                if (checkinsert == true){
                                    Toast.makeText(getApplicationContext(),"Score Saved",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Score Not Saved",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                    }
                    else
                    {
                        builder.setMessage("Failed : 0 Score :(");
                    };
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                historique = historique + "\r\n" + guessnumero.getText().toString();
                hist.setText(historique);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = db.showAllData();
                if (res.getCount()==0){
                    Toast.makeText(getApplicationContext(),"No score exists",Toast.LENGTH_LONG).show();
                }
                StringBuffer buffer= new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("Name :"+res.getString(0)+"\t"+"\t");
                    buffer.append("Score :"+res.getString(1)+"\n");
                }

                AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("All best Scores ");
                builder.setMessage(buffer.toString());
                builder.show();
                }

        });

    }
    public void startnewgame()
    {
        essay = 0 ;
        timeleft = 0;
        Random numRandom = new Random();
        nombre = numRandom.nextInt(100);
        checkstart=true;
        historique="";
        guessnumero.setText("0");
        reset.setText("Start new Game");
        guessnumero.setVisibility(View.VISIBLE);
        hist.setText("");
        confirm.setVisibility(View.VISIBLE);
        result.setText("Guess Number!");
    }



}