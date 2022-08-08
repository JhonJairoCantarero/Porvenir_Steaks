package com.example.home.deliverypanel;


import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdenesDelivery {

    String titulo, descripcion, estado, precio, cliente, img;
    Timestamp fecha;

    public OrdenesDelivery() {
    }

    public OrdenesDelivery(String titulo, String descripcion, String estado, String precio, String cliente, String img, Timestamp fecha) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.precio = precio;
        this.cliente = cliente;
        this.img = img;
        this.fecha = fecha;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getfecha2() {

        Integer anio = fecha.toDate().getYear()+1900;
        Integer mes = fecha.toDate().getMonth()+1;
        Integer dia = fecha.toDate().getDate();
        Integer hora = fecha.toDate().getHours();
        Integer min = fecha.toDate().getMinutes();
        Integer seg = fecha.toDate().getSeconds();
        String fecha = "";
        fecha += anio.toString() + "/";
        fecha += ((mes < 10) ? "0" + mes.toString() : mes.toString()) + "/";
        fecha += (dia < 10) ? "0" + dia.toString() : dia.toString() + " ";
        fecha += (hora < 10) ? "0" + hora.toString() : hora.toString() + ":";
        fecha += (min < 10) ? "0" + min.toString() : min.toString() + ":";
        fecha += (seg < 10) ? "0" + seg.toString() : seg.toString();

        Date f;
        try {
            f = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(fecha);

        }catch (Exception e){
            f = new Date();
        }
        return f;
    }


}