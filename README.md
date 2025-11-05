LevelUp Gamer — App Android (Kotlin + Compose)

Estado: MVP en desarrollo  ·  Plataforma: Android  ·  Arquitectura: MVVM + Repository  ·  Persistencia offline: Room  ·  UI: Jetpack Compose  ·  Imágenes: Coil


1) ¿Qué es LevelUp Gamer?

-LevelUp Gamer es una app de compra de productos gamer (consolas, periféricos y accesorios) enfocada en una experiencia rápida, simple y usable en Chile. Funciona offline‑first: el catálogo y el carrito viven en el teléfono usando Room, y cuando hay internet sincroniza/actualiza.


2) ¿Para quién es?

-Gamers casuales y entusiastas que compran desde el celular y quieren comparar rápido precio/calidad.
-Clientes que valoran la compra sin fricción, con carrito local y continuidad entre sesiones.
-Tiendas o emprendimientos que buscan vitrina móvil y (opcionalmente) conectar su backend para inventario real.


3) Propuesta de valor

-Rendimiento y simpleza: Compose nativo, navegación clara, precios en CLP.
-Funciona sin internet: catálogo y carrito disponibles; sincroniza al volver la conexión.
-Escalable: puede operar solo local o conectarse a un backend REST (/api/products).


4) Funcionalidades clave (MVP)

-Catálogo: listado con imágenes, precio, rating e indicadores básicos.
-Detalle: fotos, descripción y botón “Agregar al carrito”.
-Carrito local: persiste en Room; el usuario no pierde su selección.
-Perfil (base): pantallas de registro/inicio de sesión listas para completar flujo.
-Confirmar Ubicación: pantalla para guardar la zona del usuario y mostrar cobertura/ofertas locales.


5) Flujo de uso

1. El usuario entra a Home y ve banners + productos.
2. Explora Detalle → agrega al Carrito.
3. Confirma Ubicación para mejorar cobertura/envíos.
4. Revisa carrito y continúa al flujo de checkout (en roadmap).


6) Permisos y por qué (según AndroidManifest.xml)

-ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION
  Para Confirmar Ubicación: sugerir cobertura de despacho/retiro cercano o mostrar precios/reglas por comuna. Se solicita en tiempo de uso.
-INTERNET
  Necesario para sincronizar catálogo, cargar imágenes remotas y hablar con el backend.
-ACCESS_NETWORK_STATE
  Detectar si hay o no conexión y activar el modo offline (evitar errores/llamadas innecesarias).

> La app no comparte la ubicación a terceros ni la usa en background. El usuario siempre puede denegar el permiso y la app sigue funcionando con capacidades limitadas (sin personalización por zona).


7) Privacidad y datos

Locales (en el dispositivo): carrito, caché del catálogo, preferencias de usuario y ubicación aproximada elegida en Confirmar Ubicación.
Remotos (si hay backend): catálogo, precios e imágenes provienen de /api/products del servidor de la tienda.
Sin rastreadores externos en el MVP.
Borrado: al desinstalar se elimina toda la data local; en versiones futuras habrá “Borrar mis datos” dentro de la app.


8) Arquitectura (en simple)

-Capa UI: Compose (pantallas Home, Detalle, Carrito, Perfil, Confirmar Ubicación).
-Capa Dominio: ViewModels (estado + lógica de presentación).
-Capa Datos: Repositorio que orquesta Room (offline) y API REST (online).

>Esta arquitectura permite funcionar sin internet y sincronizar cuando exista. No requiere servicios de terceros para operar.


9) Requisitos y ejecución

-Android Studio 2024+, JDK 17, dispositivo o emulador API 24+.
-Abrir el proyecto, sincronizar y presionar Run  sobre el módulo app.

-Opcional backend (para catálogo en vivo):

-Levantar un servicio con endpoints REST en /api/products y configurar la URL base en la app (host emulador: 10.0.2.2:puerto; dispositivo físico: http://IP_LAN:puerto).


10) Roadmap (alto nivel)

-Checkout con medios de pago locales.
-Favoritos, filtros y búsqueda avanzada.
-Carrito completo (ítems, increment/decrement, totales, cupones).
-Sincronización catálogo Room con políticas de cacheo.
-'Modo dark' refinado y accesibilidad (TalkBack/contrast).
-Analítica respetuosa (eventos mínimos, opt‑in).


11) Métricas sugeridas para el negocio

-Conversión (Add‑to‑Cart → Compra).
-AOV (ticket promedio) y retención a 7/30 días.
-Velocidad de carga de Home/Detalle y tasa offline (uso sin red).


12) Licencia

MIT © 2025 LevelUp