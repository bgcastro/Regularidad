package com.example.braisgallegocastro.asistente;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import android.os.AsyncTask;
import android.os.SystemClock;


public class ServicioUpdater extends AsyncTask<Sector, Object, Void>{

    private  Double distanciaActual, distanciaTotal, distanciaCambio;
    private long tiempoInicio, tiempoActual, tiempoAnterior;
    private Sector sectorActual;
    private int numSectorActual;
    private Float medActual;
    private Double mediaSiguiente;
    private float cont = 0.1f;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Regularidad.chronoUp.setBase(SystemClock.elapsedRealtime());
        Regularidad.chronoUp.start();
    }


    @Override
    protected Void doInBackground(Sector... sectores) {
        sectorActual = sectores[0];
        medActual = sectorActual.getMedia();
        distanciaActual = 0.0;
        distanciaTotal =  Double.valueOf(sectorActual.getDistancia());
        numSectorActual = 0;
        tiempoInicio = 0;
        tiempoAnterior =  System.currentTimeMillis() - 500;

        //Si hay mas de un sector asignamos el valor de media siguiente si no
        if(sectores.length > 1)
            mediaSiguiente = Double.valueOf(sectores[1].getMedia());
        else
            mediaSiguiente = -1.0;	//en el onProgressUpdate() si recibe un valor negativo lo considera no v�lido

        while(numSectorActual <= sectores.length-1){	//Mientras no llegemos al ultimo sector
            //System.err.println(numSectorActual+" de "+sectores.length);
            try {
                if(distanciaActual < distanciaTotal){
                    //guardamos el valor actual
                    tiempoInicio = System.currentTimeMillis();
                    //El tiempo que tard� es la diferencia entre el anterior y el inicio
                    tiempoActual = tiempoInicio - tiempoAnterior;
                    //esperamos medio segundo
                    Thread.sleep((long) 500);
                    //Se calcula la distancia que deberiamos llevar recorrida a la velocidad media actual
                    distanciaActual += (tiempoActual / 3600000.0) * medActual;
                    distanciaCambio = distanciaTotal-distanciaActual;

                    if(Math.abs(distanciaActual-cont) < 0.005)
                    {
                        cont += 0.1;
                        Regularidad.reproductor.play(Regularidad.idSound, 1.0f, 1.0f, 1, 0, 0f);
                    }
                    //Enviamos los datos para que se actualice la UI
                    publishProgress(distanciaActual, Double.valueOf(medActual.toString()), mediaSiguiente,distanciaCambio);
                    //el tiempo de inicio se convierte en el anterior en cada loop
                    tiempoAnterior =  tiempoInicio;
                }else{
                    //llegamos al final del sector, cambiamos de sector

                    if (numSectorActual < sectores.length-1) {
                        //aun hay mas sectores
                        numSectorActual++;
                        sectorActual = sectores[numSectorActual];
                        medActual = sectorActual.getMedia();
                        distanciaTotal += sectorActual.getDistancia();
                        //actualizo la media siguiente
                        if (numSectorActual+1  <= sectores.length-1) {
                            mediaSiguiente = Double.valueOf(sectores[numSectorActual+1].getMedia());
                        }
                        else
                        {
                            mediaSiguiente = -1.0;
                        }


                    }else{
                        numSectorActual++;
                    }
                }

            } catch (InterruptedException e) {
                System.err.println(e.toString());
            }catch (Exception e){
                System.err.println(e.toString());
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        System.err.println("postExecute");
        Regularidad.chronoUp.stop();
        Regularidad.tareaAsincrona = null;
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        Double dis = (Double) values[0];
        DecimalFormat df = new DecimalFormat("###0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        Regularidad.distancia.setText(df.format(dis));

        Double medAct = (Double) values[1];
        Regularidad.mediaActual.setText(medAct.toString());

        Double disCam = (Double) values[3];
        if(disCam > 0.0)
        {
            Regularidad.distanciaDown.setText(df.format(disCam));
        }
        else
        {
            Regularidad.distanciaDown.setText("0.00");
        }

        if((Double)values[2] < 0)
            //valor negativo -> no hay media que mostrar
            Regularidad.mediaSig.setText("-");
        else
        {
            Double medSig = (Double)values[2];
            Regularidad.mediaSig.setText(medSig.toString());
        }
        super.onProgressUpdate(values);
    }



}
