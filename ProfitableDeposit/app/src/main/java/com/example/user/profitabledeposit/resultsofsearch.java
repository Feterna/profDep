package com.example.user.profitabledeposit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class resultsofsearch extends AppCompatActivity
{

    ArrayList<String> outList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        outList = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultsofsearch);
        outList = getIntent().getStringArrayListExtra("output");

        showRes();
    }

    public void showRes()
    {
        ListView listview = (ListView) findViewById(R.id.serach_list);
        listview.setAdapter(new ArrayAdapter<String>(resultsofsearch.this,
                android.R.layout.simple_list_item_1, outList));
    }

}
