package com.example.braisgallegocastro.asistente;

import java.util.LinkedList;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kirincostas.regularidad.R;

public class Regularidad extends Activity implements OnClickListener {
    private SpinnerAdapter adapter;
    private LinkedList<Sector> sectores;

    protected static TextView distancia;
    protected static TextView mediaActual;
    protected static TextView mediaSig;
    protected static Chronometer chronoUp;
    protected static TextView distanciaDown;

    protected WakeLock wakelock;

    protected static AsyncTask<Sector, Object, Void> tareaAsincrona;

    protected static SoundPool reproductor;
    protected static int idSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        reproductor = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        idSound = reproductor.load(this,R.raw.bip,1);

        setContentView(R.layout.piramide);
        chronoUp = (Chronometer) findViewById(R.id.chronoUp);

        if(savedInstanceState != null)
        {
            sectores = (LinkedList<Sector>) savedInstanceState.getSerializable("sectores");
            chronoUp.setBase(savedInstanceState.getLong("base"));
            chronoUp.start();
        }
        else
            sectores = new LinkedList<Sector>();

        //sectores.add(new Sector(1f, 30f));
        //sectores.add(new Sector(1f, 60f));

        //zona de binding con la IU
        distancia = (TextView) findViewById(R.id.distancia);
        mediaActual = (TextView)findViewById(R.id.mediaActual);
        mediaSig = (TextView) findViewById(R.id.mediaSig);
        //chronoUp = (Chronometer) findViewById(R.id.chronoUp);
        distanciaDown = (TextView) findViewById(R.id.distanciaDown);

        Button add = (Button) findViewById(R.id.add);
        Spinner spinSectores =  (Spinner) findViewById(R.id.spinner1);

        add.setOnClickListener(this);
        distancia.setOnClickListener(this);

        adapter = new ArrayAdapter<Sector>(this, android.R.layout.simple_spinner_item, sectores);
        spinSectores.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        Sector arraySectores[] ={null};

        if(view.getId() == R.id.distancia && tareaAsincrona == null && sectores.size() > 0){
            reproductor.play(idSound, 1.0f, 1.0f, 1, 0, 0f);
            tareaAsincrona = new ServicioUpdater().execute(sectores.toArray(arraySectores));
        }
        if(view.getId() == R.id.add)
        {
            AlertDialog.Builder builder = new Builder(this);
            //elementos de UI
            final EditText distancia = new EditText(this);
            final EditText media = new EditText(this);
            final TextView lblDistancia = new TextView(this);
            final TextView lblMedia = new TextView(this);

            //configuracion de UI
            lblDistancia.setText("Distancia ");
            lblMedia.setText("Media ");
            distancia.setHint("Km");
            media.setHint("Km/h");

            distancia.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            media.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

            final LinearLayout l = new LinearLayout(this);
            l.addView(lblDistancia, 0);
            l.addView(distancia, 1);
            l.addView(lblMedia, 2);
            l.addView(media, 3);

            builder.setView(l);
            Integer numeroSectores =  sectores.size();
            builder.setTitle("Sector "+numeroSectores.toString());


            //listener del boton aceptar
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (distancia.getText().toString().isEmpty() || media.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Campos vacios", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        sectores.add(new Sector(Float.parseFloat(distancia.getText().toString()), Float.parseFloat(media.getText().toString())));
                    }
                }
            });
            builder.create();
            builder.show();

        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        wakelock.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(sectores != null){
            outState.putSerializable("sectores", sectores);
            outState.putLong("base", chronoUp.getBase());
        }
    }



}
