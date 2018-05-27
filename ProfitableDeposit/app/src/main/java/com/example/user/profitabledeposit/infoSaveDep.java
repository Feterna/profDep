package com.example.user.profitabledeposit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class infoSaveDep extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_save_dep);
        setOnListeners();
    }

    private void setOnListeners()
    {
        Button butInfo = (Button) findViewById(R.id.save_info);

        butInfo.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String bankName;
                        String deposName;
                        String initial;

                        EditText bName = (EditText) findViewById(R.id.for_bank_name);
                        bankName = bName.getText().toString();
                        EditText depName = (EditText) findViewById(R.id.for_deposit_name);
                        deposName = depName.getText().toString();
                        EditText init = (EditText) findViewById(R.id.for_initial_sum);
                        initial = init.getText().toString();

                        Intent addIt = new Intent(".SaveDep");
                        addIt.putExtra("userBankName", bankName);
                        addIt.putExtra("userDepName", deposName);
                        addIt.putExtra("userInitialSum", initial);
                        addIt.putExtra("flag", true);
                        startActivity(addIt);

                    }
                }
        );
    }
}
