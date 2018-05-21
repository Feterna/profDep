package com.example.user.profitabledeposit;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;

import bankparse.Deposit;
import bankparse.Gazprom_Parse;
import bankparse.RCH_Parse;
import bankparse.Sberbank_Parse;

public class OfferList extends AppCompatActivity
{

    ArrayList<String> offers;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);
        offers = new ArrayList<String>();
        pd = ProgressDialog.show(OfferList.this, "Поиск...", "Загружаем вклады", true, false);
        new ParseList().execute("http://www.sberbank.ru/ru/person/contributions/depositsnew",
                "https://www.rshb.ru/natural/deposits/",
                "http://www.gazprombank.ru/personal/invest_savings/deposits/");

    }

    private class ParseList extends AsyncTask<String, Void, ArrayList<String>>
    {
        //Фоновая операция
        protected ArrayList<String> doInBackground(String... arg)
        {
            ArrayList<Deposit> alldep = new ArrayList<Deposit>();

            Sberbank_Parse sbp = new Sberbank_Parse();
            RCH_Parse rch = new RCH_Parse();
            Gazprom_Parse gpb = new Gazprom_Parse();

            try
            {
                sbp.SbParse(new URL(arg[0]));
                rch.RCHParse(new URL(arg[1]));
                gpb.GazpromParse(new URL(arg[2]));

                alldep = sbp.SbDep;
                alldep.addAll(rch.RCHDep);
                alldep.addAll(gpb.GPBDep);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            for(Deposit tempd : alldep)
            {
                offers.add(tempd.info());
            }

            return offers;
        }

        protected void onPostExecute(ArrayList<String> output)
        {
            pd.dismiss();
            ListView listview = (ListView) findViewById(R.id.offer_list);
            listview.setAdapter(new ArrayAdapter<String>(OfferList.this,
                    android.R.layout.simple_list_item_1, output));
        }
    }


}
