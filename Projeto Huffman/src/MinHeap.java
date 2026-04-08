import java.util.ArrayList;

public class MinHeap {
    private final ArrayList<No> heap;
    
    public MinHeap() {heap = new ArrayList<>();}
    
    public void inserir(No no) {
        heap.add(no);
        subir(heap.size() - 1);
    }
    
    public No removerMin() {
        if (heap.isEmpty()) {
            return null;
        }
        
        No min = heap.get(0);
        No ultimo = heap.remove(heap.size() - 1);
        
        if (!heap.isEmpty()) {
            heap.set(0, ultimo);
            descer(0);
        }
        
        return min;
    }
    
    public int tamanho() {return heap.size();}
    
    public boolean vazio() {return heap.isEmpty();}
    
    private void subir(int i) {
        while (i > 0) {
            int pai = (i - 1) / 2;
            if (heap.get(i).compareTo(heap.get(pai)) >= 0) {
                break;
            }
            trocar(i, pai);
            i = pai;
        }
    }
    
    private void descer(int i) {
        int n = heap.size();
        while (true) {
            int esq = 2 * i + 1;
            int dir = 2 * i + 2;
            int menor = i;
        
            if (esq < n && heap.get(esq).compareTo(heap.get(menor)) < 0) menor = esq;
            if (dir < n && heap.get(dir).compareTo(heap.get(menor)) < 0) menor = dir;
            if (menor == i) break;
            trocar(i, menor);
            i = menor;
        }
    }
    
    private void trocar(int i, int j) {
        No temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
    @Override
    public String toString() {return heap.toString();}

    public ArrayList<No> getHeap() {return heap;}
}
