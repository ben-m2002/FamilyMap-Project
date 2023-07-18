package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import Data.DataCache;
import Models.Event;
import Models.Person;

public class MapsFragment extends Fragment {


    public GoogleMap map;
    public int standardWith = 10;
    private int spouseLineColor = R.color.pink;
    public int familyTreeLineColor = R.color.black;
    public int lifeEventLineColor = R.color.yellow;
    public Map<Marker, Event> markerEvent = new HashMap<>(); // associates a Marker to an event
    public Map<Event, Marker> eventMarker = new HashMap<>(); // associates an Event to a marker
    public Event currentEvent = null;

    //Settings

    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = true;
    private boolean fatherSideLines = true;
    private boolean motherSideLines = true;
    private boolean maleEventLines = true;
    private boolean femaleEventLines = true;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            TextView mapInfoView = (TextView) getView().findViewById(R.id.LayoutMapTextView);
            map = googleMap;
            createMarkers(googleMap);



            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    // clear the map
                    mapReset(googleMap);
                    DataCache dc = DataCache.getInstance();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                    Event selected = markerEvent.get(marker);
                    currentEvent = selected;

                    //System.out.println(selected.getEventType() + " " + selected.getEventID());

                    // create event markers

                    if (familyTreeLines){
                        drawFamilyTreeLine(selected);
                    }
                    if (spouseLines){
                        drawSpouseLine(selected);
                    }
                    if (lifeStoryLines){
                        drawLifeEventLines(selected);
                    }



                    // make bottom of the textField talk about event
                    TextView bottom = (TextView) getView().findViewById(R.id.LayoutMapTextView);
                    Person p = dc.getPersons().get(selected.getPersonID());
                    bottom.setText(p.getFirstName() + " " + p.getLastName()
                    + "\n" + selected.getEventType() + ":"+ " " + selected.getCity() +
                            ", " + selected.getCountry() + " (" + selected.getYear() +
                            ")");

                    return true;
                }
            });

            mapInfoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Person currentPerson = DataCache.getInstance().getPersons().get(currentEvent.getPersonID());
                    DataCache.getInstance().currentViewPerson = currentPerson;
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    startActivity(intent);
                }
            });

        }

    };

    private void selectMarker (GoogleMap googleMap, Event selected){

        Marker marker = eventMarker.get(selected);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        DataCache dc = DataCache.getInstance();

        if (familyTreeLines){
            drawFamilyTreeLine(selected);
        }
        if (spouseLines){
            drawSpouseLine(selected);
        }
        if (lifeStoryLines){
            drawLifeEventLines(selected);
        }

        // change the color


        // make bottom of the textField talk about event and chance color
        TextView bottom = (TextView) getView().findViewById(R.id.LayoutMapTextView);
        Person p = dc.getPersons().get(selected.getPersonID());
        bottom.setText(p.getFirstName() + " " + p.getLastName()
                + "\n" + selected.getEventType() + ":"+ " " + selected.getCity() +
                ", " + selected.getCountry() + " (" + selected.getYear() +
                ")");
        Person person = dc.getPersons().get(selected.getPersonID());

        if (person.getGender().equals("m")) {
            bottom.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.lightblue));
        }
        else{
            bottom.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.pink));
        }
    }

    private void createMarkers (GoogleMap googleMap){
        map = googleMap; // set the map value

        // clear

        googleMap.clear();

        // initialize values

        System.out.println("remaking this");

        // Load some Data Points

        DataCache dc = DataCache.getInstance();
        Map<String, String> eventTypes = new HashMap<>(); // eventType color
        final int HUE_SIZE = 5;
        float[] eventHues = new float [HUE_SIZE];
        eventHues[0] = 240.0f; eventHues[1] = 120.0f; eventHues[2] = 30.0f;
        eventHues[3] = 280.0f; eventHues[4] = 0.0f;
        int colorCount = 0;

        // set the default for the Map Fragment

        // draw up all the event markers

        for (Event e : dc.getAllValidEvents()){
            LatLng pos = new LatLng(e.getLatitude(), e.getLongitude());
            Marker mapMarker = googleMap.addMarker(new MarkerOptions().position(pos).title(e.getCity()));
            markerEvent.put(mapMarker, e);
            eventMarker.put(e, mapMarker);
            if (colorCount > HUE_SIZE - 1){
                colorCount = 0;
            }
            if (eventTypes.keySet().contains(e.getEventType())){
                mapMarker.setIcon(BitmapDescriptorFactory.defaultMarker(
                        Float.parseFloat(eventTypes.get(e.getEventType()))));
            }
            else{
                eventTypes.put(e.getEventType(), String.valueOf(eventHues[colorCount]));
                mapMarker.setIcon(BitmapDescriptorFactory.defaultMarker(
                        Float.parseFloat(eventTypes.get(e.getEventType()))
                ));
                colorCount = colorCount + 1;
            }
        }

        // auto select

        if (DataCache.getInstance().currentEvent != null){
            currentEvent = DataCache.getInstance().currentEvent;
            selectMarker(googleMap, DataCache.getInstance().currentEvent);
            DataCache.getInstance().currentEvent = null; // important
        }

    }

    private void mapReset (GoogleMap googleMap){
        googleMap.clear();
        createMarkers(googleMap);
    }

    public Event getEarliestEvent (Person p){
        if (p == null){return null;}
        Vector<Event> personEvents = DataCache.getInstance().getPersonEvents().get(p.getPersonID());
        Event birth = null;
        Event earliest = null;
        for (Event e : personEvents){
            if (e.getEventType().equals("birth") && DataCache.getInstance().getAllValidEvents()
                    .contains(e)){
                birth = e;
            }
        }
        if (birth != null){
           return birth;
        }
        else {
            // no birth event no problem
            int y = 2000000; // trying to find years less than this to find the min year
            for (Event e : personEvents ) {
                if (e.getYear() < y && DataCache.getInstance().getAllValidEvents()
                        .contains(e)) {
                    y = e.getYear();
                    earliest = e;
                }
            }
            return earliest;
        }
    }

    void drawSpouseLine (Event main){
        Person corresponding = DataCache.getInstance().getPersons().
                get(DataCache.getInstance().getPersons().
                get(main.getPersonID()).getSpouseID());
        Event e = getEarliestEvent(corresponding);
        if (e != null && DataCache.getInstance().getAllValidEvents()
                .contains(e)) {
            drawLine(main, e, spouseLineColor, standardWith);
        }
    }

    public void FamilyTreeHelper (Event main, Vector<Person> m, int width){
        if (m == null){
            return;
        }
        Event earliestDad = getEarliestEvent(m.elementAt(0));
        Event earliestMom = getEarliestEvent(m.elementAt(1));
        if (earliestDad != null){
            drawLine(main,earliestDad, familyTreeLineColor, standardWith);
            FamilyTreeHelper(earliestDad, DataCache.getInstance().getParents().
                    get(earliestDad.getPersonID()), width - 15);
        }
        if (earliestMom != null){
            drawLine(main,earliestMom, familyTreeLineColor, standardWith);
            FamilyTreeHelper(earliestMom, DataCache.getInstance().getParents().
                   get(earliestMom.getPersonID()), width - 15);
        }
    }

    void drawFamilyTreeLine (Event main){
        Person p = DataCache.getInstance().getPersons().get(main.getPersonID());
        Vector<Person> m = DataCache.getInstance().getParents().get(p.getPersonID());
        FamilyTreeHelper(main, m, standardWith);
    }

    void drawLifeEventLines (Event main){
        Person p = DataCache.getInstance().getPersons().get(main.getPersonID());
        Vector<Event> personEvents = DataCache.getInstance().getPersonEvents().get(main.getPersonID());

        // get the first event then remove it from the list
        Event earliest = getEarliestEvent(p);
        Event lastEvent = earliest; // the event to connect the line from
        personEvents.remove(earliest);

        // iterative loop through until all the events have been gotten to

        while (!personEvents.isEmpty()){
            Event e = getEarliestEvent(p);
            drawLine(lastEvent, e, lifeEventLineColor, standardWith);
            lastEvent = e;
            personEvents.remove(e);
        }
    }

    void drawLine (Event start, Event end, int googleColor, float width){
        LatLng startPoint = new LatLng(start.getLatitude(), start.getLongitude());
        LatLng endPoint = new LatLng(end.getLatitude(), end.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(getContext().getResources().getColor(googleColor))
                .width(width);

        Polyline line = map.addPolyline(options);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Activity activity = getActivity();

        if (activity instanceof  MainActivity){
            setHasOptionsMenu(true);
        }

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DataCache d = DataCache.getInstance();
        if (lifeStoryLines != d.isLifeStoryLines() || familyTreeLines != d.isFamilyTreeLines() ||
        spouseLines != d.isSpouseLines() || fatherSideLines != d.isFatherSideLines() ||
        motherSideLines != d.isMotherSideLines() || maleEventLines != d.isMaleEventLines() ||
        femaleEventLines != d.isFemaleEventLines()){
            lifeStoryLines = d.isLifeStoryLines();
            familyTreeLines = d.isFamilyTreeLines();
            spouseLines = d.isSpouseLines();
            fatherSideLines = d.isFatherSideLines();
            motherSideLines = d.isMotherSideLines();
            maleEventLines = d.isMaleEventLines();
            femaleEventLines = d.isFemaleEventLines();
            if (map != null) {
                createMarkers(map);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mapmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_menu_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.map_menu_settings:
                Intent intent2 = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}