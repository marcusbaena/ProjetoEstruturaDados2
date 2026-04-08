import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class Huffman {

    public static int[] analisarFrequencia(String nomeArquivo) throws IOException {
        int[] frequencias = new int[256];
        try (FileInputStream fis = new FileInputStream(nomeArquivo)) {
            int b;
            while ((b = fis.read()) != -1) {
                frequencias[b]++;
            }
        }
        return frequencias;
    }

    public static MinHeap construirMinHeap(int[] frequencias) {
        MinHeap heap = new MinHeap();
        for (int i = 0; i < 256; i++) {
            if (frequencias[i] > 0) {
                heap.inserir(new No((char) i, frequencias[i]));
            }
        }
        return heap;
    }

    public static No construirArvore(MinHeap heap) {
        while (heap.tamanho() > 1) {
            No esq = heap.removerMin();
            No dir = heap.removerMin();
            No pai = new No(esq.frequencia + dir.frequencia, esq, dir);
            heap.inserir(pai);
        }
        return heap.removerMin();
    }

    public static String[] gerarTabelaCodigos(No raiz) {
        String[] tabela = new String[256];
        if (raiz != null) {
            if (raiz.ehFolha()) {
                tabela[raiz.caractere] = "0";
            } else {
                gerarCodigosRecursivo(raiz, "", tabela);
            }
        }
        return tabela;
    }

    private static void gerarCodigosRecursivo(No no, String codigo, String[] tabela) {
        if (no == null) return;
        if (no.ehFolha()) {
            tabela[no.caractere] = codigo;
        } else {
            gerarCodigosRecursivo(no.esquerda, codigo + "0", tabela);
            gerarCodigosRecursivo(no.direita, codigo + "1", tabela);
        }
    }

    public static void comprimir(String arquivoOriginal, String arquivoComprimido) throws IOException {
        int[] frequencias = analisarFrequencia(arquivoOriginal);
        MinHeap heap = construirMinHeap(frequencias);
        

        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 1: Tabela de Frequencia de Caracteres");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < 256; i++) {
            if (frequencias[i] > 0) {
                char c = (char) i;
                String charDisplay = (c >= 32 && c < 127) ? String.valueOf(c) : "?";
                System.out.println("Caractere '" + charDisplay + "' (ASCII: " + i + "): " + frequencias[i]);
            }
        }
        
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 2: Min-Heap Inicial (Vetor)");
        System.out.println("--------------------------------------------------");
        System.out.println(heap.getHeap().toString());

        No raiz = construirArvore(heap);
        String[] tabelaCodigos = gerarTabelaCodigos(raiz);

        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 3: Arvore de Huffman");
        System.out.println("--------------------------------------------------");
        imprimirArvore(raiz);
        
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 4: Tabela de Codigos de Huffman");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < 256; i++) {
            if (tabelaCodigos[i] != null) {
                char c = (char) i;
                String charDisplay = (c >= 32 && c < 127) ? String.valueOf(c) : "?";
                System.out.println("Caractere '" + charDisplay + "': " + tabelaCodigos[i]);
            }
        }
        
        escreverArquivoComprimido(arquivoOriginal, arquivoComprimido, frequencias, tabelaCodigos);
        
        File f1 = new File(arquivoOriginal);
        File f2 = new File(arquivoComprimido);
        long bitsOriginais = f1.length() * 8;
        long bitsComprimidos = f2.length() * 8;
        double taxa = (1.0 - (double) f2.length() / f1.length()) * 100;
        
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 5: Resumo da Compressao");
        System.out.println("--------------------------------------------------");
        System.out.println("Tamanho original....: " + bitsOriginais + " bits (" + f1.length() + " bytes)");
        System.out.println("Tamanho comprimido..: " + bitsComprimidos + " bits (" + f2.length() + " bytes)");
        System.out.printf("Taxa de compressao..: %.2f%%%n", taxa);
        System.out.println("--------------------------------------------------");
    }
    
    private static void imprimirArvore(No no) {
        if (no == null) return;
        System.out.println("- " + no.toString());
        if (no.esquerda != null) imprimirArvore(no.esquerda);
        if (no.direita != null) imprimirArvore(no.direita);
    }

    private static void escreverArquivoComprimido(String arquivoOriginal, String arquivoComprimido,
                                                  int[] frequencias, String[] tabelaCodigos) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(arquivoComprimido));
             FileInputStream fis = new FileInputStream(arquivoOriginal)) {
            for (int i = 0; i < 256; i++) {
                dos.writeInt(frequencias[i]);
            }
            StringBuilder bits = new StringBuilder();
            int b;
            while ((b = fis.read()) != -1) {
                bits.append(tabelaCodigos[b]);
            }
            int numBitsValidos = bits.length() % 8;
            if (numBitsValidos == 0) numBitsValidos = 8;
            dos.writeByte(numBitsValidos);
            for (int i = 0; i < bits.length(); i += 8) {
                String byteBits = bits.substring(i, Math.min(i + 8, bits.length()));
                while (byteBits.length() < 8) byteBits += "0";
                dos.writeByte(Integer.parseInt(byteBits, 2));
            }
        }
    }

    public static void descomprimir(String arquivoComprimido, String arquivoRestaurado) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoComprimido))) {
            int[] frequencias = new int[256];
            for (int i = 0; i < 256; i++) {
                frequencias[i] = dis.readInt();
            }
            MinHeap heap = construirMinHeap(frequencias);
            No raiz = construirArvore(heap);
            int numBitsValidos = dis.readByte() & 0xFF;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while ((b = dis.read()) != -1) {
                baos.write(b);
            }
            byte[] dadosComprimidos = baos.toByteArray();
            try (FileOutputStream fos = new FileOutputStream(arquivoRestaurado)) {
                No atual = raiz;
                if (raiz.ehFolha()) {
                    int totalCaracteres = raiz.frequencia;
                    for (int i = 0; i < totalCaracteres; i++) {
                        fos.write(raiz.caractere);
                    }
                    return;
                }
                for (int i = 0; i < dadosComprimidos.length; i++) {
                    byte byteAtual = dadosComprimidos[i];
                    int numBits = (i == dadosComprimidos.length - 1) ? numBitsValidos : 8;
                    for (int j = 7; j >= 8 - numBits; j--) {
                        int bit = (byteAtual >> j) & 1;
                        if (bit == 0) atual = atual.esquerda;
                        else atual = atual.direita;
                        if (atual.ehFolha()) {
                            fos.write(atual.caractere);
                            atual = raiz;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso:");
            System.out.println("  Comprimir:    java -jar huffman.jar -c <arquivo_original> <arquivo_comprimido>");
            System.out.println("  Descomprimir: java -jar huffman.jar -d <arquivo_comprimido> <arquivo_restaurado>");
            return;
        }

        String opcao = args[0];
        String arquivo1 = args[1];
        String arquivo2 = args[2];

        try {
            switch (opcao) {
                case "-c" ->                     {
                        long inicio = System.nanoTime();
                        comprimir(arquivo1, arquivo2);
                        long fim = System.nanoTime();
                        long tempoMs = (fim - inicio) / 1_000_000;
                        File f1 = new File(arquivo1);
                        File f2 = new File(arquivo2);
                        System.out.println("Arquivo comprimido com sucesso!");
                        System.out.println("Tempo de compressão: " + tempoMs + " ms");
                        System.out.println("Tamanho antes: " + f1.length() + " bytes");
                        System.out.println("Tamanho depois: " + f2.length() + " bytes");
                    }
                case "-d" ->                     {
                        long inicio = System.nanoTime();
                        descomprimir(arquivo1, arquivo2);
                        long fim = System.nanoTime();
                        long tempoMs = (fim - inicio) / 1_000_000;
                        File f1 = new File(arquivo1);
                        File f2 = new File(arquivo2);
                        System.out.println("Arquivo descomprimido com sucesso!");
                        System.out.println("Tempo de descompressão: " + tempoMs + " ms");
                        System.out.println("Tamanho comprimido: " + f1.length() + " bytes");
                        System.out.println("Tamanho restaurado: " + f2.length() + " bytes");
                    }
                default -> System.out.println("Opção inválida. Use -c ou -d");
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}