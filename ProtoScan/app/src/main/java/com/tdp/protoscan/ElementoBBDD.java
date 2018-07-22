package com.tdp.protoscan;

public class ElementoBBDD {

    private String nombreRed;
    private String password;

    public ElementoBBDD(String nombre, String pass){

        nombreRed=nombre;
        password=pass;
    }

    public String getNombre(){
        return nombreRed;
    }

    public String getPassword(){
        return password;
    }

}
