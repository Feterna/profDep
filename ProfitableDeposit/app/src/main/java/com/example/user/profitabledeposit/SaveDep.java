package com.example.user.profitabledeposit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import bankparse.Deposit;

public class SaveDep extends AppCompatActivity
{

    ArrayList<String> userDepList = new ArrayList<String>();
    ArrayList<Deposit> userDep = new ArrayList<Deposit>();
    private boolean created = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_dep);
        setOnClickListeners();
        created = getIntent().getBooleanExtra("flag", false);

        if (created)
        {
            Deposit newD = new Deposit();

            newD.bankName = getIntent().getStringExtra("userBankName");
            newD.name = getIntent().getStringExtra("userDepName");
            newD.initialSum = Integer.parseInt(getIntent().getStringExtra("userInitialSum"));

            userDep.add(newD);

            created = false;
        }

        if (userDep.size() > 0)
        {
            for (Deposit element : userDep)
                userDepList.add(element.userDep());
        }

        if (userDepList.size() > 0)
        {
            ListView listview = (ListView) findViewById(R.id.saved_list);
            listview.setAdapter(new ArrayAdapter<String>(SaveDep.this,
                    android.R.layout.simple_list_item_1, userDepList));
        }
    }

    private void setOnClickListeners()
    {
        Button addDep = (Button) findViewById(R.id.add_save);
        addDep.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent newDep = new Intent(".infoSaveDep");
                        created = true;
                        startActivity(newDep);
                    }
                }
        );
    }




}
