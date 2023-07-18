package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import Data.DataCache;
import Models.Event;
import Models.Person;

public class SearchActivity extends AppCompatActivity {


    public static final int SEARCH_EVENT_VIEW = 0;
    public static final int SEARCH_PERSON_VIEW = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText eText = findViewById(R.id.search_box);

        RecyclerView recyclerView = findViewById(R.id.search_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        DataCache dc = DataCache.getInstance();
        Vector<Event> validEvents = dc.getSearchAbleEvents(eText.getText().toString());
        Vector<Person> allPersons = dc.getSearchAblePerson(eText.getText().toString());

        SearchActivityAdapter searchActivityAdapter = new SearchActivityAdapter(validEvents, allPersons);
        recyclerView.setAdapter(searchActivityAdapter);

        eText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text is changing");
                Vector<Event> validEvents = dc.getSearchAbleEvents(eText.getText().toString());
                Vector<Person> allPersons = dc.getSearchAblePerson(eText.getText().toString());
                searchActivityAdapter.validEvents = validEvents;
                searchActivityAdapter.allPersons = allPersons;
                searchActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class SearchActivityAdapter extends RecyclerView.Adapter<searchActivityViewHolder>{

       public Vector<Event> validEvents = null;
       public Vector<Person> allPersons = null;

        public SearchActivityAdapter(Vector<Event> validEvents, Vector<Person> allPersons){
            this.validEvents = validEvents;
            this.allPersons = allPersons;
        }

        @Override
        public int getItemViewType(int position) {
            return position < validEvents.size() ? SEARCH_EVENT_VIEW : SEARCH_PERSON_VIEW;
        }

        public searchActivityViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
            View view;

            if (viewType == SEARCH_EVENT_VIEW){
                view = getLayoutInflater().inflate(R.layout.search_event, parent, false);
            }else{
                view = getLayoutInflater().inflate(R.layout.search_person, parent, false);
            }

            return new searchActivityViewHolder(view, viewType);
        }

        public void onUpdate (Vector<Event> validEvents, Vector<Person> allPersons){


        }

        @Override
        public void onBindViewHolder(@NonNull searchActivityViewHolder holder, int position) {
            if (position < validEvents.size()){
                holder.bind(validEvents.get(position));
            }else{
                holder.bind(allPersons.get(position - validEvents.size()));
            }
        }

        @Override
        public int getItemCount() {
            return validEvents.size() + allPersons.size();
        }

    }

    private class searchActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView nameHolder;
        private final TextView eventDescription;

        private final int ViewType;
        private Event e;
        private Person p;


        public searchActivityViewHolder(@NonNull View itemView, int ViewType) {
            super(itemView);
            this.ViewType = ViewType;

            itemView.setOnClickListener(this);

            if (ViewType == SEARCH_EVENT_VIEW){
                nameHolder = itemView.findViewById(R.id.search_event_name);
                eventDescription = itemView.findViewById(R.id.search_event_info);
            }
            else{
                nameHolder = itemView.findViewById(R.id.search_person_name_holder);
                eventDescription = null;
            }

        }

        private void bind (Event e){
            Person p = DataCache.getInstance().getPersons().get(e.getPersonID());
            eventDescription.setText(e.getEventType().toUpperCase(Locale.ROOT) + " : " +
                    e.getCity() + ", " + e.getCountry() + " (" + e.getYear() + ")");
            this.nameHolder.setText(p.getFirstName() + " "+
                    p.getLastName());
        }

        private void bind (Person p){
            this.nameHolder.setText(p.getFirstName() + " "+
                    p.getLastName());
        }

        @Override
        public void onClick(View v) {

        }
    }

}