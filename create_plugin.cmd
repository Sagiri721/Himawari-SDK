@echo off

set /p name=Plugin name?
mkdir %name%

cd %name%

Rem write dependency file
echo {"dependencies": []} > plugin.json

Rem write main class file
set classText=class %name% { public static void doSomething(){} }
echo %classText% > %name%.java