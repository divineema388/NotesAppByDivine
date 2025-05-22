package com.example.notesapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDevelopers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> devNames = Arrays.asList(
                "Alice Johnson",
                "Bob Smith",
                "Carol Lee",
                "David Walker",
                "Eva Brown",
                "Frank Green",
                "Grace Hall",
                "Henry Adams",
                "Ivy White",
                "Jack Young",
                "Karen Davis",
                "Leo Wilson",
                "Mona Clark",
                "Nick Lewis",
                "Olivia King",
                "Paul Wright",
                "Quinn Scott",
                "Rachel Turner",
                "Steve Phillips",
                "Tina Bennett"
        );

        DevelopersAdapter adapter = new DevelopersAdapter(devNames);
        recyclerView.setAdapter(adapter);

        TextView companyDesc = findViewById(R.id.textViewCompanyDesc);
        companyDesc.setText("About Our Company:\n\n" +
                "We are a leading software development company dedicated to delivering high-quality applications. " +
                "Our team of talented developers and designers work collaboratively to create modern, reliable, and user-friendly software solutions.");
    }
}
