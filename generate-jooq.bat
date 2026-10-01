@echo off
setlocal
title Oversoul - jOOQ Code Generator

cd /d "%~dp0"

echo ==========================================
echo       Oversoul - jOOQ Code Generator
echo ==========================================
echo.

if not exist "gradlew.bat" (
    echo [ERRO] gradlew.bat nao encontrado.
    echo.
    echo Coloque este arquivo na raiz do projeto:
    echo %CD%
    echo.
    pause
    exit /b 1
)

echo [INFO] Gerando tabelas/classes do jOOQ...
echo.

call gradlew.bat jooqCodegen

if errorlevel 1 (
    echo.
    echo ==========================================
    echo [ERRO] Falha ao executar jooqCodegen.
    echo ==========================================
    echo.
    pause
    exit /b 1
)

echo.
echo ==========================================
echo [OK] jOOQ gerado com sucesso!
echo ==========================================
echo.

pause
endlocal