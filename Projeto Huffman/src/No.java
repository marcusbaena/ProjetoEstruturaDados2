public class No implements Comparable<No> {
    char caractere;
    int frequencia;
    No esquerda;
    No direita;
    
    public No(char caractere, int frequencia) {
        this.caractere = caractere;
        this.frequencia = frequencia;
        this.esquerda = null;
        this.direita = null;
    }
    
    public No(int frequencia, No esquerda, No direita) {
        this.caractere = '\0'; 
        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }
    
    public boolean ehFolha() {
        return esquerda == null && direita == null;
    }
    
    @Override
    public int compareTo(No outroNo) {
        return this.frequencia - outroNo.frequencia;
    }
    
    @Override
    public String toString() {
        if (ehFolha()) {
            return "No('" + caractere + "'," + frequencia + ")";
        } else {
            return "No(interno," + frequencia + ")";
        }
    }
}
