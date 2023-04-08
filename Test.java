package databricks;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Test {
    public static void main(String[] args) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.add(5);
        minHeap.add(1);
        minHeap.add(3);
        minHeap.add(8);
        minHeap.add(6);

        List<Integer> l = new ArrayList<>(minHeap);
        System.out.println(l.get(0));
    }

}
