package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import Autenticacion.Autenticacion;

/**
 * Servlet de manejo de login. Recibe correo y contraseña desde el formulario
 * y delega la autenticación a Autenticacion.hacerLogin.
 *
 * Flujo posterior al login:
 * - Si el usuario tiene un fichaje activo o está "En producción": redirige a perfil.jsp.
 * - Si es admin: redirige a admin.jsp.
 * - Si es usuario normal: redirige a bienvenido.jsp.
 * - Caso especial: credenciales "externo/externo123" para acceso de sólo lectura a externo.jsp.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /**
     * Procesa el envío del formulario de login (POST).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // Validar y crear sesión por correo
        boolean loginExitoso = Autenticacion.hacerLogin(request, correo, contrasena);
        if (loginExitoso) {
            // Si el usuario tiene un fichaje activo, ir directamente a perfil.jsp
            HttpSession session = request.getSession();
            String usuarioSesion = Autenticacion.obtenerUsuarioActual(request);

            // Preferir datos reales del usuario desde BD; si no, fallback por espacios
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
            // Si el usuario tiene fichaje activo o está "En producción", ir a perfil.jsp
            if (activo != null || (u != null && "En producción".equalsIgnoreCase(u.getDescripcion()))) {
                if (activo != null) {
                    // Precarga de atributos de sesión útiles para la vista de perfil
                    session.setAttribute("horaEntrada", java.time.LocalDateTime.of(activo.getFecha(), activo.getEntrada()));
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                }
                response.sendRedirect("perfil.jsp");
                return;
            }

            // Si no hay fichaje activo ni "En producción", ir al panel correspondiente
            if ("admin".equals(Autenticacion.obtenerRol(request))) {
                response.sendRedirect("admin.jsp");
            } else {
                response.sendRedirect("bienvenido.jsp");
            }
        } else if ("externo".equals(correo) && "externo123".equals(contrasena)) {
            // Login para externo
            HttpSession session = request.getSession();
            session.setAttribute("usuario", correo);
            session.setAttribute("rol", "externo");
            response.sendRedirect("externo.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }
}
