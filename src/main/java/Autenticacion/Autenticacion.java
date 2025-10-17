
package Autenticacion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import com.mycompany.controlfichaje.dao.UsuarioDAO;

public class Autenticacion {
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    //Método para verificar las credenciales
    public static boolean verificarCredenciales(String usuario, String password) {
        System.out.print("Verificando credenciales para: " + usuario);
        
        if (usuario == null || usuario.trim().isEmpty() ||
            password == null || password.trim().isEmpty())
        {    
            System.out.println("Campos vacíos");
            return false;
        }
        
        boolean credencialesCorrectas = usuarioDAO.verificarCredenciales(usuario.trim(), password);
        
        if (credencialesCorrectas) {
            System.out.println("Credenciales correctas para: " + usuario);  
        } else {
            System.out.println("Credenciales incorrectas para: " + usuario);
        }
        
        return credencialesCorrectas;   
    }
    
    //Método de Login
    public static boolean hacerLogin(HttpServletRequest request, String usuario, String password) {
        System.out.println("Iniciando proceso de login para: " + usuario);
        
        if (!verificarCredenciales(usuario, password)) {
            return false;
        }
        
        Map<String, String> userInfo = usuarioDAO.obtenerInfoUsuario(usuario);
        if (userInfo.isEmpty()) {
            return false;
        }
        
        HttpSession session = request.getSession(true);
        System.out.println("ID de Sesión creada: " + session.getId());
        
        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", userInfo.get("rol"));
        session.setAttribute("descripcion", userInfo.get("descripcion"));
        session.setAttribute("timestampLogin", System.currentTimeMillis());
        session.setAttribute("ipAddress", request.getRemoteAddr());
        session.setMaxInactiveInterval(30 * 60);
        
        System.out.println("Login exitoso - Usuario: " + usuario + ", Rol: " + userInfo.get("rol") + ", Sesión: " + session.getId());
        return true;
    }
    
    //Método para verificar si hay sesión activa
    public static boolean estaAutenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            System.out.println("No hay sesión activa");
            return false;
        }
        
        String usuario = (String) session.getAttribute("usuario");
        boolean autenticado = (usuario != null);
        
        if (autenticado) {
            System.out.println("Sesión activa encontrada - Usuario: " + usuario);      
        } else {
            System.out.println("Sesión existe pero sin usuario");
        }
        
        return autenticado;
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




