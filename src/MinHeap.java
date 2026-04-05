import java.util.ArrayList;

public class MinHeap {
    private ArrayList<No> heap = new ArrayList<>();

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void inserir(No no) {
        heap.add(no);
        subir(heap.size() - 1);
    }

    public No removerMin() {
        if (heap.isEmpty()) return null;

        No min = heap.get(0);
        No ultimo = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, ultimo);
            descer(0);
        }

        return min;
    }

    private void subir(int i) {
        while (i > 0) {
            int pai = (i - 1) / 2;
            if (heap.get(i).compareTo(heap.get(pai)) >= 0) break;

            trocar(i, pai);
            i = pai;
        }
    }

    private void descer(int i) {
        int menor = i;
        int esq = 2 * i + 1;
        int dir = 2 * i + 2;

        if (esq < heap.size() && heap.get(esq).compareTo(heap.get(menor)) < 0) {
            menor = esq;
        }

        if (dir < heap.size() && heap.get(dir).compareTo(heap.get(menor)) < 0) {
            menor = dir;
        }

        if (menor != i) {
            trocar(i, menor);
            descer(menor);
        }
    }

    private void trocar(int i, int j) {
        No temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    @Override
    public String toString() {
        return heap.toString();
    }
}