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
}
