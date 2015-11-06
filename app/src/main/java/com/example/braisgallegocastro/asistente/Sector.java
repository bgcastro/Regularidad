package com.example.braisgallegocastro.asistente;

import java.io.Serializable;

/**
 *
 * @author Jesus Gandara Martinez
 * @author Brais Gallego Castro
 *
 * clase que respresenta a un sector de un tramo que consta de una distancia y una media a seguir
 *
 */
public class Sector implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Float distancia;
    private Float media;

    public Sector() {
    }

    public Sector(Float distancia, Float media) {
        super();
        this.distancia = distancia;
        this.media = media;
    }

    public Float getDistancia() {
        return distancia;
    }
    public void setDistancia(Float distancia) {
        this.distancia = distancia;
    }
    public Float getMedia() {
        return media;
    }
    public void setMedia(Float media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return new String("Distancia "+distancia.toString()+"Km 	"+"Media "+media.toString()+"Km/h");
    }



}
