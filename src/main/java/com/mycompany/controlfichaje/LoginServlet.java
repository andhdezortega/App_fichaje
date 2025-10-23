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

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // Usar la clase Autenticacion para validar y crear sesión por correo
        boolean loginExitoso = Autenticacion.hacerLogin(request, correo, contrasena);
        if (loginExitoso) {
            HttpSession session = request.getSession();
            String usuarioSesion = Autenticacion.obtenerUsuarioActual(request);

            String nombre;
            String apellido;
            com.mycompany.controlfichaje.dao.Usuario u = com.mycompany.controlfichaje.dao.UsuarioDAO.obtenerUsuarioPorCorreo(correo);
            if (u != null) {
                nombre = (u.getUsuario() != null) ? u.getUsuario() : usuarioSesion;
                apellido = (u.getApellido() != null) ? u.getApellido() : "";
            } else {
                String[] partes = usuarioSesion.split(" ", 2);
                nombre = partes[0];
                apellido = partes.length > 1 ? partes[1] : "";
            }

            com.mycompany.controlfichaje.dao.FichajeDAO dao = new com.mycompany.controlfichaje.dao.FichajeDAO();
            com.mycompany.controlfichaje.FichajeModel activo = dao.obtenerFichajeActivo(nombre, apellido);

            if (activo != null || (u != null && "En producción".equalsIgnoreCase(u.getDescripcion()))) {
                if (activo != null) {
                    session.setAttribute("horaEntrada", java.time.LocalDateTime.of(activo.getFecha(), activo.getEntrada()));
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                }
                response.sendRedirect("perfil.jsp");
                return;
            }

            if ("admin".equals(Autenticacion.obtenerRol(request))) {
                response.sendRedirect("admin.jsp");
            } else {
                response.sendRedirect("bienvenido.jsp");
            }
        } else if ("admin".equals(correo) && "admin123".equals(contrasena)) {
  
        
        // hardcoded admin y externo sin acceso a BD porque no furula
            HttpSession session = request.getSession();
            session.setAttribute("usuario", correo);
            session.setAttribute("rol", "admin");
            response.sendRedirect("admin.jsp");
            
        } else if ("externo".equals(correo) && "externo123".equals(contrasena)) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", correo);
            session.setAttribute("rol", "externo");
            response.sendRedirect("externo.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    
    }
}

        
