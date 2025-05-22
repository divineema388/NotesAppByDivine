package com.example.notesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DevelopersAdapter extends RecyclerView.Adapter<DevelopersAdapter.DevViewHolder> {

    private final List<String> devList;

    public DevelopersAdapter(List<String> devList) {
        this.devList = devList;
    }

    @NonNull
    @Override
    public DevViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_developer, parent, false);
        return new DevViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevViewHolder holder, int position) {
        holder.devNameTextView.setText(devList.get(position));
    }

    @Override
    public int getItemCount() {
        return devList.size();
    }

    static class DevViewHolder extends RecyclerView.ViewHolder {
        TextView devNameTextView;

        public DevViewHolder(@NonNull View itemView) {
            super(itemView);
            devNameTextView = itemView.findViewById(R.id.textViewDevName);
        }
    }
}
