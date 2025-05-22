package com.example.notesapp;

import android.widget.FrameLayout;
import android.content.Intent; // For Intent
import androidx.appcompat.app.AppCompatDelegate; // For AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat; // For SwitchCompat
import android.content.SharedPreferences; // For SharedPreferences
import android.preference.PreferenceManager; // For PreferenceManager
import androidx.appcompat.app.AppCompatDelegate; // For AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat; // For SwitchCompat
import android.content.SharedPreferences; // For SharedPreferences
import android.preference.PreferenceManager; // For PreferenceManager
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.Gravity; // Import for Gravity
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.net.Uri; 

public class MainActivity extends AppCompatActivity {

    private FrameLayout rootLayout;
    private BackgroundAnimator backgroundAnimator;
    private RecyclerView recyclerView; // Declare RecyclerView
    private NotesAdapter adapter; // Declare Adapter
    private ArrayList<Note> notesList; // Declare Notes List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.rootLayout);

        backgroundAnimator = new BackgroundAnimator();
        backgroundAnimator.startBackgroundAnimation(rootLayout);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        // Setup toolbar navigation icon click to open drawer
        topAppBar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();

            int id = menuItem.getItemId();
            if (id == R.id.nav_settings) {
                showSettingsDialog();
                return true;
            } else if (id == R.id.nav_about) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_thanks) {
                openGmailApp();
                return true;
            } else if (id == R.id.nav_source) {
                Intent intent = new Intent(MainActivity.this, SourceActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesList = new ArrayList<>();
        loadNotes();

        adapter = new NotesAdapter(notesList, position -> showEditNoteDialog(position));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAddNote);
        fabAdd.setOnClickListener(v -> showCreateNoteDialog());
    }

    @Override
    protected void onDestroy() {
        if (backgroundAnimator != null) {
            backgroundAnimator.stopBackgroundAnimation();
        }
        super.onDestroy();
    }

    private void openGmailApp() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:pupchatinc@gmail.com")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thank You");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your message goes here.");

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
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
                String title = input.getText().toString();
                if (title.isEmpty()) {
                    return; // Do not create an empty note
                }
                // Logic to create a new note
                Note newNote = new Note(title, ""); // Assuming a Note constructor
                notesList.add(newNote);
                adapter.notifyItemInserted(notesList.size() - 1);
                saveNotes();
                showEditNoteDialog(notesList.size() - 1);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

private void showSettingsDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Settings");

    // Create a layout for the dialog
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);
    builder.setView(dialogView);

    // Find the Switch for Dark Theme
    SwitchCompat switchDarkTheme = dialogView.findViewById(R.id.switchDarkTheme);
    switchDarkTheme.setChecked(isDarkThemeEnabled()); // Set initial state

    // Set listener for the switch
    switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
            setDarkTheme();
        } else {
            setLightTheme();
        }
    });

    // Add a button to rename notes
    builder.setPositiveButton("Rename Notes", (dialog, which) -> {
        showRenameNotesDialog();
    });

    builder.setNegativeButton("Close", null);
    builder.show();
}


private void showAboutDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("About Developers");
    builder.setMessage("This app is developed by BlackboxAI, your intelligent software engineer assistant.");
    builder.setPositiveButton("OK", null);
    builder.show();
}

private void showRenameNotesDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Rename Notes");

    final EditText input = new EditText(this);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    input.setHint("Enter new name for your notes");
    builder.setView(input);

    builder.setPositiveButton("Rename", (dialog, which) -> {
        String newName = input.getText().toString();
        if (!newName.isEmpty()) {
            // Logic to rename notes (you can implement this based on your requirements)
            Toast.makeText(this, "Notes renamed to: " + newName, Toast.LENGTH_SHORT).show();
        }
    });

    builder.setNegativeButton("Cancel", null);
    builder.show();
}

 
 private boolean isDarkThemeEnabled() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    return sharedPreferences.getBoolean("dark_theme", false);
}

private void setDarkTheme() {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    saveThemePreference(true);
}

private void setLightTheme() {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    saveThemePreference(false);
}

private void saveThemePreference(boolean isDark) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("dark_theme", isDark);
    editor.apply();
}


    private void showEditNoteDialog(final int position) {
    Note note = notesList.get(position);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Edit Note: " + note.getTitle());

    View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_edit_note, null, false);
    final EditText inputContent = viewInflated.findViewById(R.id.editTextNoteContent);
    final ToggleButton toggleBold = viewInflated.findViewById(R.id.toggleBold);
    final ToggleButton toggleItalic = viewInflated.findViewById(R.id.toggleItalic);

    // Set content as Spannable to allow formatting
    inputContent.setText(note.getContent());
    inputContent.setSelection(inputContent.getText().length());

    // Animate dialog content fade-in
    viewInflated.setAlpha(0f);
    viewInflated.animate().alpha(1f).setDuration(400).start();

    // Toggle button click listeners to apply/remove formatting on selected text
    toggleBold.setOnCheckedChangeListener((buttonView, isChecked) -> {
        applyStyleToSelection(inputContent, isChecked, Typeface.BOLD);
    });

    toggleItalic.setOnCheckedChangeListener((buttonView, isChecked) -> {
        applyStyleToSelection(inputContent, isChecked, Typeface.ITALIC);
    });

    builder.setView(viewInflated);

    builder.setPositiveButton("Save", (dialog, which) -> {
        String newContent = inputContent.getText().toString();
        note.setContent(newContent);
        adapter.notifyItemChanged(position);
        saveNotes();
        Toast.makeText(MainActivity.this, "Note saved.", Toast.LENGTH_SHORT).show();
    });

    builder.setNeutralButton("Delete", (dialog, which) -> {
        confirmDeleteNote(position);
    });

    builder.setNegativeButton("Cancel", null);

    AlertDialog dialog = builder.create();
    dialog.show();
}

  private void confirmDeleteNote(final int position) {
    if (position < 0 || position >= notesList.size()) {
        Toast.makeText(this, "Invalid note position.", Toast.LENGTH_SHORT).show();
        return; // Exit if the position is invalid
    }

    new AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete \"" + notesList.get(position).getTitle() + "\"?")
            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (position < notesList.size()) { // Check if position is still valid
                        notesList.remove(position);
                        adapter.notifyItemRemoved(position);
                        saveNotes();
                        Toast.makeText(MainActivity.this, "Note deleted.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Note not found.", Toast.LENGTH_SHORT).show();
                    }
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
    
    /**
 * Apply or remove text style (bold or italic) on selected text in EditText.
 */
private void applyStyleToSelection(EditText editText, boolean apply, int style) {
    int start = editText.getSelectionStart();
    int end = editText.getSelectionEnd();

    if (start == end) {
        // No selection: toggle style for entire text
        start = 0;
        end = editText.getText().length();
    }

    android.text.Spannable str = editText.getText();
    if (apply) {
        str.setSpan(new android.text.style.StyleSpan(style), start, end, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    } else {
        android.text.style.StyleSpan[] spans = str.getSpans(start, end, android.text.style.StyleSpan.class);
        for (android.text.style.StyleSpan span : spans) {
            if (span.getStyle() == style) {
                int spanStart = str.getSpanStart(span);
                int spanEnd = str.getSpanEnd(span);
                // Remove span if it overlaps selected range
                if (spanStart < end && spanEnd > start) {
                    str.removeSpan(span);
                }
            }
        }
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


