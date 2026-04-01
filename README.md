# Guía de instalación y ejecución de Android Studio 

## 1. Cómo instalar Android Studio

### Paso 1: Descargar Android Studio
1. Entrar a la página oficial de Android Studio https://developer.android.com/studio?hl=es-419.
2. Buscar el botón de descarga.
3. Descargar la versión correspondiente para tu sistema operativo.


### Paso 2: Ejecutar el instalador
1. Abrir el archivo descargado.
2. Seguir el asistente de instalación.
3. Dejar activadas las opciones recomendadas.
4. Esperar a que termine la instalación.


### Paso 3: Abrir Android Studio por primera vez
1. Ejecutar Android Studio.
2. Esperar a que cargue la configuración inicial.
3. Aceptar la instalación de componentes necesarios como el SDK de Android.


## 2. Cómo configurar el emulador desde Android Studio

El emulador no se maneja como un programa aparte sino que dentro del flujo normal de Android Studio.

### Paso 1: Abrir Device Manager
1. Abrir Android Studio.
2. Ir al menú de herramientas que esta en la parte superior derecha.
3. Seleccionar **Device Manager**.
4. Ir a **File > Settings**.
5. Entrar a **Languages & Frameworks > Android SDK**.
6. Seleccionar la versión de Android que se desea instalar, por ejemplo **Android API 36**.
7. 7. Dar clic en **Apply** y después en **OK**. 8. Esperar a que la descarga termine.


## 3. Cómo abrir un proyecto o documento de Android Studio


### Opción 1: Importar un proyecto existente
1. Abrir Android Studio.
2. Ir a **File > New > Import Project**.
3. Buscar la carpeta principal del proyecto.
4. Seleccionarla y dar clic en **OK**.
5. Esperar a que Android Studio indexe y abra el proyecto.

### Opción 2: Abrir un proyecto ya existente
1. Desde la pantalla principal de Android Studio, elegir la opción **Open** si aparece disponible.
2. Buscar la carpeta raíz del proyecto.
3. Seleccionarla.
4. Esperar a que cargue el proyecto.


## 4. Cómo ejecutar la aplicación en el emulador

Una vez abierto el proyecto y creado el dispositivo virtual, puedes ejecutar la app desde Android Studio seleccionando el emulador como dispositivo de prueba.

### Paso 1: Preparar el proyecto
1. Abrir el proyecto en Android Studio.
2. Esperar a que termine la sincronización de Gradle.

### Paso 2: Seleccionar el dispositivo
En la parte superior de Android Studio, haz clic en el ícono de configuración y selecciona SDK Manager.
Se abrirá la ventana de Settings; entra a Languages & Frameworks > Android SDK.
Marca Android API 36 y descárgalo.
Después, abre Device Manager y verifica que el dispositivo esté encendido; si no, inícialo.
   

### Paso 3: Ejecutar la app
1. Dar clic en el botón **Run**.
2. Esperar a que la aplicación se compile.
3. Esperar a que la app se instale y se abra en el emulador.


## 5. Correr la aplicación desde tu celular
1. Ir a **Configuración > Acerca del teléfono > Información de software**.
2. Presionar **Número de compilación** 8 veces para activar las opciones de desarrollador.
3. Regresar a **Configuración** y entrar a **Opciones de desarrollo**.
4. Activar la opción de **Depuración USB**.
5. Conectar el celular a la computadora mediante cable USB.
6. Abrir Android Studio, seleccionar tu dispositivo y dar clic en **Run**.


## 6. Recomendaciones finales

- Usar la versión estable más reciente de Android Studio para evitar problemas de compatibilidad.
- Si una API no aparece al crear el emulador, revisar primero el **Android SDK** en la configuración.
- Si el emulador funciona lento, también se puede probar la aplicación directamente en un celular físico.



