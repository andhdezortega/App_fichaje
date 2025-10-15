package com.mycompany.controlfichaje;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import Autenticacion.Autenticacion;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        // Usar la clase Autenticacion para validar y crear sesión
        boolean loginExitoso = Autenticacion.hacerLogin(request, usuario, contrasena);
        if (loginExitoso) {
            response.sendRedirect("bienvenido.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }  
}
