package com.example.user.profitabledeposit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setListeners();
    }



    public void setListeners()
    {
        Button butFind = (Button) findViewById(R.id.find_dep);
        Button butList = (Button)findViewById(R.id.list_dep);
        Button butSave = (Button) findViewById(R.id.save_dep);

        butFind.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent show_find = new Intent(".FinddepActivity");
                        startActivity(show_find);
                    }
                }
        );

        butList.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent show_find = new Intent(".OfferList");
                        startActivity(show_find);
                    }
                }
        );

        butSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent show_save = new Intent(".SaveDep");
                        startActivity(show_save);
                    }
                }
        );
    }

}

