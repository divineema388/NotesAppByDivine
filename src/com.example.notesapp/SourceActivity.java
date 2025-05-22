package com.example.notesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SourceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);

        TextView repoLink = findViewById(R.id.textViewRepoLink);
        repoLink.setText("GitHub Repository: https://github.com/divineema388/NotesAppByDivine");
        repoLink.setMovementMethod(LinkMovementMethod.getInstance()); // make the link clickable
        repoLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/divineema388/NotesAppByDivine"));
            startActivity(browserIntent);
        });

        TextView appInfo = findViewById(R.id.textViewAppInfo);
        appInfo.setText("This app is a modern polished Notes application with create, edit, save, and delete functionalities.\n\nDeveloped using Java and Android SDK.");
    }
}
