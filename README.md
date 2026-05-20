🏦 MACRI BANK - Banca Móvil Nativa
MACRI BANK es una aplicación financiera de alto rendimiento desarrollada de forma nativa para Android. Este proyecto implementa una arquitectura robusta y escalable, simulando las funcionalidades principales de aplicaciones líderes en el mercado como Nequi o Bancolombia.
🛠️ Tecnologías y Arquitectura
Para este proyecto se utilizó un stack tecnológico moderno y profesional:
•
Lenguaje: Kotlin 100% nativo.
•
Interfaz de Usuario: Jetpack Compose (Declarative UI).
•
Arquitectura: Clean Architecture + MVVM (Model-View-ViewModel).
•
Inyección de Dependencias: Hilt (Dagger).
•
Base de Datos y Auth: Firebase (Authentication & Cloud Firestore).
•
Reactividad: Kotlin Coroutines & Flow para manejo de datos en tiempo real.
✨ Funcionalidades Actuales (Versión Pre-Final)
🔐 Seguridad y Acceso
•
Registro de Usuarios: Creación de nuevas cuentas con validación en Firebase.
•
Login Seguro: Autenticación obligatoria para ingreso a la plataforma.
•
Gestión de Perfil: Visualización de datos personales y cierre de sesión.
💸 Gestión Financiera
•
Saldo en Tiempo Real: Sincronización inmediata con Firestore.
•
Número de Cuenta Único: Generación automática de cuenta para nuevos clientes.
•
Consignaciones: Funcionalidad nativa para cargar saldo a la cuenta.
🔁 Transacciones
•
Transferencias Atómicas: Envío de dinero entre cuentas mediante transacciones de base de datos (evita pérdida de datos).
•
Historial de Movimientos: Lista detallada de ingresos y egresos con fecha y hora.
•
Comprobante Digital: Vista detallada de cada movimiento (Recibo oficial).
🏗️ Estructura del Proyecto
•
data/: Implementación de repositorios y fuentes de datos (Firebase).
•
domain/: Modelos de negocio e interfaces de repositorio (Lógica pura).
•
presentation/: ViewModels y Vistas (Compose) organizadas por módulos.
•
di/: Módulos de inyección de dependencias con Hilt.
🚀 Instalación y Pruebas
1.
Clonar el repositorio.
2.
Añadir el archivo google-services.json en la carpeta /app.
3.
Compilar con Android Studio (Min SDK 26).