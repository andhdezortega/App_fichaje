
package Autenticacion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import com.mycompany.controlfichaje.dao.UsuarioDAO;

public class Autenticacion {
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    //Método para verificar las credenciales por correo
    public static boolean verificarCredenciales(String correo, String password) {
        if (correo == null || correo.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }
        return usuarioDAO.verificarCredencialesPorCorreo(correo.trim(), password);
    }
    
    //Método de Login por correo
    public static boolean hacerLogin(HttpServletRequest request, String correo, String password) {
        if (!verificarCredenciales(correo, password)) {
            return false;
        }
        Map<String, String> userInfo = usuarioDAO.obtenerInfoUsuarioPorCorreo(correo);
        if (userInfo.isEmpty()) {
            return false;
        }
        HttpSession session = request.getSession(true);
        // Obtener el nombre de usuario real para la sesión
        com.mycompany.controlfichaje.dao.Usuario u = com.mycompany.controlfichaje.dao.UsuarioDAO.obtenerUsuarioPorCorreo(correo);
        String usuario = (u != null && u.getUsuario() != null) ? u.getUsuario() : correo;
        session.setAttribute("usuario", usuario);
        session.setAttribute("correo", correo);
        session.setAttribute("rol", userInfo.get("rol"));
        session.setAttribute("descripcion", userInfo.get("descripcion"));
        session.setAttribute("timestampLogin", System.currentTimeMillis());
        session.setAttribute("ipAddress", request.getRemoteAddr());
        session.setMaxInactiveInterval(30 * 60);
        return true;
    }
    
    //Método para verificar si hay sesión activa
    public static boolean estaAutenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String usuario = (String) session.getAttribute("usuario");
        return usuario != null;
    }
    
    //Método para obtener el Usuario Actual de la sesión
    public static String obtenerUsuarioActual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String usuario = (String) session.getAttribute ("usuario");
            System.out.println("Usuario actual: " + usuario);
            return usuario;            
        }
        return null;
    }
    
    //Método para obtener el rol del usuario
    public static String obtenerRol(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session !=null) {
            String rol = (String) session.getAttribute("rol");
            System.out.println("Rol actual: " + rol);
            return rol;
        }
        return null;
    }
    
    //Método para verificación del rol requerido
    public static boolean tieneRol(HttpServletRequest request, String rolRequerido) {
        String rolActual = obtenerRol(request);
        boolean tieneAcceso = rolRequerido != null && rolRequerido.equals(rolActual);
        
        System.out.println("Verificación de rol - Requerido: " + rolRequerido + ", Actual: " + rolActual + ", Acceso: " + tieneAcceso);
        return tieneAcceso;
    }
    
    //Método para hacer logout
    public static void hacerlogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String usuario = (String) session.getAttribute ("usuario");
            System.out.println("Cerrando sesión para usuario: " + usuario);
            
            session.invalidate();
            
            System.out.println("sesión cerrada correctamente");
        } else {
            System.out.println("no hay sesión activa para cerrar");
        }
    }
    
    public static long obtenerTiempoSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long timestamp = (Long) session.getAttribute("timestampLogin");
            if (timestamp != null) {
                long tiempoActivo = (System.currentTimeMillis() - timestamp) / 1000;
                System.out.println("Sesión activa por: " + tiempoActivo + " segundos");
                return tiempoActivo;
            }
        }
        return 0;
    }
}




