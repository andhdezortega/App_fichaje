package com.mycompany.controlfichaje;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/ActualizarFichaje")
public class ActualizarFichaje extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");  
        response.setContentType("text/html;charset=UTF-8");
        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String rol = request.getParameter("rol");
        String fecha = request.getParameter("fecha");
        String entrada = request.getParameter("entrada");
        String salida = request.getParameter("salida");
        int descanso = Integer.parseInt(request.getParameter("descanso"));
        int comida = Integer.parseInt(request.getParameter("comida"));
        int horasSemanales = Integer.parseInt(request.getParameter("horasSemanales"));
        boolean estado = request.getParameter("estado") != null;

        List<FichajeMock> fichajes = (List<FichajeMock>) request.getSession().getAttribute("fichajes");

        if (fichajes != null) {
            for (FichajeMock f : fichajes) {
                if (f.id == id) {
                    f.nombre = nombre;
                    f.apellido = apellido;
                    f.rol = rol;
                    f.fecha = LocalDate.parse(fecha);
                    f.entrada = LocalTime.parse(entrada);
                    f.salida = LocalTime.parse(salida);
                    f.descanso = descanso;
                    f.comida = comida;
                    f.horasSemanales = horasSemanales;
                    f.estado = estado;
                    break;
                }
            }
        }

        response.sendRedirect("admin.jsp");
    }
}
