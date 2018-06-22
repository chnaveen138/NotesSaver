package com.example.naveen.notessaver;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    android.support.v7.app.ActionBar actionBar;
    Intent intent;
    static ArrayList<String> notes;
    static ArrayList<String> headers;
    static ArrayList<String> times;
    static ArrayAdapter<String> notesAdapter;
    ListView notesList;
    SharedPreferences sharedPreferences;
    Date currentTime;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (R.id.addNote == menuItem.getItemId()) {
            intent.putExtra("listItem", -1);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9966ff")));
        notesList = findViewById(R.id.notesList);
        intent = new Intent(this, NotesActivity.class);
        currentTime = Calendar.getInstance().getTime();
        notes = new ArrayList<String>();
        headers = new ArrayList<String>();
        times = new ArrayList<String>();
        sharedPreferences = this.getSharedPreferences("com.example.naveen.notessaver", Context.MODE_PRIVATE);

        try {
            notes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
            headers = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("headers", ObjectSerializer.serialize(new ArrayList<String>())));
            times = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("times", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Notes Status", "Failed during deserialization");
        }
        if (notes.size() <= 0) {
            addNewDialog();
        }

        notesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, headers) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                text.setTextSize(25f);
                return view;
            }
        };
        notesList.setAdapter(notesAdapter);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {
                intent.putExtra("listItem", position);
                startActivity(intent);
                Log.i("List item Position", Integer.toString(position));
            }
        });
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Log.i("Notes Status", "Long Clicking");
                deleteDialog(position);
                return true;
            }
        });


    }

    public void deleteDialog(final int position) {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.trash)
                .setTitle("Are you sure to delete this Item?")
                .setMessage(times.get(position) + "\nNumber of Characters: " + notes.get(position).length())
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(position);
                        headers.remove(position);
                        times.remove(position);
                        notesAdapter.notifyDataSetChanged();
                        try {
                            sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(notes)).apply();
                            sharedPreferences.edit().putString("headers", ObjectSerializer.serialize(headers)).apply();
                            sharedPreferences.edit().putString("times", ObjectSerializer.serialize(times)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("Notes Status", "Deleted List Item " + Integer.toString(position));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Notes status", "Deleting an item cancelled");
                    }
                })
                .show();
    }

    public void addNewDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.pencil)
                .setTitle("Add a note")
                .setMessage("It seems you don't have any previous notes. Want to add one?")
                .setPositiveButton("Add a note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("listItem", -1);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Notes Status", "Adding a new note cancelled");
                    }
                })
                .show();

    }

}
