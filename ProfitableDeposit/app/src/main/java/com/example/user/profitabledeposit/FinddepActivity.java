package com.example.user.profitabledeposit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URL;
import java.util.ArrayList;

import bankparse.AlfaBank;
import bankparse.BMaths;
import bankparse.Deposit;
import bankparse.Gazprom_Parse;
import bankparse.RCH_Parse;
import bankparse.Sberbank_Parse;
import bankparse.VTB_Parse;

public class FinddepActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finddep);
        setOnListeners();
    }

    private ProgressDialog pd;
    public ArrayList<Deposit> alldep = new ArrayList<Deposit>();
    public ArrayList<String> output;

    private void setOnListeners()
    {
        Button butSearch = (Button) findViewById(R.id.search_button);
        butSearch.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        pd = ProgressDialog.show(FinddepActivity.this, "Поиск...", "Ищем вклады", true, false);
                        new ParseSite().execute("http://www.sberbank.ru/ru/person/contributions/depositsnew",
                                "https://www.rshb.ru/natural/deposits/",
                                "http://www.gazprombank.ru/personal/invest_savings/deposits/",
                                "https://www.vtb.ru/personal/vklady-i-scheta/",
                                "https://alfabank.ru/make-money/deposits/");
                    }
                }
        );
    }

    private class ParseSite extends AsyncTask<String, Void, ArrayList<String>>
    {
        //Фоновая операция
        protected ArrayList<String> doInBackground(String... arg)
        {
            output = new ArrayList<String>();

            Sberbank_Parse sbp = new Sberbank_Parse();
            RCH_Parse rch = new RCH_Parse();
            Gazprom_Parse gpb = new Gazprom_Parse();
            VTB_Parse vtb = new VTB_Parse();
            AlfaBank alf = new AlfaBank();

            BMaths bm = new BMaths();

            int init; long days;
            EditText d1 = (EditText) findViewById(R.id.start_date);
            EditText d2 = (EditText) findViewById(R.id.close_date);
            EditText e1 = (EditText) findViewById(R.id.initial_sum);
            init = Integer.parseInt(e1.getText().toString());
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
            DateTime startD;
            DateTime endD;
            Duration mills;


            try
            {
                sbp.SbParse(new URL(arg[0]));
                rch.RCHParse(new URL(arg[1]));
                gpb.GazpromParse(new URL(arg[2]));
                alf.Alfa_Parse(new URL(arg[4]));


                startD = formatter.parseDateTime(d1.getText().toString());
                endD = formatter.parseDateTime(d2.getText().toString());
                mills = new Duration(startD, endD);

               days = mills.getStandardDays();

               vtb.VTBParse(new URL(arg[3]), FinddepActivity.this, days, init);

               for (Deposit el : sbp.SbDep)
                 sbp.ParseExcel(FinddepActivity.this, el, days, init);

               for (Deposit el : rch.RCHDep)
                   rch.RCH_Excel(FinddepActivity.this, el, days, init);

               for (Deposit el : gpb.GPBDep)
                   gpb.GAZ_Excel(FinddepActivity.this, el, days, init);

               for (Deposit el : sbp.SbDep)
                    if (!el.isExcel)
                        alldep.add(el);

                for (Deposit el : rch.RCHDep)
                    if (!el.isExcel)
                        alldep.add(el);

                for (Deposit el : gpb.GPBDep)
                    if (!el.isExcel)
                        alldep.add(el);

                for (Deposit el : sbp.moreDeps)
                    if (el.isTermOk)
                        alldep.add(el);

                for (Deposit el : rch.moreDeps)
                    if (el.isTermOk)
                        alldep.add(el);

                for (Deposit el : gpb.moreDeps)
                    if (el.isTermOk)
                        alldep.add(el);

                alldep.addAll(vtb.VTBdeps);


               bm.sortByProfit(alldep, init, days);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            for(Deposit tempd : alldep)
            {
                if (tempd.profit > 0)
                    output.add(tempd.toString());
            }

            return output;
        }

        protected void onPostExecute(ArrayList<String> output)
        {
            pd.dismiss();

            Intent showres = new Intent(".resultsofsearch");
            showres.putStringArrayListExtra("output", output);
            startActivity(showres);
        }
    }
}
