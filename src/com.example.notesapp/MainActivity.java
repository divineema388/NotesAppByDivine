package com.example.notesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "noteContent.txt.txt";

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private ArrayList<Note> notesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesList = new ArrayList<>();
        loadNotes();

        adapter = new NotesAdapter(notesList, new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                showEditNoteDialog(position);
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAddNote);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateNoteDialog();
            }
        });
    }

    private void showCreateNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Note Title");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setHint("Enter note title");
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                String title = input.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check duplicate titles
                for (Note note : notesList) {
                    if (note.getTitle().equals(title)) {
                        Toast.makeText(MainActivity.this, "Title already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                notesList.add(new Note(title, ""));
                adapter.notifyItemInserted(notesList.size() - 1);
                saveNotes();
                showEditNoteDialog(notesList.size() -1);
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showEditNoteDialog(final int position) {
        Note note = notesList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note: " + note.getTitle());

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_edit_note, null, false);
        final EditText inputContent = viewInflated.findViewById(R.id.editTextNoteContent);
        inputContent.setText(note.getContent());

        builder.setView(viewInflated);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newContent = inputContent.getText().toString();
                note.setContent(newContent);
                adapter.notifyItemChanged(position);
                saveNotes();
                Toast.makeText(MainActivity.this, "Note saved.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                confirmDeleteNote(position);
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void confirmDeleteNote(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete \"" + notesList.get(position).getTitle() + "\"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        notesList.remove(position);
                        adapter.notifyItemRemoved(position);
                        saveNotes();
                        Toast.makeText(MainActivity.this, "Note deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Persist notes to internal file as JSON array of objects {title, content}
    private void saveNotes() {
        JSONArray jsonArray = new JSONArray();
        for (Note note : notesList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", note.getTitle());
                obj.put("content", note.getContent());
                jsonArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonString = jsonArray.toString();

        try (FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            writer.write(jsonString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save notes", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotes() {
        notesList.clear();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            if (!json.isEmpty()) {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String title = obj.getString("title");
                    String content = obj.getString("content");
                    notesList.add(new Note(title, content));
                }
            }
        } catch (IOException | JSONException e) {
            // No file or error reading file - start fresh
            e.printStackTrace();
        }
    }
}


