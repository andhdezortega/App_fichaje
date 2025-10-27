
package Autenticacion;

/**
 * Utilidades de autenticación y autorización para la aplicación.
 *
 * Responsabilidades principales:
 * - Validar credenciales de usuario (por correo y contraseña).
 * - Gestionar la sesión HTTP: inicio de sesión, obtención de datos de sesión y cierre.
 * - Comprobar el rol del usuario autenticado para controlar accesos.
 *
 * Notas de diseño:
 * - Esta clase expone métodos estáticos porque se usa desde distintos servlets/JSP
 *   sin necesidad de instanciarla.
 * - La información de sesión almacenada incluye: usuario visible ("usuario"),
 *   correo ("correo"), rol ("rol"), descripción ("descripcion"), marca de tiempo
 *   del login ("timestampLogin") y la IP de origen ("ipAddress").
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import com.mycompany.controlfichaje.dao.UsuarioDAO;

public class Autenticacion {
    
    /**
     * Verifica que el correo y la contraseña sean válidos en base de datos.
     * Solo se permite el login mediante correo.
     * @param correo Correo electrónico del usuario (identificador de login)
     * @param password Contraseña en texto plano introducida por el usuario
     * @return true si las credenciales son correctas; false en caso contrario
     */
    public static boolean verificarCredenciales(String correo, String password) {
        if (correo == null || correo.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            return false;
        }
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.verificarCredencialesPorCorreo(correo.trim(), password);
    }
    
    /**
     * Realiza el proceso de login a partir de correo y contraseña.
     * Si es correcto, inicializa la sesión HTTP y almacena los atributos necesarios.
     * @param request Petición actual (para crear/recuperar la sesión)
     * @param correo Correo electrónico del usuario
     * @param password Contraseña introducida
     * @return true si el login es satisfactorio; false si las credenciales no son válidas
     */
    public static boolean hacerLogin(HttpServletRequest request, String correo, String password) {
        if (!verificarCredenciales(correo, password)) {
            return false;
        }
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Map<String, String> userInfo = usuarioDAO.obtenerInfoUsuarioPorCorreo(correo);
        if (userInfo.isEmpty()) {
            return false;
        }
        HttpSession session = request.getSession(true);
        com.mycompany.controlfichaje.dao.Usuario u = com.mycompany.controlfichaje.dao.UsuarioDAO.obtenerUsuarioPorCorreo(correo);
        String usuario = (u != null && u.getUsuario() != null) ? u.getUsuario() : correo;
        session.setAttribute("usuario", usuario);
        session.setAttribute("correo", (u != null ? u.getCorreo() : correo));
        session.setAttribute("rol", userInfo.get("rol"));
        session.setAttribute("descripcion", userInfo.get("descripcion"));
        session.setAttribute("timestampLogin", System.currentTimeMillis());
        session.setAttribute("ipAddress", request.getRemoteAddr());
        session.setMaxInactiveInterval(30 * 60);
        return true;
    }
    
    /**
     * Comprueba si existe una sesión válida con un usuario autenticado.
     * @param request Petición actual
     * @return true si hay sesión y el atributo "usuario" está presente; false en caso contrario
     */
    public static boolean estaAutenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String usuario = (String) session.getAttribute("usuario");
        return usuario != null;
    }
    
    /**
     * Recupera el nombre de usuario visible almacenado en sesión.
     * @param request Petición actual
     * @return el nombre de usuario o null si no hay sesión
     */
    public static String obtenerUsuarioActual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("usuario");
        }
        return null;
    }
    
    /**
     * Obtiene el rol actual del usuario autenticado desde la sesión.
     * @param request Petición actual
     * @return rol (por ejemplo, "admin" o "usuario"), o null si no hay sesión
     */
    public static String obtenerRol(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("rol");
        }
        return null;
    }
    
    /**
     * Verifica si el usuario autenticado tiene el rol indicado.
     * @param request Petición actual
     * @param rolRequerido Rol necesario para acceder a un recurso
     * @return true si coincide; false en caso contrario
     */
    public static boolean tieneRol(HttpServletRequest request, String rolRequerido) {
        String rolActual = obtenerRol(request);
        return rolRequerido != null && rolRequerido.equals(rolActual);
    }
    
    /**
     * Cierra la sesión del usuario actual invalidando la sesión HTTP.
     * @param request Petición actual
     */
    public static void hacerlogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * Devuelve el tiempo (en segundos) transcurrido desde el inicio de sesión.
     * @param request Petición actual
     * @return segundos de sesión activa o 0 si no hay sesión o falta la marca de tiempo
     */
    public static long obtenerTiempoSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long timestamp = (Long) session.getAttribute("timestampLogin");
            if (timestamp != null) {
                return (System.currentTimeMillis() - timestamp) / 1000;
            }
        }
        return 0;
    }
}




