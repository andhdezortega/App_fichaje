package com.mycompany.controlfichaje; 

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import Autenticacion.Autenticacion;

import java.io.IOException;

@WebServlet("/ActualizarEstadoServlet")
public class ActualizarEstadoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar autenticación
        if (!Autenticacion.estaAutenticado(request)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String estado = request.getParameter("estado");

        if (estado != null && !estado.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("estadoUsuario", estado);
            
            // Obtener el usuario autenticado
            String usuario = Autenticacion.obtenerUsuarioActual(request);
            System.out.println("Actualizando estado para usuario: " + usuario + " - Nuevo estado: " + estado);
        }
        
        response.sendRedirect("perfil.jsp");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
