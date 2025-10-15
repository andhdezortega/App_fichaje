package com.mycompany.controlfichaje;

<<<<<<< HEAD

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import Autenticacion.Autenticacion;
=======
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
>>>>>>> origin/andrea

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
<<<<<<< HEAD
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
=======
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String usuario = request.getParameter("usuario");
    String contrasena = request.getParameter("contrasena");

    HttpSession session = request.getSession();

    // Login para usuario normal
    if ("usuario".equals(usuario) && "1234".equals(contrasena)) {
        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", "usuario");
        response.sendRedirect("bienvenido.jsp");

    // Login para administrador
    } else if ("admin".equals(usuario) && "admin123".equals(contrasena)) {
        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", "admin");
        response.sendRedirect("admin.jsp");

    } else {
        response.sendRedirect("login.jsp?error=1");
    }
}
}
>>>>>>> origin/andrea
