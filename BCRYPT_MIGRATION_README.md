# 🔐 Sistema de Hasheo de Contraseñas con BCrypt

## ✅ Cambios Implementados

Se ha implementado un sistema seguro de hasheo de contraseñas utilizando **BCrypt**, reemplazando el almacenamiento de contraseñas en texto plano.

### Archivos Modificados/Creados:

1. **`pom.xml`** - Añadida dependencia `jbcrypt`
2. **`PasswordHasher.java`** - Nueva clase utilidad para hashear y verificar contraseñas
3. **`UsuarioDAO.java`** - Modificado para usar BCrypt en:
   - `crearUsuario()` - Hashea contraseñas nuevas
   - `actualizarUsuarioPorId()` - Hashea contraseñas al actualizar
   - `verificarCredencialesPorCorreo()` - Verifica con BCrypt
4. **`MigratePasswordsServlet.java`** - Servlet para migrar contraseñas existentes

---

## 🚀 Pasos para Activar el Sistema

### 1. Recompilar el Proyecto

```bash
mvn clean install
```

Esto descargará la dependencia BCrypt y recompilará el proyecto.

### 2. Reiniciar Tomcat

Detén y reinicia el servidor Tomcat para que cargue los nuevos cambios.

### 3. Migrar Contraseñas Existentes

**⚠️ IMPORTANTE:** Antes de que los usuarios puedan iniciar sesión, debes migrar las contraseñas existentes.

Accede a la siguiente URL en tu navegador:

```
http://localhost:8080/controlfichaje/MigratePasswordsServlet
```

Esta página:
- ✓ Identificará todas las contraseñas en texto plano
- ✓ Las convertirá a hashes BCrypt
- ✓ Actualizará la base de datos
- ✓ Mostrará un reporte detallado

**Ejemplo de salida:**
```
✓ Contraseña migrada para usuario 'admin' (admin@fichajes.com)
✓ Contraseña migrada para usuario 'Juan' (juan@fichajes.com)
✓ Contraseña migrada para usuario 'Carlos' (carlos@fichajes.com)

Resumen:
✓ Contraseñas migradas: 3
ℹ️ Ya estaban hasheadas: 0
```

### 4. Verificar el Funcionamiento

Después de la migración:
1. Ve a `http://localhost:8080/controlfichaje/login.jsp`
2. Inicia sesión con las credenciales normales
3. El sistema ahora verificará las contraseñas hasheadas automáticamente

### 5. Eliminar el Servlet de Migración (Opcional pero Recomendado)

Una vez completada la migración, puedes eliminar el archivo:
```
src/main/java/com/mycompany/controlfichaje/MigratePasswordsServlet.java
```

O simplemente eliminarlo del proyecto para evitar ejecuciones accidentales.

---

## 🔍 ¿Cómo Funciona?

### Hasheo de Contraseñas

Cuando se crea o actualiza un usuario:
```java
String plainPassword = "miPassword123";
String hashedPassword = PasswordHasher.hashPassword(plainPassword);
// Resultado: "$2a$12$KIx8lN3b5..."  (60 caracteres)
```

### Verificación de Login

Cuando un usuario inicia sesión:
```java
boolean valida = PasswordHasher.checkPassword(passwordIngresada, hashAlmacenado);
```

BCrypt compara de forma segura, resistente a:
- ✓ Ataques de timing
- ✓ Ataques de fuerza bruta (gracias al factor de coste)
- ✓ Rainbow tables (cada password tiene un salt único)

### Compatibilidad durante Migración

El método `verificarCredencialesPorCorreo()` tiene compatibilidad temporal:
- Si detecta un hash BCrypt → usa `BCrypt.checkpw()`
- Si detecta texto plano → usa comparación directa (solo durante migración)

Esto permite que el sistema funcione mientras se migran gradualmente las contraseñas.

---

## 🛡️ Ventajas de BCrypt

✓ **Salt automático**: Cada contraseña tiene un salt aleatorio único  
✓ **Adaptativo**: El factor de coste (log_rounds=12) puede ajustarse  
✓ **Resistente a fuerza bruta**: El hashing es intencionalmente lento  
✓ **Estándar de la industria**: Ampliamente probado y recomendado  
✓ **No reversible**: Imposible recuperar la contraseña original del hash  

---

## 📊 Formato de Hashes BCrypt

Un hash BCrypt tiene esta estructura:
```
$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW
\__/\__/\_____________________________/\_________________/
 |   |            Salt                      Hash
 |   └── Factor de coste (2^12 = 4096 iteraciones)
 └── Algoritmo BCrypt versión 2a
```

**Longitud total:** 60 caracteres  
**Almacenamiento:** Columna VARCHAR(60) o mayor

---

## ⚠️ Consideraciones de Base de Datos

Asegúrate de que la columna `password` en la tabla `usuarios` tenga suficiente espacio:

```sql
-- Verificar estructura actual
PRAGMA table_info(usuarios);

-- Si la columna es muy pequeña, ampliarla:
ALTER TABLE usuarios ADD COLUMN password_new TEXT;
UPDATE usuarios SET password_new = password;
-- ... migración ...
```

En SQLite, las columnas TEXT pueden almacenar hasta 1 GB, así que no hay problema.

---

## 🧪 Pruebas

### Crear Usuario Nuevo
Los nuevos usuarios creados desde ahora tendrán contraseñas hasheadas automáticamente.

### Actualizar Contraseña
Al editar un usuario y cambiar su contraseña, se hasheará automáticamente.

### Login
El login funcionará igual, pero ahora verificando contra hashes BCrypt.

---

## 🔧 Configuración Avanzada

Si en el futuro quieres ajustar la seguridad, puedes modificar en `PasswordHasher.java`:

```java
private static final int LOG_ROUNDS = 12;  // Actual: 4096 iteraciones
```

**Valores comunes:**
- `10` = 1024 iteraciones (rápido, menor seguridad)
- `12` = 4096 iteraciones (recomendado, equilibrado)
- `14` = 16384 iteraciones (muy seguro, más lento)

---

## 📝 Resumen de Ejecución

1. ✅ Recompilar proyecto (`mvn clean install`)
2. ✅ Reiniciar Tomcat
3. ✅ Ejecutar `MigratePasswordsServlet` (solo una vez)
4. ✅ Verificar login funcionando
5. ✅ Eliminar servlet de migración (opcional)

---

## 🆘 Solución de Problemas

### Error: "The import org.mindrot cannot be resolved"
**Solución:** Ejecuta `mvn clean install` para descargar la dependencia.

### Error: "No se pudo actualizar usuario"
**Solución:** Verifica que la base de datos sea escribible y que Tomcat tenga permisos.

### Los usuarios no pueden iniciar sesión
**Solución:** Asegúrate de haber ejecutado el servlet de migración primero.

### Hash parece incorrecto
**Solución:** Los hashes BCrypt siempre son diferentes aunque la contraseña sea la misma (debido al salt aleatorio). Esto es normal y correcto.

---

**¡El sistema de hasheo de contraseñas está listo! 🎉**
