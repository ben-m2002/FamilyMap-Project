package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.material.slider.Slider;

import Data.DataCache;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch lifeStoryOptions = findViewById(R.id.life_story_switch);
        Switch familyTreeLinesOptions = findViewById(R.id.family_tree_switch);
        Switch spouseLinesOptions = findViewById(R.id.spouse_lines_switch);
        Switch fathersSideOptions = findViewById(R.id.father_side_switch);
        Switch mothersSideOptions = findViewById(R.id.mother_side_switch);
        Switch maleEventOptions = findViewById(R.id.male_event_switch);
        Switch femaleEventOptions = findViewById(R.id.female_event_switch);

        View logout = findViewById(R.id.rlayout_logout);


        lifeStoryOptions.setChecked(DataCache.getInstance().isLifeStoryLines());
        familyTreeLinesOptions.setChecked(DataCache.getInstance().isFamilyTreeLines());
        spouseLinesOptions.setChecked(DataCache.getInstance().isSpouseLines());
        fathersSideOptions.setChecked(DataCache.getInstance().isFatherSideLines());
        mothersSideOptions.setChecked(DataCache.getInstance().isMotherSideLines());
        maleEventOptions.setChecked(DataCache.getInstance().isMaleEventLines());
        femaleEventOptions.setChecked(DataCache.getInstance().isFemaleEventLines());

        lifeStoryOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setLifeStoryLines(!data.isLifeStoryLines());
            }
        });

        familyTreeLinesOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setFamilyTreeLines(!data.isFamilyTreeLines());
            }
        });

       spouseLinesOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setSpouseLines(!data.isSpouseLines());
            }
        });

        fathersSideOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setFatherSideLines(!data.isFatherSideLines());
            }
        });

       mothersSideOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setMotherSideLines(!data.isMotherSideLines());
            }
        });

        maleEventOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setMaleEventLines(!data.isMaleEventLines());
            }
        });

        femaleEventOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch view = (Switch)v;
                DataCache data = DataCache.getInstance();
                DataCache.getInstance().setFemaleEventLines(!data.isFemaleEventLines());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.getInstance().clear();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
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
}