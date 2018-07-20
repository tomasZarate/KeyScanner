package com.tdp.protoscan;

/**
 * Created by tomi on 22/5/2018.
 */

public class ElementoRed {

    private String nombreRed;
    private String signal;

    public ElementoRed(String nombre, String sig){

        nombreRed=nombre;
        signal=sig;
    }

    public String getNombreRed(){
        return nombreRed;
    }

    public String getPass(){
        return signal;
    }

}
