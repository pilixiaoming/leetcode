package databricks;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MockHashMap2 {
    private final Map<String, String> kv = new HashMap<>();
    private final LinkedHashMap<Long, Integer> getMetrics = new LinkedHashMap<>();
    private final LinkedHashMap<Long, Integer> putMetrics = new LinkedHashMap<>();
    private final int INTERVAL = 300;

    public String get(String key, long currentTime) {
        String value = kv.get(key);
        // long currentTime = System.currentTimeMillis() / 1000;
        getMetrics.merge(currentTime, 1, (oldValue, newValue) -> oldValue + newValue);
//        truncateMetricsMap(getMetrics, currentTime);
        return value;
    }

    public void put(String key, String value, long currentTime) {
        kv.put(key, value);
        // long currentTime = System.currentTimeMillis() / 1000;
//        putMetrics.merge(currentTime, 1, Integer::sum);
        putMetrics.merge(currentTime, 1, (oldValue, newValue) -> oldValue + newValue);
//        truncateMetricsMap(putMetrics, currentTime);
    }

    public int getMetrics(long currentTime) {
        // long currentTime = System.currentTimeMillis() / 1000;
        truncateMetricsMap(getMetrics, currentTime);
        return getMetrics.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int putMetrics(long currentTime) {
        // long currentTime = System.currentTimeMillis() / 1000;
        truncateMetricsMap(putMetrics, currentTime);
        return putMetrics.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void truncateMetricsMap(LinkedHashMap<Long, Integer> metricsMap, long currentTime) {
        metricsMap.entrySet().removeIf(longIntegerEntry -> longIntegerEntry.getKey() < currentTime - INTERVAL);
    }

    public static void main(String[] args) {
        MockHashMap2 mockHashMap = new MockHashMap2();
        mockHashMap.put("1", "v", 1);
        mockHashMap.put("2", "v", 100);
        mockHashMap.put("3", "v", 200);
        mockHashMap.put("4", "v", 200);
        mockHashMap.put("5", "v1", 400);
        mockHashMap.put("5", "v2", 400);
        mockHashMap.put("6", "v", 500);
        System.out.println(mockHashMap.putMetrics(801));
    }
}