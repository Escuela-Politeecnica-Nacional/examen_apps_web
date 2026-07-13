# Examen Crediticio Automático

Sistema web para evaluar de forma automática la elegibilidad de un cliente para
un **crédito a plazos** dentro de una tienda virtual, en base a su edad, sus
ingresos declarados y el puntaje que reporta un sistema externo de buró
crediticio.

---

## 1. Contexto del proyecto

### Caso de uso: *Evaluar elegibilidad de crédito*

El cliente, ya autenticado en la tienda virtual (no requiere inicio de
sesión dentro de este módulo), desea comprar a plazos. El sistema debe
verificar automáticamente si el cliente puede o no acceder al crédito.

**Actores**

| Actor | Rol |
|---|---|
| Cliente | Solicita el crédito a plazos e ingresa sus datos (edad, ingresos). |
| Sistema de tienda virtual | Presenta la vista `creditoplazos` y dispara la evaluación. |
| Administrador de créditos | Revisa manualmente las solicitudes que quedan `EN_REVISION`. |
| Sistema de buró crediticio | Sistema externo (simulado vía DAO) que entrega el puntaje del cliente. |

### Datos del cliente

El cliente, en este contexto, solo posee:
- **Edad**
- **Ingresos declarados**

### Reglas de negocio

El **puntaje crediticio** se calcula sobre una base de **100 puntos** y define
una matriz de decisión:

| Condición | Estado | Acción |
|---|---|---|
| Puntaje **≥ 75** | `APROBADO` | Se aprueba el crédito y se guarda en la BD como `APROBADO`. |
| **50 ≤** Puntaje **< 75** | `EN_REVISION` | Se notifica al cliente que su solicitud será evaluada por un asesor en un plazo máximo de **24 horas**. Se guarda la solicitud (orden) en la BD. |
| Puntaje **< 50** | `RECHAZADO` | Se muestra el mensaje "no es posible otorgar el crédito en este momento" y se redirige al cliente a los métodos de pago tradicionales (**tarjeta** o **transferencia**). |

El puntaje puede caer por debajo de 50, además del cálculo del buró, si:
- La **edad es menor a 18 años**, o
- La **cuota mensual no supera el 40% de los ingresos declarados**.

### Flujo funcional

1. El cliente accede a la vista **`creditoplazos`** y selecciona *"Solicitar
   crédito a plazos"*.
2. El **Servlet `Gestionar`** (controlador/router) recibe la petición HTTP.
   - Método **`solicitar`**: recibe los datos del cliente (edad, ingresos) y
     construye el objeto `Cliente`.
   - Método **`evaluarCredito`**: orquesta la evaluación, delegando en la
     capa DAO.
3. El **DAO del sistema** expone `obtenerPuntaje(Cliente cliente)`, que
   recibe el objeto `Cliente` y consume al **`SistemaBuroCreditoDAO`**, el
   cual retorna el **puntaje como `String`**.
4. Con el puntaje obtenido, se aplica la matriz de decisión y se persiste el
   resultado (estado de la solicitud) vía **JPA / EclipseLink** sobre
   **MySQL (XAMPP)**.
5. Según el estado resultante, la vista **JSP** muestra:
   - `APROBADO` → confirmación de aprobación.
   - `EN_REVISION` → aviso de revisión manual (máx. 24 hrs).
   - `RECHAZADO` → mensaje de rechazo + redirección a métodos de pago
     tradicionales (tarjeta / transferencia).

### Arquitectura y tecnologías

| Capa | Tecnología |
|---|---|
| Front-end | JSP |
| Controlador | Servlet (`Gestionar` — router con `solicitar` y `evaluarCredito`) |
| Acceso a datos | DAO + JPA (Jakarta Persistence API) |
| Proveedor JPA | EclipseLink 4.0.2 |
| Base de datos | MySQL (vía **XAMPP**), driver `mysql-connector-j` 8.3.0 |
| Servidor | Tomcat 10.1.x — **embebido**, para ejecutar directamente desde el IDE |
| Empaquetado | WAR (Maven `maven-war-plugin`) |
| Java | 21 |

```
com.tuempresa
├── controlador
│   └── Gestionar (Servlet)          → solicitar() / evaluarCredito()
├── dao
│   ├── SistemaDAO                   → obtenerPuntaje(Cliente): String... (int)
│   └── SistemaBuroCreditoDAO        → consulta al buró, retorna puntaje (String)
├── modelo
│   ├── Cliente                      → edad, ingresosDeclarados
│   └── Solicitud                    → entidad JPA (estado, fecha, etc.)
└── ServerLauncher                   → arranque de Tomcat embebido
```

> Nota: la estructura exacta de paquetes puede variar según cómo la hayas
> organizado en tu IDE; este README documenta el diseño conceptual acordado.

---

## 2. Ejecución del proyecto en el IDE (VS Code)

Este proyecto usa **Tomcat embebido**, por lo que no necesitas instalar ni
configurar un servidor externo: se ejecuta como una aplicación Java normal
desde VS Code y verás los cambios en tiempo real.

### 2.1 Requisitos previos

- **JDK 21** instalado y configurado (`JAVA_HOME`).
- **Maven** instalado (o usar el Maven embebido que trae la extensión de VS Code).
- **XAMPP** con el módulo **MySQL** iniciado (Panel de control de XAMPP →
  *Start* en `MySQL`).
- Extensiones de VS Code recomendadas:
  - `Extension Pack for Java` (Microsoft)
  - `Maven for Java`
  - (Opcional) `Debugger for Java` — ya incluido en el Extension Pack.

### 2.2 Configurar la base de datos

1. Abre `phpMyAdmin` (desde XAMPP) y crea la base de datos, por ejemplo:
   ```sql
   CREATE DATABASE examen_crediticio_automatico;
   ```
2. Verifica que la configuración de conexión en `persistence.xml`
   (ubicado en `src/main/resources/META-INF/persistence.xml`) coincida con
   tus credenciales de XAMPP (por defecto usuario `root` sin contraseña):
   ```xml
   <property name="jakarta.persistence.jdbc.url"
             value="jdbc:mysql://localhost:3306/examen_crediticio_automatico"/>
   <property name="jakarta.persistence.jdbc.user" value="root"/>
   <property name="jakarta.persistence.jdbc.password" value=""/>
   ```

### 2.3 Clonar / abrir el proyecto

1. Abre la carpeta del proyecto en VS Code (`File > Open Folder`).
2. Espera a que la extensión de Java indique **"Java: Ready"** en la barra
   inferior (esto importa el `pom.xml` y descarga las dependencias).

### 2.4 Ejecutar el servidor embebido

1. Abre la clase `ServerLauncher.java`.
2. Haz clic en **`Run`** (aparece sobre el método `main`) o usa
   `Ctrl+F5` / el ícono ▶ en la parte superior del editor.
3. En la consola verás:
   ```
   Servidor arriba en http://localhost:8080/
   ```
4. Abre el navegador en:
   ```
   http://localhost:8080/creditoplazos
   ```

### 2.5 Ver los cambios en tiempo real

- **Cambios en JSP / HTML / CSS / JS** (dentro de `src/main/webapp`):
  se reflejan al refrescar el navegador, sin reiniciar el servidor
  (`reloadable = true`).
- **Cambios en clases Java** (`Gestionar`, DAOs, `Cliente`, etc.):
  - VS Code recompila automáticamente a `target/classes` al guardar
    (compilación incremental de la extensión de Java).
  - Para que el servidor tome el nuevo bytecode, **detén y vuelve a
    ejecutar** `ServerLauncher` (o usa *Hot Code Replace* del depurador de
    Java si solo cambiaste el cuerpo de un método, ejecutando en modo
    **Debug** con `F5`).

### 2.6 Generar el WAR para despliegue en un Tomcat externo (opcional)

Si en algún momento necesitas desplegar en un Tomcat "de verdad" (fuera del
IDE):

```bash
mvn clean package
```

Esto genera el archivo `.war` en `target/`, listo para copiarlo a la carpeta
`webapps` de cualquier instancia de Tomcat.

---

## 3. Resumen de endpoints (Servlet `Gestionar`)

| Acción | Método | Descripción |
|---|---|---|
| `?accion=solicitar` | `POST` | Recibe edad e ingresos, crea el `Cliente` y muestra el formulario/estado inicial de la solicitud. |
| `?accion=evaluarCredito` | `POST` | Ejecuta la evaluación: obtiene el puntaje vía `SistemaBuroCreditoDAO`, aplica la matriz de decisión, persiste el resultado y redirige a la vista correspondiente (`APROBADO`, `EN_REVISION` o `RECHAZADO`). |

---

## 4. Estados posibles de una solicitud

- `APROBADO`
- `EN_REVISION`
- `RECHAZADO`