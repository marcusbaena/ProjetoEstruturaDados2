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
}