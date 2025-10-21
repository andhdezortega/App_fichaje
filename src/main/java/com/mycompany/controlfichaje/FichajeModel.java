package com.mycompany.controlfichaje;

import java.time.LocalDate;
import java.time.LocalTime;

public class FichajeModel {
    private int id;
    private String nombre;
    private String apellido;
    private String rol;
    private LocalDate fecha;
    private LocalTime entrada;
    private LocalTime salida;
    private int descanso;
    private int comida;
    private int horasSemanales;
    private boolean estado;

    // Constructor para crear desde la base de datos
    public FichajeModel(int id, String nombre, String apellido, String rol, 
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
    public FichajeModel(int id, String n, String a, String r, String f, String e, String s, String d, String c, int hs, boolean est) {
        this.id = id;
        this.nombre = n;
        this.apellido = a;
        this.rol = r;
        this.fecha = LocalDate.parse(f);
        this.entrada = LocalTime.parse(e);
        this.salida = LocalTime.parse(s);
        this.descanso = Integer.parseInt(d);
        this.comida = Integer.parseInt(c);
        this.horasSemanales = hs;
        this.estado = est;
    }

    // Constructor por defecto para usar en código (por ejemplo desde servlet)
    public FichajeModel() {
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

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalTime entrada) {
        this.entrada = entrada;
    }

    public LocalTime getSalida() {
        return salida;
    }

    public void setSalida(LocalTime salida) {
        this.salida = salida;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }

    public int getComida() {
        return comida;
    }

    public void setComida(int comida) {
        this.comida = comida;
    }

    public int getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(int horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    // Método para calcular horas extra en minutos
    public Integer calcularHorasExtraMinutos() {
        if (entrada == null || salida == null) {
            return 0;
        }
        
        long minutosTrabajados = java.time.Duration.between(entrada, salida).toMinutes();
        int descuentos = Math.max(0, descanso) + Math.max(0, comida);
        long minutosEfectivos = Math.max(0, minutosTrabajados - descuentos);
        
        // Calcular horas semanales en minutos (asumiendo 5 días laborables)
        long minutosSemanales = horasSemanales * 60;
        long minutosDiarios = minutosSemanales / 5;
        
        long minutosExtra = minutosEfectivos - minutosDiarios;
        return (int) Math.max(0, minutosExtra);
    }

    // Método para obtener horas extra en minutos (alias para compatibilidad)
    public Integer getHorasExtraMinutos() {
        return calcularHorasExtraMinutos();
    }

    // Campo para estado de horas extra (pendiente/aprobado)
    private String estadoHorasExtra;

    public String getEstadoHorasExtra() {
        return estadoHorasExtra;
    }

    public void setEstadoHorasExtra(String estadoHorasExtra) {
        this.estadoHorasExtra = estadoHorasExtra;
    }
}
