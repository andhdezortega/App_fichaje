package com.mycompany.controlfichaje;

import java.time.LocalDate;
import java.time.LocalTime;

public class FichajeMock {
    public int id;
    public String nombre, apellido, rol;
    public LocalDate fecha;
    public LocalTime entrada, salida;
    public int descanso, comida, horasSemanales;
    public boolean estado;

    // Constructor para crear desde la base de datos
    public FichajeMock(int id, String nombre, String apellido, String rol, 
                       LocalDate fecha, LocalTime entrada, LocalTime salida, 
                       int descanso, int comida, int horasSemanales, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.fecha = fecha;
        this.entrada = entrada;
        this.salida = salida;
        this.descanso = descanso;
        this.comida = comida;
        this.horasSemanales = horasSemanales;
        this.estado = estado;
    }

    // Constructor para crear desde formulario
    public FichajeMock(int id, String n, String a, String r, String f, String e, String s, String d, String c, int hs, boolean est) {
        this.id = id;
        nombre = n;
        apellido = a;
        rol = r;
        fecha = LocalDate.parse(f);
        entrada = LocalTime.parse(e);
        salida = LocalTime.parse(s);
        descanso = Integer.parseInt(d);
        comida = Integer.parseInt(c);
        horasSemanales = hs;
        estado = est;
    }

    // Constructor por defecto para usar en código (por ejemplo desde servlet)
    public FichajeMock() {
        this.id = 0;
        this.nombre = "";
        this.apellido = "";
        this.rol = "";
        this.fecha = LocalDate.now();
        this.entrada = null;
        this.salida = null;
        this.descanso = 0;
        this.comida = 0;
        this.horasSemanales = 0;
        this.estado = false;
    }
}
