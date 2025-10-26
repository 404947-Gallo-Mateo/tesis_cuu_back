# Backend Club Union Unquillo - Fragmentos del documento técnico

## Tecnologías
- Backend - JDK Java 17 y Spring Boot v3.3.2 con Maven v3.9
- Frontend - Angular 19, con standalone components, junto con las librerías de
- componentes de Bootstrap 5+, Bootstrap Icons, SweetAlert2 y Google Charts
para los KPIs.
- Base de Datos - MySQL 8+ para producción y H2 para el desarrollo.
- Integraciones - Se integró Keycloak, una plataforma para la gestión de
identidades y accesos (IAM), utilizada para la Autenticación y Autorización de
usuarios, y también la API de Mercado Pago (Checkout Pro), para integrar el
pago de Cuotas.
- Gestión del proyecto - Jira con tablero Scrum

## Objetivo
    Implementar y desplegar una plataforma web integral para el Club Unión Unquillo antes
    de Diciembre 2025, que digitalice el 100% de sus procesos administrativos, y contribuya a
    eliminar el uso de papel en las gestiones principales, automatizar el 90% de los pagos
    mediante la integración con Mercado Pago, centralizar la información en dashboards
    personalizados por rol, reducir en un 80% el tiempo de gestión administrativa en papel.

    Para demostrar el exito de la implementación, se espera que el 80% de la comunidad utilice la
    plataforma en los primeros 4 meses.
    Se busca garantizar la sostenibilidad, al eliminar el uso de miles de hojas de papel
    anuales, transparencia, al dar acceso en tiempo real a datos financieros y operativos del
    Club y eficiencia, al digitalizar sus gestiones.

## Límites
    Desde que un Usuario inicia sesión o se registra.
    Hasta que se inscribe a una disciplina o se genera el comprobante de un pago o un
    reporte.

## Alcances
### Gestión de Disciplinas
- Registrar Disciplina
- Actualizar Disciplina
- Dar de baja Disciplina
- Emitir informe de Disciplinas
- Consultar indicadores (KPIs) de Disciplinas

### Gestión de Categorías
- Registrar Categoría
- Actualizar Categoría
- Dar de baja Categoría
- Registrar inscripción de Alumno en Categoría
- Registrar baja de Alumno en Categoría

### Gestión de Usuarios
- Registrar un Usuario
- Modificar un Usuario
- Eliminar un Usuario
- Emitir informe sobre Usuarios
- Consultar indicadores (KPIs) de Usuarios

### Gestión de Pagos
- Generar Cuotas según usuario de forma automática
- Registrar pago online de Cuota de una Disciplina (Mercado Pago)
- Registrar pago online de Cuota social del Club (Mercado Pago)
- Registrar como pagada una Cuota de una Disciplina (Efectivo)
- Registrar como pagada una Cuota social del Club (Efectivo)
- Emitir informe de Pagos
- Consultar indicadores (KPIs) de Pagos

### Gestión de Notificaciones
- Emitir comprobante de pago
- Emitir mail para el cambio de contraseña
- Emitir mail para confirmar dirección de correo al registrarse

## Gestión y Desarrollo
    Para llevar a cabo el proyecto se usaron métodos y herramientas actuales que
    permitieron organizar las tareas a realizar y lograr un Producto Mínimo Viable (MVP)
    completo. Se utilizó la plataforma Jira para planificar sprints, seguir tareas y visualizar
    el progreso en el tablero y backlog. Dentro de Jira, se eligió la metodología Scrum,
    haciendo posible la entrega de funcionalidad de forma gradual cada 2 semanas.
    
    Durante el desarrollo hubo dificultades, por ejemplo, al integrar Mercado Pago se
    encontró poca documentación, además de que fue necesario utilizar Ngrok, el cual
    posibilitó tener túneles HTTPS que redirigen al proyecto local, cumpliendo así uno de
    los requerimientos al integrar MP Checkout Pro.
    
    Otro ejemplo, al integrar Keycloak hubo que asegurar la sincronización de los datos
    de los usuarios en la BD de Keycloak y la BD propia del proyecto, además de que
    para actualizar los datos de un usuario se utiliza un endpoint específico de Keycloak,
    pero para actualizar sus roles se utiliza otro, entre otras particularidades.
    
    Por último, al implementar el flujo de creación automática de Cuotas, hubo que
    tener en cuenta que solo se generan cuotas del Club cuando se está inscripto en una
    Disciplina, además de que no deben generarse Cuotas del Club durante los periodos
    que el usuario esté o haya estado inactivo (osea que no estuvo inscripto en ninguna
    Disciplina en un período de tiempo), todo esto teniendo en cuenta también que, al
    generar Cuotas nuevamente al inscribirse a alguna Disciplina, no se deben generar
    Cuotas del Club sobre el o los periodos inactivos.
    
    Pese a estas y otras dificultades, contando con un tiempo limitado debido a
    obligaciones laborales, igualmente se logró la totalidad de los alcances propuestos.
