package com.example.notesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(int position);
    }

    private List<Note> notesList;
    private OnNoteClickListener listener;

    public NotesAdapter(List<Note> notesList, OnNoteClickListener listener){
        this.notesList = notesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position){
        Note note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        // Optional: show snippet of content or date/time etc.
    }

    @Override
    public int getItemCount(){
        return notesList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        OnNoteClickListener listener;

        public NoteViewHolder(@NonNull View itemView, OnNoteClickListener listener){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewNoteTitle);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                listener.onNoteClick(getAdapterPosition());
            }
        }
    }
}
