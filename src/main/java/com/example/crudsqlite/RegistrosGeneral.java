package com.example.crudsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RegistrosGeneral extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_general);

        tv = findViewById(R.id.textView6);

        Bundle lonchera = getIntent().getExtras();

        String registros = lonchera.getString("registros");

        tv.setText(registros);

    }
}