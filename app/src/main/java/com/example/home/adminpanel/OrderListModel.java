package com.example.home.adminpanel;


import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderListModel {

    String titulo,precio,img,cliente,estado,descripcion,repartidor;
    Timestamp fecha;

    public OrderListModel() {
    }

    public OrderListModel(String titulo, String precio, String img, String cliente, String estado, String repartidor, String descripcion, Timestamp fecha) {
        this.titulo = titulo;
        this.precio = precio;
        this.img = img;
        this.cliente = cliente;
        this.estado = estado;
        this.repartidor = repartidor;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
