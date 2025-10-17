package com.mycompany.controlfichaje.dao;

public class Fichaje {
    private int id;
    private String nombre;
    private String apellido;
    private String rol;
    private String fecha;
    private String entrada;
    private String salida;
    private int descanso;
    private int comida;
    private int horasSemanales;
    private boolean estado;
    
    public Fichaje(int id, String nombre, String apellido, String rol, String fecha, 
                  String entrada, String salida, int descanso, int comida, 
                  int horasSemanales, boolean estado) {
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
    
    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRol() { return rol; }
    public String getFecha() { return fecha; }
    public String getEntrada() { return entrada; }
    public String getSalida() { return salida; }
    public int getDescanso() { return descanso; }
    public int getComida() { return comida; }
    public int getHorasSemanales() { return horasSemanales; }
    public boolean getEstado() { return estado; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setRol(String rol) { this.rol = rol; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setEntrada(String entrada) { this.entrada = entrada; }
    public void setSalida(String salida) { this.salida = salida; }
    public void setDescanso(int descanso) { this.descanso = descanso; }
    public void setComida(int comida) { this.comida = comida; }
    public void setHorasSemanales(int horasSemanales) { this.horasSemanales = horasSemanales; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    // Método para calcular las horas trabajadas
    public double calcularHorasTrabajadas() {
        try {
            if (entrada == null || salida == null || entrada.trim().isEmpty() || salida.trim().isEmpty()) {
                return 0.0;
            }

            java.time.LocalTime entradaTime = java.time.LocalTime.parse(entrada);
            java.time.LocalTime salidaTime = java.time.LocalTime.parse(salida);

            long minutos = java.time.Duration.between(entradaTime, salidaTime).toMinutes();
            if (minutos < 0) {
                // Si salida es antes que entrada (datos incoherentes), no computar
                return 0.0;
            }

            int totalDescansos = Math.max(0, descanso) + Math.max(0, comida);
            long minutosEfectivos = Math.max(0, minutos - totalDescansos);
            return minutosEfectivos / 60.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}