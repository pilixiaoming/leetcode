package databricks;

import java.util.*;

/**
 * use ring buffer to record all the calls in the past 300s
 */
public class MockHashMap {
    private Map<String, String> internalMap = new HashMap();
    private static final int RECORD_TIME_ELAPSE_SECOND = 10;
    private int[] getLoadRecord = new int[RECORD_TIME_ELAPSE_SECOND];
    private int[] putLoadRecord = new int[RECORD_TIME_ELAPSE_SECOND];
    LoadPointer getPointer;
    LoadPointer putPointer;

    public MockHashMap() {
        long curTime = System.currentTimeMillis() / 1000;
        getPointer = new LoadPointer(0, curTime);
        putPointer = new LoadPointer(0, curTime);
    }


    public String get(String key) {
        logCurrentCall(getPointer, getLoadRecord);
        return internalMap.get(key);
    }

    public void put(String key, String value) {
        logCurrentCall(putPointer, putLoadRecord);
        internalMap.put(key, value);
    }

    public int measurePutLoad() {
        return getTotalLoad(putPointer, putLoadRecord) / RECORD_TIME_ELAPSE_SECOND;
    }

    public int measureGetLoad() {
        return getTotalLoad(getPointer, getLoadRecord) / RECORD_TIME_ELAPSE_SECOND;
    }

    private int getTotalLoad(LoadPointer pointer, int[] records) {
        long curTimeS = System.currentTimeMillis() / 1000;

        // gap is how many slots we need move backforward from cur slot to count the total load
        long gaps = RECORD_TIME_ELAPSE_SECOND - (curTimeS - pointer.timestampS);
        if (gaps <= 0) {
            return 0;
        }

        int count = 0;
        int mover = pointer.index;
        while (gaps > 0) {
            count += records[mover];
            mover = getNextMoveIndexBackward(mover - 1, RECORD_TIME_ELAPSE_SECOND);
            gaps--;
        }

        return count;
    }

    private int getNextMoveIndexBackward(int nextIndex, int size) {
        return (nextIndex < 0) ? size + nextIndex : nextIndex;
    }

    private void logCurrentCall(LoadPointer pointer, int[] records) {
        long curTimeS = System.currentTimeMillis() / 1000;

        if (curTimeS == pointer.timestampS) {
            records[pointer.index]++;
            return;
        }

        // reset the gap between to 0
        long gap = curTimeS - pointer.timestampS;
        int newIndex = new Long((pointer.index + gap) % RECORD_TIME_ELAPSE_SECOND).intValue();
        if (gap >= RECORD_TIME_ELAPSE_SECOND) {
            resetRecordSlot(0, records.length - 1, records);
        } else {
            // make sure the start index is within range of records
            resetRecordSlot((pointer.index + 1) % RECORD_TIME_ELAPSE_SECOND, newIndex, records);
        }

        // update
        records[newIndex] = 1;
        pointer.index = newIndex;
        pointer.timestampS = curTimeS;
    }

    private void resetRecordSlot(int startIndex, int endIndex, int[] record) {
        int gap = endIndex - startIndex + 1;
        int moveCount = (gap >= 0) ? gap : RECORD_TIME_ELAPSE_SECOND + gap;
        int mover = startIndex;

        while (moveCount > 0) {
            record[mover] = 0;
            mover = (mover + 1) % RECORD_TIME_ELAPSE_SECOND;
            moveCount--;
        }
    }

    public static void main(String[] args) {
        MockHashMap mockHashMap = new MockHashMap();
        mockHashMap.put("1","1");
        mockHashMap.put("2","2");
        mockHashMap.put("3","3");
        mockHashMap.put("4","4");
        mockHashMap.put("5","5");
        mockHashMap.put("6","6");
        mockHashMap.put("7","7");

        LinkedHashMap<Long, Integer> getMetrics = new LinkedHashMap<>();
    }
}

class LoadPointer {
    int index;
    long timestampS;

    public LoadPointer(int index, long timestampS) {
        this.index = index;
        this.timestampS = timestampS;
    }
}



