package databricks;

import java.util.*;

class RandomHashMap<K, V> {
    private Map<K, V> internalMap = new HashMap<K, V>();
    private Map<K, Integer> keyToIndex = new HashMap<K, Integer>(); // for fast remove
    private Map<Integer, K> indexToKey = new HashMap<Integer, K>(); // for random
    private Random random = new Random();

    public V get(K key) {
        return internalMap.get(key);
    }

    public void put(K key, V value) {
        boolean existed = internalMap.containsKey(key);

        internalMap.put(key, value);
        if (existed) return;

        int index = keyToIndex.size();
        keyToIndex.put(key, index);
        indexToKey.put(index, key);
    }

    public boolean delete(K key) {
        if (internalMap.remove(key) == null) {
            return false;
        }

        // swap the last index to the current key's index
        int lastIndex = indexToKey.size() - 1;
        K lastKey = indexToKey.get(lastIndex);
        int currentIndex = keyToIndex.get(key);

        indexToKey.put(currentIndex, lastKey);
        keyToIndex.put(lastKey, currentIndex);

        indexToKey.remove(lastIndex);
        keyToIndex.remove(key);

        return true;
    }

    public K random() {
        return indexToKey.get(random.nextInt(indexToKey.size()));
    }

    public static void main(String[] args) {
        RandomHashMap<String, String> randomHashMap = new RandomHashMap<>();
        randomHashMap.put("A", "A");
        randomHashMap.put("B", "B");
        randomHashMap.put("C", "C");
        randomHashMap.put("D", "D");
        System.out.println(randomHashMap.random());
    }
}