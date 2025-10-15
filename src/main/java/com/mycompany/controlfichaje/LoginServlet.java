package com.mycompany.controlfichaje;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
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