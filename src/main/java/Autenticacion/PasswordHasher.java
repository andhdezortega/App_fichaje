package Autenticacion;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para hashear contraseñas usando BCrypt y verificarlas de forma segura.
 * BCrypt es un algoritmo de hashing adaptativo que incluye automáticamente un salt
 * aleatorio y puede ajustar su coste computacional para resistir ataques de fuerza bruta.
 * 
 * @author ControlFichaje Team
 * @version 1.0
 */
public class PasswordHasher {
    
    /**
     * Número de rondas de hashing (factor de coste). 
     * A mayor número, más seguro pero más lento.
     * 12 es un buen equilibrio entre seguridad y rendimiento.
     */
    private static final int LOG_ROUNDS = 12;
    
    /**
     * Hashea una contraseña en texto plano usando BCrypt.
     * Genera automáticamente un salt aleatorio y lo incluye en el hash resultante.
     * 
     * @param plainPassword Contraseña en texto plano a hashear
     * @return Hash BCrypt de la contraseña (incluye salt y configuración)
     * @throws IllegalArgumentException si la contraseña es null o vacía
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }
    
    /**
     * Verifica si una contraseña en texto plano coincide con un hash BCrypt.
     * Compara de forma segura contra timing attacks.
     * 
     * @param plainPassword Contraseña en texto plano ingresada por el usuario
     * @param hashedPassword Hash BCrypt almacenado en la base de datos
     * @return true si la contraseña coincide con el hash, false en caso contrario
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // El hash no es válido (probablemente texto plano antiguo)
            return false;
        }
    }
    
    /**
     * Verifica si un string parece ser un hash BCrypt válido.
     * Los hashes BCrypt comienzan con "$2a$", "$2b$" o "$2y$".
     * 
     * @param password String a verificar
     * @return true si parece un hash BCrypt, false si parece texto plano
     */
    public static boolean isHashed(String password) {
        if (password == null) {
            return false;
        }
        // Los hashes BCrypt tienen formato: $2a$rounds$salt+hash (60 caracteres)
        return password.startsWith("$2a$") || 
               password.startsWith("$2b$") || 
               password.startsWith("$2y$");
    }
}
