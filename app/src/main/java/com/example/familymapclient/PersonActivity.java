package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import Data.DataCache;
import Models.Event;
import Models.Person;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);


        DataCache dc = DataCache.getInstance();
        Vector<Person> familyPersons = dc.getPersonFamily(dc.currentViewPerson);
        Vector<Event> personEvents = getOrderedPersonEvents(dc.currentViewPerson, dc);
        Map<Person, String> relationShips = dc.getPersonFamilyRelationShip(dc.currentViewPerson);

        // set current view

        TextView firstNameView = findViewById(R.id.person_activity_fn_placeholder);
        TextView lastNameView = findViewById(R.id.person_activity_ln_placeholder);
        TextView gender = findViewById(R.id.person_activity_gend_placeholder);

        firstNameView.setText(dc.currentViewPerson.getFirstName());
        lastNameView.setText(dc.currentViewPerson.getLastName());
        gender.setText(dc.currentViewPerson.getGender());

        ExpandableListView expandableListView = findViewById(R.id.person_expandable_list_View);
        expandableListView.setAdapter(new ExpandableListAdapter(familyPersons, personEvents, relationShips));
    }

    public Event getEarliestEvent (Person p, Vector<Event> personEvents){
        if (p == null){return null;}
        Event birth = null;
        Event earliest = null;
        for (Event e : personEvents){
            if (e.getEventType().equals("birth")){
                birth = e;
            }
        }
        if (birth != null){
            return birth;
        }
        else {
            // no birth event no problem
            int y = 2000000; // trying to find years less than this to find the min year
            for (Event e : personEvents) {
                if (e.getYear() < y) {
                    y = e.getYear();
                    earliest = e;
                }
            }
            return earliest;
        }
    }

    private Vector<Event> getPersonsEvents (Person p, DataCache dc){
        Vector<Event> allEvents = dc.getAllEvents();
        Vector<Event> personEvents = new Vector<>();

        for (Event e : allEvents){
            if (e.getPersonID().equals(p.getPersonID())){
                personEvents.add(e);
            }
        }

        return personEvents;
    }

    private Vector<Event> getOrderedPersonEvents (Person p, DataCache dc){
        Vector<Event> personEvents = getPersonsEvents(p, dc);
        Vector<Event> orderedPersonEvents = new Vector<>();
        int n = personEvents.size();

        while (orderedPersonEvents.size() != n){
            Event e = getEarliestEvent(p, personEvents);
            personEvents.remove(e);
            orderedPersonEvents.add(e);
        }

        return orderedPersonEvents;
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


    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int PERSON_EVENTS_POSITION = 0;
        private static final int FAMILY_PERSONS_POSITION = 1;

        private final Vector<Person> familyPersons;
        private final Vector<Event> personEvents;
        private  final Map<Person, String> relationShips;

        ExpandableListAdapter(Vector<Person> familyPersons, Vector<Event> personEvents
        , Map<Person,String> relationShips){
            this.familyPersons = familyPersons;
            this.personEvents = personEvents;
            this.relationShips = relationShips;
        }

         @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition){
            switch (groupPosition){
                case PERSON_EVENTS_POSITION:
                    return personEvents.size();
                case FAMILY_PERSONS_POSITION:
                    return familyPersons.size();
                default:
                    throw new IllegalArgumentException( "Unrecognized group position" );
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch( groupPosition ){
                case PERSON_EVENTS_POSITION:
                    return getString(R.string.group_life_events);
                case FAMILY_PERSONS_POSITION:
                    return getString(R.string.group_family);
                default:
                     throw new IllegalArgumentException( "Unrecognizable group position" );
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition){
                case PERSON_EVENTS_POSITION:
                    return personEvents.get(childPosition);
                case FAMILY_PERSONS_POSITION:
                    return familyPersons.get(childPosition);
                default:
                    throw new IllegalArgumentException( "Unrecognized group position" );
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch(groupPosition){
                case PERSON_EVENTS_POSITION:
                    titleView.setText(R.string.group_life_events);
                    break;
                case FAMILY_PERSONS_POSITION:
                    titleView.setText(R.string.group_family);
                    break;
                default:
                    throw new IllegalArgumentException( "Unrecognizable group position " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition){
                case PERSON_EVENTS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.life_event, parent, false);
                    createPersonEventsView(itemView, childPosition);
                    break;
                case FAMILY_PERSONS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family, parent , false);
                    createFamilyPersonsView(itemView, childPosition);
                    break;
                default:
                     throw new IllegalArgumentException( "Unrecognized group position " + groupPosition);
            }

            return  itemView;
        }

        @SuppressLint("SetTextI18n")
        private void createPersonEventsView (View personEventsView, final int childPosition){
            TextView eventNameView = personEventsView.findViewById(R.id.life_event_name);
            TextView eventInfoView = personEventsView.findViewById(R.id.life_event_info);

            Person selected = DataCache.getInstance().currentViewPerson;
            eventNameView.setText(selected.getFirstName() + " " + selected.getLastName());
            Event current = personEvents.get(childPosition);
            eventInfoView.setText(current.getEventType().toUpperCase(Locale.ROOT) + " "
            + current.getCity() + ", " + current.getCountry() + "( " +
                    current.getYear() + ")" );

            personEventsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCache.getInstance().currentEvent = current;
                    Intent intent = new Intent(PersonActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            });

        }

        private void createFamilyPersonsView (View familyPersonView, final int childPosition){
            TextView familyPersonsNameView = familyPersonView.findViewById(R.id.family_name_field);
            TextView familyPersonsRelationsView = familyPersonView.findViewById(R.id.family_relations_field);

            Person p = familyPersons.get(childPosition);

            familyPersonsNameView.setText(p.getFirstName() + " " + p.getLastName());
            familyPersonsRelationsView.setText(relationShips.get(p));

            familyPersonsNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Person familyMember = DataCache.getInstance().getPersonByName(p.getFirstName(), p.getLastName());
                    DataCache.getInstance().currentViewPerson = familyMember;
                    finish();
                    startActivity(getIntent());
                }
            });

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}