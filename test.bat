@echo off
title Ejecutando Pruebas de APIs con Sura3 - Súper Limpieza Activa
cls

echo =========================================================
echo       INICIANDO AUTOMATIZACION - APIS SARA 3
echo =========================================================
echo.

:: Validar existencia del Gradle wrapper
if not exist "gradlew.bat" (
    color 0C
    echo [ERROR] No se encontro el archivo 'gradlew.bat'.
    echo Coloca este .bat en la carpeta raiz de tu proyecto.
    echo.
    pause
    exit /b
)

echo [!] FASE DE LIMPIEZA PROFUNDA: Garantizando ejecucion limpia...
echo ---------------------------------------------------------

:: 1. Detener procesos huérfanos de Java y Gradle Daemons previos
echo [+] Matando demonios de Gradle activos en segundo plano...
call gradlew.bat --stop
echo.

:: 2. Forzar eliminación física de carpetas conflictivas (en caso de que clean falle por archivos bloqueados)
echo [+] Eliminando carpetas de reportes y temporales antiguas...
if exist "target" rmdir /s /q target
if exist "build" rmdir /s /q build
if exist "bin" rmdir /s /q bin
echo.

:: 3. Ejecutar la tarea limpia de compilación
echo [+] Ejecutando Gradle Clean nativo...
call gradlew.bat clean
if %errorlevel% neq 0 (
    color 0E
    echo [ADVERTENCIA] Fallo el comando clean. Posiblemente un archivo este bloqueado por el IDE o el explorador.
)
echo.

echo =========================================================
echo       INICIANDO EJECUCION DE PRUEBAS
echo =========================================================
echo.

:: 4. Ejecutar el Runner con logs de información
echo [+] Lanzando el Runner de Cucumber...
echo Ejecutando: Runner.Runner
echo.

call gradlew.bat clean test --tests "org.sara.api.Runner.Runner" --info

:: Guardar código de salida
set TEST_EXIT_CODE=%errorlevel%

echo.
echo [+] Generando reporte de Serenity BDD...
call gradlew.bat aggregate

echo.
echo =========================================================
if %TEST_EXIT_CODE% equ 0 (
    color 0A
    echo    [EXITO] Las pruebas pasaron correctamente.
) else (
    color 0C
    echo    [ALERTA] Hubo fallas durante las pruebas (Codigo: %TEST_EXIT_CODE%).
)
echo =========================================================
echo.

:: Preguntar si se abre el reporte
set /p open_report="¿Deseas abrir el reporte de Serenity en el navegador? (S/N): "
if /i "%open_report%"=="S" (
    if exist "target\site\serenity\index.html" (
        start target\site\serenity\index.html
    ) else (
        echo [ERROR] El reporte index.html no existe.
    )
)

echo.
echo Proceso finalizado. La consola quedara abierta.
cmd /k