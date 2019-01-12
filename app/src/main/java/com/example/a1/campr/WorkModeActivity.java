package com.example.a1.campr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class WorkModeActivity extends AppCompatActivity {

//    public void getStarted(View view){
//        Switch userTypeSwitch = (Switch) findViewById(R.id.userTypeSwitch);
//
//        String userType = "Adopter";
//        if (userTypeSwitch.isChecked()){
//            userType = "Lister";
//            Intent intent = new Intent(getApplicationContext(),ListerActivity.class);
//        }else{
//            Intent intent = new Intent(getApplicationContext(), AdopterActivity.class);
//        }
//    }

    private Button mstart;
    private Switch mswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workmode);

        getSupportActionBar().hide();

        mstart = (Button) findViewById(R.id.start);
        mswitch = (Switch) findViewById(R.id.userTypeSwitch);
        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mswitch.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), ListerActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), AdopterActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
}
