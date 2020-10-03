package com.sully11.android.familymapclient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.sully11.android.familymapclient.FamilyData;
import com.sully11.android.familymapclient.R;

public class SettingActivity extends AppCompatActivity {

    Context mContext = this;
    FamilyData fd = FamilyData.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        Switch lifeEventSwitch = (Switch) findViewById(R.id.lifeEventSwitch);
        if(fd.isLifeStoryLinesOn()) { lifeEventSwitch.setChecked(true); }
        lifeEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setLifeStoryLinesOn(true);
                } else {
                    fd.setLifeStoryLinesOn(false);
                }
            }
        });

        Switch familyEventSwitch = (Switch) findViewById(R.id.familyEventSwitch);
        if(fd.isFamilyTreeLinesOn()) { familyEventSwitch.setChecked(true); }
        familyEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setFamilyTreeLinesOn(true);
                } else {
                    fd.setFamilyTreeLinesOn(false);
                }
            }
        });

        Switch spouseEventSwitch = (Switch) findViewById(R.id.spouseEventSwitch);
        if(fd.isSpouseLinesOn()) { spouseEventSwitch.setChecked(true); }
        spouseEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setSpouseLinesOn(true);
                } else {
                    fd.setSpouseLinesOn(false);
                }
            }
        });

        Switch fatherEventSwitch = (Switch) findViewById(R.id.fatherEventSwitch);
        if(fd.isFatherSideOn()) { fatherEventSwitch.setChecked(true); }
        fatherEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setFatherSideOn(true);
                } else {
                    fd.setFatherSideOn(false);
                }
            }
        });

        Switch motherEventSwitch = (Switch) findViewById(R.id.motherEventSwitch);
        if(fd.isMotherSideOn()) { motherEventSwitch.setChecked(true); }
        motherEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setMotherSideOn(true);
                } else {
                    fd.setMotherSideOn(false);
                }
            }
        });

        Switch maleEventSwitch = (Switch) findViewById(R.id.maleEventSwitch);
        if(fd.isMaleEventsOn()) { maleEventSwitch.setChecked(true); }
        maleEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setMaleEventsOn(true);
                } else {
                    fd.setMaleEventsOn(false);
                }
            }
        });

        Switch femaleEventSwitch = (Switch) findViewById(R.id.femaleEventSwitch);
        if(fd.isFemaleEventsOn()) { femaleEventSwitch.setChecked(true); }
        femaleEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fd.setFemaleEventsOn(true);
                } else {
                    fd.setFemaleEventsOn(false);
                }
            }
        });


        RelativeLayout logoutLayout = (RelativeLayout) findViewById(R.id.logoutLayout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamilyData fd = FamilyData.getInstance();
                fd.clearClient();

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
