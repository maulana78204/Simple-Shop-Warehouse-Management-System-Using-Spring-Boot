@echo off
REM Warehouse Management System - Rebuild Script
REM Purpose: Stop running application, clean build, and rebuild JAR
REM Platform: Windows Command Prompt
REM Usage: rebuild.bat

setlocal enabledelayedexpansion

echo.
echo ==================================================
echo   Warehouse Management System - Rebuild Script
echo ==================================================
echo.

REM Step 1: Kill any running Java processes on port 8080
echo 📍 Step 1: Stopping running application...
for /f "tokens=5" %%a in ('netstat -aon ^| find "LISTENING" ^| find ":8080"') do (
    set PID=%%a
)

if defined PID (
    echo    Found running process on port 8080 (PID: !PID!)
    taskkill /PID !PID! /F >nul 2>&1
    if !errorlevel! equ 0 (
        echo    ✅ Process killed
    ) else (
        echo    ℹ️  Could not kill process (may already be stopped)
    )
) else (
    echo    ℹ️  No process running on port 8080
)

echo.

REM Step 2: Clean Maven build
echo 📍 Step 2: Cleaning Maven artifacts...
call mvn clean -q
if !errorlevel! equ 0 (
    echo    ✅ Clean successful
) else (
    echo    ❌ Clean failed
    exit /b 1
)

echo.

REM Step 3: Compile source code
echo 📍 Step 3: Compiling source code...
call mvn compile -q
if !errorlevel! equ 0 (
    echo    ✅ Compilation successful
) else (
    echo    ❌ Compilation failed
    exit /b 1
)

echo.

REM Step 4: Run tests
echo 📍 Step 4: Running tests...
call mvn test -q
if !errorlevel! equ 0 (
    echo    ✅ Tests passed
) else (
    echo    ⚠️  Some tests failed ^(continuing^)
)

echo.

REM Step 5: Package JAR
echo 📍 Step 5: Building JAR package...
call mvn package -DskipTests=true -q
if !errorlevel! equ 0 (
    echo    ✅ Package created successfully
) else (
    echo    ❌ Package failed
    exit /b 1
)

echo.

REM Step 6: Verify JAR
echo 📍 Step 6: Verifying JAR file...
if exist "target\warehouse-management-1.0.0.jar" (
    for %%F in (target\warehouse-management-1.0.0.jar) do (
        set "JAR_SIZE=%%~zF"
        echo    ✅ JAR found: target\warehouse-management-1.0.0.jar ^(Size: !JAR_SIZE! bytes^)
    )
) else (
    echo    ❌ JAR file not found
    exit /b 1
)

echo.
echo ==================================================
echo   ✅ Build Complete!
echo ==================================================
echo.
echo Next steps:
echo   1. Start application:
echo      java -jar target\warehouse-management-1.0.0.jar
echo.
echo   2. Access Swagger UI:
echo      http://localhost:8080/api/swagger-ui.html
echo.
echo   3. Default credentials:
echo      Username: admin
echo      Password: admin123
echo.
echo ==================================================
echo.

pause
