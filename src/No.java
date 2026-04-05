public class No implements Comparable<No> {
    private char caractere;
    private int frequencia;
    private No esquerda;
    private No direita;

    public No(char caractere, int frequencia) {
        this.caractere = caractere;
        this.frequencia = frequencia;
    }

    public No(char caractere, int frequencia, No esquerda, No direita) {
        this.caractere = caractere;
        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }

    public char getCaractere() {
        return caractere;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public No getEsquerda() {
        return esquerda;
    }

    public No getDireita() {
        return direita;
    }

    public void setEsquerda(No esquerda) {
        this.esquerda = esquerda;
    }

    public void setDireita(No direita) {
        this.direita = direita;
    }

    public boolean ehFolha() {
        return esquerda == null && direita == null;
    }

    @Override
    public int compareTo(No outro) {
        return this.frequencia - outro.frequencia;
    }
}