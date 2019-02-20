package com.example.aqil.angkotcustomerside;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class dialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;

    public dialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.orede_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        Spinner spinnerPemunpang = (Spinner) findViewById(R.id.spinner_capacity);
        Spinner spinnerHalte = (Spinner) findViewById(R.id.spinner_halte);
        String[] items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinnerPemunpang.setAdapter(adapter);
        String[] itemHalte = new String[]{"H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8"};
        ArrayAdapter<String> adapterHalte = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemHalte);
        spinnerHalte.setAdapter(adapterHalte);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:

                      Intent  intent = new Intent(getContext(), TicketActivity.class);
                        getContext().startActivity(intent);
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}