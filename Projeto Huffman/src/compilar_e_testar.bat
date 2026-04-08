@echo off
setlocal
cls

echo.
echo     ==================================
echo          Estrutura de Dados II
echo          Prof. Joaquim Pessoa Filho
echo                Projeto 1
echo     ==================================
echo.
pause

echo --- limpando arquivos ---
del *.class
del huffman.jar
del teste.huff
del teste_restaurado.txt

echo.
echo --- compilando... ---
javac Huffman.java No.java MinHeap.java

echo.
echo --- criando jar ---
jar cfe huffman.jar Huffman *.class

echo.
echo --- Comprimindo arq_de_teste.txt...
java -jar huffman.jar -c arq_de_teste.txt teste.huff

echo.
echo.
echo.
pause
echo.
echo --- Descomprimindo teste.huff...
echo.
java -jar huffman.jar -d teste.huff teste_restaurado.txt

echo.
echo --- verificando resultados ---
echo.
fc /B arq_de_teste.txt teste_restaurado.txt > nul
if errorlevel 1 ( 
	echo *** falha, arquivos são diferentes***
) else (
	echo *** sucesso, arquivos sao iguais ***
)

echo.
echo Limpando arquivos temporarios...
del teste.huff
del teste_restaurado.txt

echo.
pause