
package com.mycompany.controlfichaje;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class FichajeMock implements Serializable {
    public int id;
    public String nombre, apellido, rol;
    public LocalDate fecha;
    public LocalTime entrada, salida;
    public int descanso, comida, horasSemanales;
    public boolean estado;

    public FichajeMock(int id, String nombre, String apellido, String rol, String fecha, String entrada, String salida,
                       String descanso, String comida, int horasSemanales, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.fecha = LocalDate.parse(fecha);
        this.entrada = LocalTime.parse(entrada);
        this.salida = LocalTime.parse(salida);
        this.descanso = Integer.parseInt(descanso);
        this.comida = Integer.parseInt(comida);
        this.horasSemanales = horasSemanales;
        this.estado = estado;
    }

    // Constructor vacío (necesario si lo vas a usar como bean)
    public FichajeMock() {}
}
