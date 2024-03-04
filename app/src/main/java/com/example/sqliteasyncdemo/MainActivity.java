package com.example.sqliteasyncdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private Button buttonSave;
    private Button buttonView;
    private ListView listView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        buttonSave = findViewById(R.id.buttonSave);

        databaseHelper = new DatabaseHelper(this);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                new InsertDataTask().execute(name);
            }
        });

        buttonView = findViewById(R.id.buttonView);
        listView = findViewById(R.id.listView);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadDataTask().execute();
            }
        });
    }

    private class InsertDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String name = params[0];

            // Perform database insertion in the background
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, name);

            long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);

            // Check if the insertion was successful
            return newRowId != -1;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(MainActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                editTextName.setText(""); // Clear the input field after insertion
            } else {
                Toast.makeText(MainActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            // Retrieve data from the database in the background
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String[] projection = {DatabaseHelper._ID, DatabaseHelper.COLUMN_NAME};
            return db.query(DatabaseHelper.TABLE_NAME, projection, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            // Update the ListView with the retrieved data
            if (cursor != null) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        MainActivity.this,
                        android.R.layout.simple_list_item_2,
                        cursor,
                        new String[]{DatabaseHelper._ID, DatabaseHelper.COLUMN_NAME},
                        new int[]{android.R.id.text1, android.R.id.text2},
                        0
                );

                listView.setAdapter(adapter);
            } else {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
