package com.example.n3t.n3tandroidapp.feature;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;

public class PersonalProfile extends AppCompatActivity {

    EditText fName, lName, tod, age, gen, exp;
    Button save;
    private String fileName = "n3t_storage.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context c = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        fName = (EditText)findViewById(R.id.firstName);
        lName = (EditText)findViewById(R.id.lastName);
        tod = (EditText)findViewById(R.id.typeDriver);
        age = (EditText)findViewById(R.id.age);
        gen = (EditText)findViewById(R.id.gender);
        exp = (EditText)findViewById(R.id.expOvr);
        save = (Button)findViewById(R.id.savePersonal);

        String[] stringArray = {DetailsStore.getFirstName(), DetailsStore.getLastName(), DetailsStore.getTypeOfDriver(), DetailsStore.getAge(), DetailsStore.getGender(), DetailsStore.getExpOnOVR()};
        EditText[] editTextArray = {fName, lName, tod, age, gen, exp};

        for(int i = 0; i < stringArray.length; i++){
            if(stringArray[i].equals("")){}
            else{
                editTextArray[i].setText(stringArray[i]);
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] StringArray2 = {fName.getText().toString(),lName.getText().toString(),tod.getText().toString(),age.getText().toString(),gen.getText().toString(),exp.getText().toString()};
                DetailsStore.setFirstName(StringArray2[0]);
                DetailsStore.setLastName(StringArray2[1]);
                DetailsStore.setTypeOfDriver(StringArray2[2]);
                DetailsStore.setAge(StringArray2[3]);
                DetailsStore.setGender(StringArray2[4]);
                DetailsStore.setExpOnOVR(StringArray2[5]);

                String text = DetailsStore.getFirstName()+", "+DetailsStore.getLastName()+", "+DetailsStore.getTypeOfDriver()+", "+DetailsStore.getAge()+", "+DetailsStore.getGender()+", "+DetailsStore.getExpOnOVR();
                FileOutputStream fos = null;
                try{
                    fos = openFileOutput(fileName, MODE_PRIVATE);
                    fos.write(text.getBytes());
                    Toast.makeText(c, "Saved to" + getFilesDir()+ "/" + fileName, Toast.LENGTH_LONG).show();
                    fos.close();
                } catch(Exception e){
                    Log.i("Error printing to file", e.getMessage());
                }
            }
        });


    }
}
