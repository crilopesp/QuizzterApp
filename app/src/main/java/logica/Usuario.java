/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logica;

import java.sql.Timestamp;

/**
 *
 * @author marcos
 */
public class Usuario {
    private long idusuario;
    private String usuario;
    private String nombre;
    private int aciertos;
    private int fallos;
    private double puntuacion;
    private Timestamp fecha_insercion;

    public Usuario(long idusuario, String usuario, String nombre) {
        this.idusuario = idusuario;
        this.usuario = usuario;
        this.nombre = nombre;
    }

    public Usuario(long idusuario, String usuario, String nombre, int aciertos, int fallos, double puntuacion, Timestamp fecha_insercion) {
        this.idusuario = idusuario;
        this.usuario = usuario;
        this.nombre = nombre;
        this.aciertos = aciertos;
        this.fallos = fallos;
        this.puntuacion = puntuacion;
        this.fecha_insercion = fecha_insercion;
    }

    public long getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(long idusuario) {
        this.idusuario = idusuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAciertos() {
        return aciertos;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public int getFallos() {
        return fallos;
    }

    public void setFallos(int fallos) {
        this.fallos = fallos;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Timestamp getFecha_insercion() {
        return fecha_insercion;
    }

    public void setFecha_insercion(Timestamp fecha_insercion) {
        this.fecha_insercion = fecha_insercion;
    }
    
    
    
    
}
