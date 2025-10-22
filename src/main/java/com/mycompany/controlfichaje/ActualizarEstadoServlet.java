package com.mycompany.controlfichaje;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

import Autenticacion.Autenticacion;

import com.mycompany.controlfichaje.dao.Usuario;
import com.mycompany.controlfichaje.dao.UsuarioDAO;

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

        HttpSession session = request.getSession();
        String nuevoEstado = request.getParameter("estado");
        String correo = (String) session.getAttribute("correo");

            if (nuevoEstado != null && !nuevoEstado.trim().isEmpty() && correo != null && !correo.isEmpty()) {

// ----------------------------
        // 1. Registrar tiempo del estado anterior antes de cambiar
        // ----------------------------
        String estadoAnterior = (String) session.getAttribute("estadoUsuario");
        if (estadoAnterior != null) {
            String attrInicio = "inicio" + capitalize(estadoAnterior);
            String attrMinutos = "minutos" + capitalize(estadoAnterior);

            LocalDateTime inicioAnterior = (LocalDateTime) session.getAttribute(attrInicio);
            Long minutosAcumulados = (Long) session.getAttribute(attrMinutos);

            if (inicioAnterior != null) {
                long minutosExtra = java.time.Duration.between(inicioAnterior, LocalDateTime.now()).toMinutes();
                minutosAcumulados = (minutosAcumulados != null ? minutosAcumulados : 0L) + minutosExtra;

                // Guardar en BD
                Usuario usuarioObj = UsuarioDAO.obtenerUsuarioPorCorreo(correo);
                if (usuarioObj != null) {
                    UsuarioDAO.registrarTiempo(usuarioObj.getId(), estadoAnterior, minutosAcumulados);
                }

                // Actualizar la sesión
                session.setAttribute(attrMinutos, minutosAcumulados);
                session.removeAttribute(attrInicio); // reiniciar inicio del estado anterior
            }
        }

        // ----------------------------
        // 2. Actualizar el nuevo estado en sesión y BD
        // ----------------------------
        session.setAttribute("estadoUsuario", nuevoEstado);
        UsuarioDAO.actualizarEstadoUsuario(correo, nuevoEstado);
        System.out.println("✅ Estado actualizado en BD para: " + correo + " -> " + nuevoEstado);

        // ----------------------------
        // 3. Gestionar inicio del nuevo estado
        // ----------------------------
        String attrInicioNuevo = "inicio" + capitalize(nuevoEstado);
        if (session.getAttribute(attrInicioNuevo) == null) {
            session.setAttribute(attrInicioNuevo, LocalDateTime.now());
        }
    }

    response.sendRedirect("perfil.jsp");
}
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
