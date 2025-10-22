package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import Autenticacion.Autenticacion;
import com.mycompany.controlfichaje.dao.FichajeDAO;
import com.mycompany.controlfichaje.dao.UsuarioDAO;
import com.mycompany.controlfichaje.dao.Usuario;

/**
 * Servlet principal para registrar fichajes (entrada/salida).
 *
 * Acciones soportadas:
 * - "entrada": crea un nuevo fichaje con hora de entrada y estado=false (abierto).
 * - "salida": cierra el fichaje activo marcando hora de salida y estado=true.
 *
 * Flujo:
 * - Entrada: crea fichaje, guarda en sesión horaEntrada y redirige a perfil.jsp.
 * - Salida: actualiza el fichaje activo con hora de salida y redirige a bienvenido.jsp.
 */
@WebServlet(name = "FichajeServlet", urlPatterns = {"/FichajeServlet"})
public class FichajeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario está autenticado
        if (!Autenticacion.estaAutenticado(request)) {
            response.sendRedirect("login.jsp");
            return;
        }
        
    HttpSession session = request.getSession();
    String correoUsuario = (String) session.getAttribute("correo");
        String accion = request.getParameter("accion");

        if (accion == null) {
            response.sendRedirect("perfil.jsp");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        // Obtener usuario actual y sus datos por correo
        Usuario usuario = UsuarioDAO.obtenerUsuarioPorCorreo(correoUsuario);
        // Forzar coherencia: usar siempre datos del usuario autenticado
        String nombre = (usuario != null && usuario.getUsuario() != null) ? usuario.getUsuario() : correoUsuario;
        String apellido = (usuario != null && usuario.getApellido() != null) ? usuario.getApellido() : "";
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Crear objeto FichajeModel para las operaciones
        FichajeModel fichaje = new FichajeModel();
        fichaje.setNombre(nombre);
        fichaje.setApellido(apellido);
        fichaje.setRol(usuario.getRol());
        fichaje.setFecha(LocalDate.now());
        fichaje.setEntrada(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));

        FichajeDAO fichajeDAO = new FichajeDAO();
        
        switch (accion) {
            case "entrada":
                // Evitar duplicados: si ya hay activo, no crear otro
                FichajeModel activo = fichajeDAO.obtenerFichajeActivo(nombre, apellido);
                if (activo != null) {
                    // Refrescar estado en sesión y redirigir
                    session.setAttribute("horaEntrada", LocalDateTime.of(activo.getFecha(), activo.getEntrada()));
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                    response.sendRedirect("perfil.jsp");
                    return;
                }

                // Registrar entrada nueva
                boolean entradaRegistrada = fichajeDAO.crear(fichaje);
                
                if (entradaRegistrada) {
                    System.out.println("DEBUG: Fichaje creado exitosamente para " + nombre + " " + apellido);
                    session.setAttribute("horaEntrada", ahora);
                    session.setAttribute("horaSalida", null);
                    session.setAttribute("fichajeEntrada", true);
                    session.setAttribute("fichajeSalida", false);
                    
                    // Guardar variables de estado de producción
                    session.setAttribute("inicioProduccion", ahora);
                    session.setAttribute("estadoUsuario", "produccion");

                    System.out.println("DEBUG: Atributos de sesión establecidos. fichajeEntrada=" + session.getAttribute("fichajeEntrada"));
                    
                    // Redirigir a perfil.jsp después de fichar entrada
                    response.sendRedirect("perfil.jsp");
                    return;

                } else {
                    System.out.println("ERROR: No se pudo crear el fichaje para " + nombre + " " + apellido);
                    request.setAttribute("error", "Error al registrar la entrada");
                    request.getRequestDispatcher("perfil.jsp").forward(request, response);
                }
                return;

            case "salida":
                // Buscar fichaje activo directamente en la base de datos
                FichajeModel fichajeActivo = fichajeDAO.obtenerFichajeActivo(nombre, apellido);
                
                if (fichajeActivo != null) {
                    // Actualizar fichaje con la salida
                    fichajeActivo.setSalida(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
                    fichajeActivo.setEstado(true);
                    fichajeActivo.setDescanso("break".equals(request.getParameter("estado")) ? 1 : 0);
                    fichajeActivo.setComida("comida".equals(request.getParameter("estado")) ? 1 : 0);
                    
                    boolean salidaRegistrada = fichajeDAO.actualizar(fichajeActivo);
                    
                    if (salidaRegistrada) {
                        session.setAttribute("horaSalida", ahora);
                        session.setAttribute("fichajeSalida", true);
                        session.setAttribute("fichajeEntrada", false);
                        
                        // Limpiar sesión de producción
                        session.removeAttribute("inicioProduccion");
                        session.removeAttribute("estadoUsuario");

                        // Redirigir a bienvenido.jsp tras fichar salida
                        response.sendRedirect("bienvenido.jsp");
                        return;
                    }
                    request.setAttribute("error", "No se encontró un fichaje activo");
                    request.getRequestDispatcher("perfil.jsp").forward(request, response);
                }
                return;
            
            default:
                // si no reconoce la acción
                response.sendRedirect("perfil.jsp");
        }
    }

    /**
     * Redirige GET a doPost para compatibilidad.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
