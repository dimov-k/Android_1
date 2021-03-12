package ru.mrroot.android1.lesson1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {

    private Button[] arrayNumberButton = new Button[10];
    private enum Operations {Division, MULTIPLICATION, MINUS, PLUS, PERCENT, RESULT, CE, DEL}
    private Button[] arrayOperationButton = new Button[Operations.values().length];

    private double number;
    private double resultOperation;
    private Boolean flagNewNum = true;
    private Boolean flagDivBy_0 = false;

    private Operations numberOperation = Operations.RESULT;
    private final String POINT_REPRESENTATION = ".";
    private final String CLEAR_FIELD = "";

    private String numberField = CLEAR_FIELD;
    private EditText fieldNumber;
    private EditText fieldResult;
    static final String NAME_KEY_NIGH_THEME = "isNightTheme";
    private static final int RESULT_SAVE = 1;
    private static boolean isNightTheme = false;
    private static final String prefs = "prefs.xml";


    @Override
    protected void onCreate(Bundle saveInstState) {

        isNightTheme = getSharedPreferences(prefs, MODE_PRIVATE).
                getBoolean(NAME_KEY_NIGH_THEME, false);
        if (isNightTheme) {
            setTheme(R.style.MyDarkThemes);
        } else {
            setTheme(R.style.MyThemes);
        }

        super.onCreate(saveInstState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.keyboard);
        initButtonNumbers();
        fieldNumber = findViewById(R.id.numberField);
        fieldResult = findViewById(R.id.resultField);

        MaterialButton buttonSettings = findViewById(R.id.button_Settings);
        buttonSettings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Settings.class);
            intent.putExtra("isNightTheme", isNightTheme);
            startActivityForResult(intent, RESULT_SAVE);
        });

        upDateWorkField();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent result) {
        super.onActivityResult(requestCode, responseCode, result);

        if (result != null && responseCode == RESULT_OK) {
            isNightTheme = result.getBooleanExtra(NAME_KEY_NIGH_THEME, isNightTheme);
            SharedPreferences sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
            if (sharedPreferences.getBoolean(NAME_KEY_NIGH_THEME, false) != isNightTheme) {
                sharedPreferences.edit().
                        putBoolean(NAME_KEY_NIGH_THEME, isNightTheme).apply();
            }
            recreate();
        }
    }


    public String firstUpperCase(String word){
        if(word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    private void initButtonNumbers(){

        for (int i = 0; i < arrayNumberButton.length; i++){

            String buttonID = "button" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            arrayNumberButton[i] = findViewById(resID);
            int finalI = i;
            arrayNumberButton[i].setOnClickListener(view -> {
                clickNumberButton(String.valueOf(finalI));
            });
        }

        int i = 0;
        for (Operations operation : Operations.values()) {
            String buttonID = "button" + firstUpperCase(operation.name());
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            arrayOperationButton[i] = findViewById(resID);
            if(arrayOperationButton[i] == null){continue;}
            arrayOperationButton[i].setOnClickListener(view -> {
                clickOperationButton(operation);
            });
        }

        Button mButtonPoint = findViewById(R.id.buttonPoint);
        mButtonPoint.setOnClickListener(view -> {
            clickNumberButton(POINT_REPRESENTATION);
        });
    }

    private void clickNumberButton(String arg){

        try {
            if(numberField.length() == 0 && arg.equals(POINT_REPRESENTATION)){
                numberField =  "0" + POINT_REPRESENTATION;
            }
            else{
                if(flagNewNum){
                    numberField = arg;
                }else{
                    numberField += arg;}
            }
            double dpd = Double.parseDouble(numberField);
            number = Double.parseDouble(numberField);

        }catch (NumberFormatException e){
            numberField = numberField.substring(0, numberField.length() - 1);
        }
        if (number == 0 && !numberField.contains(POINT_REPRESENTATION)){
            numberField = CLEAR_FIELD;
        }
        flagNewNum = false;
        upDateWorkField();

    }

    private void clickOperationButton(Operations operation){

        if(Operations.RESULT == operation){
            paymentResult(false);
            return;
        }

        if(Operations.PERCENT == operation){
            paymentResult(true);
            return;
        }

        else if(Operations.DEL == operation){
            if(!flagNewNum){
                delNumberField();}
            else{
                clearNumberField();
            }
            return;
        }
        else if(Operations.CE == operation){
            clearAllFields();
            return;
        }

        if(!flagNewNum){
            paymentResult(false);
        }
        numberOperation = operation;

        upDateInfoField();

    }

    private void paymentResult(Boolean mPercent){

        if(mPercent){
            number = resultOperation * number / 100;
        }

        switch (numberOperation){
            case Division:
                if(number == 0){
                    flagDivBy_0 = true;
                    break;
                }
                resultOperation /= number;
                break;
            case MULTIPLICATION:
                resultOperation *= number;
                break;
            case PLUS:
                resultOperation += number;
                break;
            case MINUS:
                resultOperation -= number;
                break;
            case PERCENT:
                resultOperation = resultOperation * number / resultOperation;
                break;
            case RESULT:
                resultOperation = number;
                break;
        }

        int MAX_ACCURACY = 8;
        double scale = Math.pow(10, MAX_ACCURACY);
        resultOperation = Math.ceil(resultOperation * scale) / scale;

        flagNewNum = true;
        numberField = Double.toString(resultOperation);
        upDateWorkField();

    }

    private void upDateWorkField() {

        if(flagDivBy_0) {
            fieldNumber.setText("аяяяй нельзя делить на 0!");
            flagDivBy_0 = false;
            return;
        }
        if(numberField.length() == 0){
            fieldNumber.setText("0");
            return;
        }

        try {
            if(Float.parseFloat(numberField) % 1 == 0 && numberField.contains(POINT_REPRESENTATION + "0") && flagNewNum){
                numberField = numberField.substring(0, numberField.indexOf(POINT_REPRESENTATION));
            }
        }catch (NumberFormatException ignored){
        }
        fieldNumber.setText(numberField);
        upDateInfoField();

    }

    private void upDateInfoField() {
        if(resultOperation != 0) {
            fieldResult.setText(String.format("%s%s", resultOperation, performOperation()));
        }
        else{
            fieldResult.setText("");
        }
    }

    private String performOperation(){

        String result;

        switch (numberOperation){
            case PLUS:
                result = "+";
                break;
            case MINUS:
                result = "-";
                break;
            case Division:
                result = "/";
                break;
            case MULTIPLICATION:
                result = "*";
                break;
            default:
                result = "=";
                break;
        }

        return result;
    }


    private void delNumberField(){

        if (numberField.length() > 0) {
            numberField = numberField.substring(0, numberField.length() - 1);
            if (numberField.length() > 0) {
                try {
                    number = Float.parseFloat(numberField);
                }catch (NumberFormatException e){
                    Log.e("ERROR", "error converting to Float");
                }
            }else{
                number = 0d;
            }
            upDateWorkField();
        }
    }

    private void clearNumberField(){
        numberField = CLEAR_FIELD;
        flagNewNum = true;
        upDateWorkField();
    }

    private void clearAllFields(){
        numberField = CLEAR_FIELD;
        resultOperation = 0d;
        number = 0d;
        flagNewNum = true;
        numberOperation = Operations.RESULT;
        upDateWorkField();
    }
}