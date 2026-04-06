import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;



public class Huffman {

    // contar frequências
    public static int[] contarFrequencias(byte[] dados) {
        int[] frequencias = new int[256];

        for (byte b : dados) {
            frequencias[b & 0xFF]++;
        }

        return frequencias;
    }

    // criar o heap inicial
    public static MinHeap criarHeap(int[] frequencias) {
        MinHeap heap = new MinHeap();

        for (int i = 0; i < 256; i++) {
            if (frequencias[i] > 0) {
                heap.inserir(new No((char) i, frequencias[i]));
            }
        }

        return heap;
    }

    // construir a árvore de Huffman
    public static No construirArvoreHuffman(MinHeap heap) {
        while (heap.size() > 1) {
            No esquerda = heap.removerMin();
            No direita = heap.removerMin();

            No pai = new No('\0',
                    esquerda.getFrequencia() + direita.getFrequencia(),
                    esquerda,
                    direita);

            heap.inserir(pai);
        }

        return heap.removerMin();
    }

    // gerar tabela de códigos
    public static void gerarCodigos(No raiz, String codigo, String[] tabela) {
        if (raiz == null) return;

        if (raiz.ehFolha()) {
            tabela[raiz.getCaractere()] = codigo.isEmpty() ? "0" : codigo;
            return;
        }

        gerarCodigos(raiz.getEsquerda(), codigo + "0", tabela);
        gerarCodigos(raiz.getDireita(), codigo + "1", tabela);
    }


    public static String codificar(byte[] dados, String[] tabela) {
    StringBuilder bits = new StringBuilder();

    for (byte b : dados) {
        bits.append(tabela[b & 0xFF]);
    }

    return bits.toString();
    }   


    public static byte[] bitsParaBytes(String bits) {
    int tamanho = (bits.length() + 7) / 8;
    byte[] resultado = new byte[tamanho];

    for (int i = 0; i < bits.length(); i++) {
        if (bits.charAt(i) == '1') {
            resultado[i / 8] |= (byte) (1 << (7 - (i % 8)));
        }
    }

    return resultado;
    }


    public static void comprimir(String arquivoEntrada, String arquivoSaida) throws Exception {
    byte[] dados = Files.readAllBytes(Paths.get(arquivoEntrada));

    int[] frequencias = contarFrequencias(dados);
    MinHeap heap = criarHeap(frequencias);
    No raiz = construirArvoreHuffman(heap);

    String[] tabela = new String[256];
    gerarCodigos(raiz, "", tabela);

    String bits = codificar(dados, tabela);
    byte[] bytesComprimidos = bitsParaBytes(bits);

    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(arquivoSaida))) {
        // cabeçalho: frequências
        for (int i = 0; i < 256; i++) {
            out.writeInt(frequencias[i]);
        }

        // quantidade de bits válidos
        out.writeInt(bits.length());

        // dados comprimidos
        out.write(bytesComprimidos);
        }
    }


    public static int[] lerFrequencias(DataInputStream in) throws Exception {
    int[] frequencias = new int[256];

    for (int i = 0; i < 256; i++) {
        frequencias[i] = in.readInt();
    }

    return frequencias;
    }

    public static String bytesParaBits(byte[] bytes, int totalBits) {
    StringBuilder bits = new StringBuilder();

    for (byte b : bytes) {
        for (int i = 7; i >= 0; i--) {
            bits.append(((b >> i) & 1) == 1 ? '1' : '0');
        }
    }

    return bits.substring(0, totalBits);
    }

    public static byte[] decodificar(String bits, No raiz) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        No atual = raiz;

        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '0') {
                atual = atual.getEsquerda();
            } else {
                atual = atual.getDireita();
            }

            if (atual.ehFolha()) {
                out.write((byte) atual.getCaractere());
                atual = raiz;
            }
        }

        return out.toByteArray();
    }

    public static void descomprimir(String arquivoEntrada, String arquivoSaida) throws Exception {
        try (DataInputStream in = new DataInputStream(new FileInputStream(arquivoEntrada))) {
            int[] frequencias = lerFrequencias(in);
            int totalBits = in.readInt();
            byte[] dadosComprimidos = in.readAllBytes();

            MinHeap heap = criarHeap(frequencias);
            No raiz = construirArvoreHuffman(heap);

            String bits = bytesParaBits(dadosComprimidos, totalBits);
            byte[] dadosOriginais = decodificar(bits, raiz);

            Files.write(Paths.get(arquivoSaida), dadosOriginais);
        }
    }

}


