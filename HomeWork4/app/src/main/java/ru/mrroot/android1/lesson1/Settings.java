package ru.mrroot.android1.lesson1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity {

    private SwitchMaterial mSwitchDarkTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        Boolean isNightTheme = intent.getBooleanExtra(MainActivity.NAME_KEY_NIGH_THEME,false);

        if (isNightTheme) {
            setTheme(R.style.MyDarkThemes);
        } else {
            setTheme(R.style.MyThemes);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSwitchDarkTheme = findViewById(R.id.switchTheme);
        mSwitchDarkTheme.setChecked(isNightTheme);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener((view) -> {
            Intent result = new Intent();
            result.putExtra(MainActivity.NAME_KEY_NIGH_THEME, mSwitchDarkTheme.isChecked());
            setResult(RESULT_OK, result);
            finish();
        });

    }
}