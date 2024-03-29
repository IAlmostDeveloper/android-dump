package com.example.cardstudying;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnAdd;
    SharedPreferences prefs;
    String[] mainMenuPhrases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPrefs();
        fillDB();
        getPhrasesFromRescources();
        setGameActivityButton();
        setAddCardsActivityButton();
        setCardsMode();
        setHelpButton();
        setMainCard();
    }

    private void setCardsMode() {
        String[] data = {"СЛОВО->ПЕРЕВОД", "ПЕРЕВОД->СЛОВО"};
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setSpinner(sAdapter);
    }

    private void setSpinner(ArrayAdapter<String> spinnerAdapter) {
        Spinner spinner = findViewById(R.id.invert_cards);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(prefs.getBoolean("invert", false) ? 1 : 0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean invert = position != 0;
                prefs.edit().putBoolean("invert", invert).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setHelpButton() {
        findViewById(R.id.help_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpDialog.show(MainActivity.this);
            }
        });
    }

    private void getPhrasesFromRescources() {
        mainMenuPhrases = getResources().getString(R.string.phrases).split(";");
    }

    private void setPrefs() {
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    private void setMainCard() {
        final TextView card = findViewById(R.id.card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setText(mainMenuPhrases[new Random().nextInt(mainMenuPhrases.length)]);
                card.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_flip));
            }
        });
        card.performClick();
    }

    private void fillDB() {
        if (prefs.getBoolean("isFirst", true))
            new FillDataBaseTask().execute();
        prefs.edit().putBoolean("isFirst", false).apply();
    }

    private void setAddCardsActivityButton() {
        btnAdd = findViewById(R.id.add_btn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddCardsActivity.class));
            }
        });
    }

    private void setGameActivityButton() {
        btnStart = findViewById(R.id.start_btn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GameActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMainCard();
    }

    class FillDataBaseTask extends AsyncTask<Void, Void, Void> {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] allCards = getResources().getString(R.string.values).split(";");

        @Override
        protected Void doInBackground(Void... voids) {
            putCardsIntoDatabase();
            return null;
        }

        private void putCardsIntoDatabase() {
            for (String card : allCards) {
                String[] splitted = card.split("=");
                ContentValues cv = new ContentValues();
                cv.put("question", splitted[0]);
                cv.put("answer", splitted[1]);
                cv.put("level", 0);
                db.insert("cardsTable", null, cv);
            }
            db.close();
        }
    }
}