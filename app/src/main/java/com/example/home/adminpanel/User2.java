package com.example.home.adminpanel;

public class User2 {
    public String nombre,correo,clave,as,uid;
    public Double longitud,latitud;

    public User2(){

    }

    public User2(String nombre, String correo, String clave, String as,String uid,Double longitud,Double latitud){
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
        this.as = as;
        this.uid = uid;
        this.longitud = longitud;
        this.latitud = latitud;

    }
}