package databricks;

import java.util.*;

public class SnapshotSet {


    Map<Integer, Integer> snapshot = new HashMap<Integer, Integer>();
    Map<Integer, Integer> curset = new HashMap<Integer, Integer>();

    void add(Integer e) {
        if (snapshot.containsKey(e)) snapshot.put(e, 0); // not deleted
        else curset.put(e, 0);
    }

    void remove(Integer e) {
        if (snapshot.containsKey(e)) snapshot.put(e, 1); // deleted
        else curset.remove(e);
    }

    boolean contains(Integer e) {
        return snapshot.containsKey(e) && snapshot.get(e) == 0 || curset.containsKey(e);
    }

    Iterator<Integer> iterator() {
        snapshot = curset;
        curset = new HashMap<Integer, Integer>();
        return new MyIterator(snapshot, curset);
    }


    public static void main(String[] args) {
        SnapshotSet ss = new SnapshotSet();
        ss.add(8);
        ss.add(2);
        ss.add(5);
        ss.remove(5);
        Iterator<Integer> it = ss.iterator();
        System.out.println(ss.contains(2));
        ss.add(1);
        ss.remove(2);
        System.out.println(ss.contains(2));
        while (it.hasNext()) {
            System.out.print(it.next() + ",");
        }
        System.out.println();

        ss.add(5);
        it = ss.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + ",");
        }
        System.out.println(ss.contains(2));

    }
}


class MyIterator implements Iterator<Integer> {
    Map<Integer, Integer> snapshot;
    Map<Integer, Integer> curset;
    Iterator<Integer> it;

    MyIterator(Map<Integer, Integer> s, Map<Integer, Integer> c) {
        this.snapshot = s;
        this.curset = c;
        it = s.keySet().iterator();
    }

    @Override
    public boolean hasNext() {
        boolean res = it.hasNext();
        if (res == false) snapshot.clear();
        return res;
    }

    @Override
    public Integer next() {
        Integer res = it.next();
        if (snapshot.get(res) == 0) curset.put(res, 0);
        return res;
    }
}
