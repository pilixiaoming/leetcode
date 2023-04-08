package databricks;

import java.util.*;

class KLargestKeyValuePair {
    private final int k;
    private static final int MAX_MEM_SIZE = 1024;

    public KLargestKeyValuePair(int k) {
        this.k = k;
    }

    public void map(List<KeyValuePair> kvs) {
        Map<String, List<Integer>> keyMaps = new HashMap<>();
        for (KeyValuePair pair : kvs) {
            keyMaps.computeIfAbsent(pair.getKey(), k -> new ArrayList<>()).add(pair.getValue());
        }

        for (Map.Entry<String, List<Integer>> entry : keyMaps.entrySet()) {
            MapReduceHelper.writeToFile(entry.getKey(), entry.getValue());
        }
    }

    public void reduce(String key, Iterator<Integer> valueIterator) {
        List<Integer> kLargest = mergeLargest(valueIterator, k, null);

        MapReduceHelper.writeToFile(key, kLargest);
    }

    public void reduceWithLargeK(String key, Iterator<Integer> valueIterator) {
        int curSize = Math.min(MAX_MEM_SIZE, k);
        Iterator<Integer> iterator = valueIterator;
        int remain = k;
        Integer maxNum = null;

        while (remain > 0) {
            // returns a list of the curSize largest integers seen so far in the input valueIterator,
            // excluding any integers larger than maxNum
            List<Integer> curLargest = mergeLargest(iterator, curSize, maxNum);
            // write to result file
            MapReduceHelper.writeAppendToFile(key, curLargest);

//            maxNum = curLargest.get(curLargest.size() - 1);
            maxNum = curLargest.get(0);
            remain -= curSize;
            iterator = MapReduceHelper.getIntermediateResult(key);
        }
    }

    private List<Integer> mergeLargest(Iterator<Integer> valueIterator, int size, Integer maxNum) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        while (minHeap.size() < size && valueIterator.hasNext()) {
            int cur = valueIterator.next();
            if (maxNum != null && cur >= maxNum) continue;
            minHeap.offer(cur);
        }

        while (valueIterator.hasNext()) {
            int curValue = valueIterator.next();

            if (maxNum != null && curValue >= maxNum) continue;

            if (curValue > minHeap.peek()) {
                minHeap.poll();
                minHeap.offer(curValue);
            }
        }

        return new ArrayList<>(minHeap);
    }
}

class KeyValuePair {
    private final String key;
    private final int value;

    public KeyValuePair(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}

class MapReduceHelper {
    public static void writeToFile(String key, List<Integer> values) {}

    public static void writeAppendToFile(String key, List<Integer> values) {}

    public static Iterator<Integer> getIntermediateResult(String key) {
//        return Collections.emptyList().iterator();
        return null;
    }
}

