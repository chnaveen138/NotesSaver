package com.example.naveen.notessaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotesActivity extends AppCompatActivity {

    EditText editText;
    Intent intent;
    String notesText;
    String headerText;
    int listItemPosition;
    Date currentTime;
    String currentTimeString;
    boolean viewed = false;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        editText = findViewById(R.id.editText);
        sdf = new SimpleDateFormat("hh:mm aa MMM dd, yyyy");
        intent = getIntent();
        listItemPosition = intent.getIntExtra("listItem", -1);
        if (listItemPosition < 0) {
            currentTime = Calendar.getInstance().getTime();
            currentTimeString = sdf.format(currentTime);
            Log.i("Notes Status", "Adding new note at time: " + currentTimeString);
            editText.setText("");
        } else {
            notesText = MainActivity.notes.get(listItemPosition);
            editText.setText(notesText);
            Log.i("Notes Status", "Showing a note stored at " + MainActivity.times.get(listItemPosition));

        }
    }

    @Override
    protected void onStop() {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.naveen.notessaver", Context.MODE_PRIVATE);
        notesText = editText.getText().toString();
        if (notesText.length() < 30) {
            headerText = notesText;
        } else {
            headerText = notesText.substring(0, 28);
        }
        if (headerText != "") {
            if ((intent.getIntExtra("listItem", -1) < 0)) {
                MainActivity.notes.add(notesText);
                MainActivity.headers.add(headerText);
                MainActivity.times.add("Created On: " + currentTimeString);
                MainActivity.notesAdapter.notifyDataSetChanged();
                Log.i("Notes Status", "Notes Activity Stopping");
            } else {
                MainActivity.notes.set(listItemPosition, notesText);
                MainActivity.headers.set(listItemPosition, headerText);
                MainActivity.notesAdapter.notifyDataSetChanged();
                Log.i("Notes Status", "Changing a note");
            }
        } else {
            Log.i("Notes Status", "Empty note");
        }
        try {
            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notes)).apply();
            sharedPreferences.edit().putString("headers", ObjectSerializer.serialize(MainActivity.headers)).apply();
            sharedPreferences.edit().putString("times", ObjectSerializer.serialize(MainActivity.times)).apply();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Notes Status", "Failed during serialization");
        }
        super.onStop();
    }
}
