# TFG: Distribución del Algoritmo Random Forest mediante un Sistema Multiagente

## Índice
- [Introducción](#introduccion)
- [Estructura](#estructura)
- [Requisitos](#requisitos)
- [Ejecución de la aplicación java SMA](#ejecución-de-la-aplicación-java-sma)
- [Ejecución de la aplicación en Flask](#ejecución-de-la-aplicación-en-flask)
- [Ejecución de la aplicación Java sin SMA](#ejecución-de-la-aplicación-java-sin-sma)
- [Ejecución de las pruebas de tiempos](#ejecución-de-las-pruebas-de-tiempos)

## Introducción

Este repositorio contiene todo el código generado para desarrollar el sistema junto al código hecho para las distintas pruebas.

## Estructura
```
├── backend: backend del sistema  
│   ├── lib: jar de JADE  
│   ├── src  
│   │   ├── main  
│   │   │   ├── resources  
│   │   │   └── java  
│   │   │       └── com  
│   │   │           └── randomforest  
│   │   │               ├── controller: controladores  
│   │   │               ├── dto: objetos dto  
│   │   │               ├── jade:   
│   │   │               │   ├── MainContainerLauncher:  definición contenedor main de JADE  
│   │   │               │   ├── agent: agentes del SMA  
│   │   │               │   └── behaviour: comportamientos de los agentes  
│   │   │               ├── model: entidades de la base de datos mapeados  
│   │   │               ├── randomforest: clases relacionadas con la lógica del RF  
│   │   │               ├── repository: operaciones realizadas en la base de datos  
│   │   │               ├── service: servicios definidos  
│   │   │               ├── RandomForestApplication.java: main del backend  
│   │   │               └── SpringContext.java: configuración de beans  
│   │   └── test: test unitarios del proyecto  
│   ├── Dockerfile: creación imagen docker backend   
│   └── pom.xml: definición dependencias  
├── frontend  
│   ├── css: ficheros Cascading Style Sheets 
│   ├── html: ficheros HTML  
│   ├── images: imagenes usadas en las páginas  
│   ├── js: código JavaScript de las páginas  
│   ├── default.conf: configuración del servicio Nginx  
│   └── Dockerfile: creación imagen docker frontend   
├── javaSinAgentes: mismo contenido que en backend y frontend sin el SMA  
├── pruebasRendimiento 
│   ├── pruebaJava: carpeta con los resultados y código para analizar el tiempo de Common-CSV y BufferReader  
│   └── pruebasPython: carpeta con los resultados y código para analizar el tiempo de Pandas  
├── python  
│   ├── flask: applicación en flask con backend y frontend, el equivalente de javaSinAgentes  
│   └── rows_generator.py: fichero para generar datasets por medio de un CSV de entrenamiento y SDV  
└── .docker-compose.yml: archivo YAML para configurar los contenedores de la carpeta backend y frontend  
```

## Requisitos
Para usar la aplicación fuera de los contenedore docker o usar la aplicación de Python desarrollada en Flask es necesario tener:
- Java 17.0.12
- Python 3.10.11  
Y en Python haber hecho este pip install:
```
pip install pandas numpy sdv flask flask-cors scikit-learn
```

## Ejecución de la aplicación java SMA
Primero, hay que asegurar que la librería de JADE y las dependencias están instaladas
1. Cambie al directo backend:
```
cd .\backend\
```
2. Instalar JADE con el siguiente comando:
```
mvn install:install-file "-Dfile=lib/jade.jar" "-DgroupId=jade" "-DartifactId=jade" "-Dversion=4" "-Dpackaging=jar"
```
3. Instalar el resto de dependencias y ejecución de tests unitarios:  
```
mvn clean install
```
Si aparece "BUILD SUCCESS" todo está listo.  
Para solicitar predicciones al sistema, sigue estos pasos:  
4. Volver al directorio raíz:
```
cd ..
```
5. Crear los contenedores e imágenes Docker:
```
docker-compose up --build
```
Este proceso lleva unos minutos, esperar hasta ver por la pantalla: "Started RandomForestApplication in X.X seconds".  
6. Acceder a la aplicación: Buscar en el navegador "localhost".  
7. Pulsar en el botón "Registrar" y crear un usuario. (Poner foto)  
8. Una vez dentro de la aplicación solo hay que cargar un fichero CSV y rellenar el formulario de muestras indicando la configuración correspondiente (tipo de columnas, número de muestras, valores de las muestras, etc). Después de tener todo listo, solo hay que pulsar en "Predecir muestras", y el sistema empieza a realizar todo el proceso de creación de árboles y predicción de muestras. (Poner foto)  
9. Cuando estén todas las muestras predichas, automáticamente redirige a la ventana para ver los resultados. Para realizar más predicciones, pulsar en el botón "Predecir" y volver al paso 8.(Poner foto)  

## Ejecución de la aplicación en Flask
1. Haber instalado las librerías con el pip de [Requisitos](#requisitos) y estar en el directorio raíz, para cambiar al backend de Flask:
```
cd .\python\flask\backend\
```
2. Ejecutar el framework:
```
py app.py
```
Esperar a que aparezca el mensaje "Running on http://127.0.0.1:5000"  
3. Buscar en el navegador "localhost:5000"  
La funcionalidad es la misma que la descrita en punto 6 de [Ejecución de la aplicación java SMA](#ejecución-de-la-aplicación-java-sma), solo que no hay ventana de inicio de sesión, los resultados de las predicciones se imprimen en el terminal  

## Ejecución de la aplicación Java sin SMA
1. Estar en el directorio raíz, para cambiar al directorio "javaSinAgentes":
```
cd .\javaSinAgentes\
```
2. Crear los contenedores e imágenes Docker y seguir el mismo proceso desde el punto 6 de [Ejecución de la aplicación java SMA](#ejecución-de-la-aplicación-java-sma)  
```
docker-compose up --build
```
## Ejecución de las pruebas de tiempos
1. Se recomienda ejecutar las pruebas desde un IDE como Visual Studio Code o IntelliJ IDEA
2. Es necesario corregir las rutas de los ficheros CSV al igual que el índide máximo del número de filas
3. Se genera un txt en la carpeta con los tiempos  
