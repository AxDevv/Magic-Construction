@echo off
echo ========================================
echo    Magic Construction - Build All
echo ========================================
echo.

echo [1/4] Building NeoForge 1.21.1...
cd /d "%~dp0neoforge-1.21.1"
call gradlew.bat build
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: NeoForge 1.21.1
    pause
    exit /b 1
)
echo SUCCESS: NeoForge 1.21.1
echo.

echo [2/4] Building Forge 1.20.1...
cd /d "%~dp0forge-1.20.1"
call gradlew.bat build
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Forge 1.20.1
    pause
    exit /b 1
)
echo SUCCESS: Forge 1.20.1
echo.

echo [3/4] Building Fabric 1.21.1...
cd /d "%~dp0fabric-1.21.1"
call gradlew.bat build
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Fabric 1.21.1
    pause
    exit /b 1
)
echo SUCCESS: Fabric 1.21.1
echo.

echo [4/4] Building Fabric 1.20.1...
cd /d "%~dp0fabric-1.20.1"
call gradlew.bat build
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Fabric 1.20.1
    pause
    exit /b 1
)
echo SUCCESS: Fabric 1.20.1
echo.

echo ========================================
echo    All builds completed successfully!
echo ========================================
echo.
echo JARs location:
echo   neoforge-1.21.1\build\libs\
echo   forge-1.20.1\build\libs\
echo   fabric-1.21.1\build\libs\
echo   fabric-1.20.1\build\libs\
echo.
pause
