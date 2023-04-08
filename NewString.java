package databricks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NewString {

    private static final int DEFAULT_MAX_STR_SIZE = 20;

    private Map<Integer, LinkedList<Character>> indexToBlockMap;//{0:[a,b,c,d,e]; 5:[a,b,c,d,e]; 10:[a,b,c,d,e]}
    private List<Integer> indexList; //[0, 5, 10, 15]
    private int blockSize;
    private int totalSize;

    public NewString() {
        this(DEFAULT_MAX_STR_SIZE);
    }

    public NewString(int N) {
        this.blockSize = (int) Math.sqrt(N) + 1;
        this.indexToBlockMap = new HashMap<>();
        this.indexList = new ArrayList<>();
        this.totalSize = 0;
    }

    private int findBlockIndex(int i) {
        return i/blockSize;
    }

//    private int findBlockIndex(int i) {
//        int l = 0, r = indexList.size() - 1;
//
//        while (l <= r) {
//            int mid = l + (r - l) / 2;
//            if (indexList.get(mid) == i) {
//                return mid;
//            } else if (indexList.get(mid) < i) {
//                l = mid + 1;
//            } else {
//                r = mid - 1;
//            }
//        }
//
//        return l - 1;
//    }

    public Character read(int i) {
        if (i < 0 || i >= totalSize || indexToBlockMap.isEmpty()) {
            return null;
        }

        int bi = findBlockIndex(i);
        int startIndex = indexList.get(bi);
        LinkedList<Character> list = indexToBlockMap.get(startIndex);

        return list.get(i - startIndex);
    }

    public void insert(int i, char c) {
        if (i < 0 || i > totalSize) {
            return;
        }

        if (indexToBlockMap.isEmpty()) {
            LinkedList<Character> list = new LinkedList<>();
            list.add(c);
            indexToBlockMap.put(0, list);
            indexList.add(0);
        } else {
            int bi = findBlockIndex(i);
            int startIndex = indexList.get(bi);
            LinkedList<Character> list = indexToBlockMap.get(startIndex);
            list.add(i - startIndex, c);

            // remove last ele and update indice after
            // loop each block and swift last element to next block
            Character poppedTail = null;
            for (int k = bi; k < indexList.size(); k++) {
                LinkedList<Character> blockList = indexToBlockMap.get(indexList.get(k));
                if (poppedTail != null) {
                    blockList.addFirst(poppedTail);
                }

                if (k != indexList.size() - 1) {
                    poppedTail = blockList.removeLast();
                }
            }

            // see if we need to allocate more blocks for last block list
            int lastStartIndex = indexList.get(indexList.size() - 1);
            LinkedList<Character> lastBlockList = indexToBlockMap.get(lastStartIndex);

            if (lastBlockList.size() > blockSize) {
                // split block
                int newStartIndex = lastStartIndex + blockSize;
                LinkedList<Character> newBlockList = new LinkedList<>();

                while (lastBlockList.size() > blockSize) {
                    newBlockList.addFirst(lastBlockList.removeLast());
                }

                indexToBlockMap.put(newStartIndex, newBlockList);
                indexList.add(newStartIndex);
            }
        }

        this.totalSize += 1;
    }

    public void delete(int i) {
        if (i < 0 || i >= totalSize || indexToBlockMap.isEmpty()) {
            return;
        }

        int bi = findBlockIndex(i);
        int startIndex = indexList.get(bi);
        LinkedList<Character> list = indexToBlockMap.get(startIndex);
        list.remove(i - startIndex);

        // update indice after (move forward)
        Character poppedHead = null;
        for (int k = indexList.size() - 1; k >= bi; k--) {
            LinkedList<Character> blockList = indexToBlockMap.get(indexList.get(k));
            if (poppedHead != null) {
                blockList.addLast(poppedHead);
            }

            if (k != bi) {
                poppedHead = blockList.removeFirst();
            }
        }

        // see if we need to remove last block
        int lastStartIndex = indexList.get(indexList.size() - 1);
        LinkedList<Character> lastBlockList = indexToBlockMap.get(lastStartIndex);
        if (lastBlockList.size() == 0) {
            indexList.remove(indexList.size() - 1);
            indexToBlockMap.remove(lastStartIndex);
        }

        this.totalSize -= 1;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indexList.size(); i++) {
            LinkedList<Character> list = indexToBlockMap.get(indexList.get(i));
            for (char c : list) {
                b.append(c);
            }

//            if (i != indexList.size() - 1) {
            b.append(",");
//            }
        }

        return b.toString();
    }

    public static void main(String[] args) {
        NewString ns = new NewString();
        ns.insert(0, 'a');
        ns.insert(0, 'b');
        ns.insert(0, 'c');
        ns.insert(0, 'd');
        ns.insert(0, 'e');
        ns.insert(0, 'f');
        ns.insert(0, 'g');
        System.out.println(ns.toString());


        ns.insert(0, 'h');
        ns.insert(1, 'i');
        ns.insert(2, 'j');
        ns.insert(3, 'k');
        ns.insert(4, 'l');
        ns.insert(5, 'f');
        ns.insert(6, 'm');
        ns.insert(7, 'n');
        ns.insert(12, 'x');
        System.out.println(ns.toString());

        char x = ns.read(3);
        System.out.println("3 " + x);
        System.out.println("5 " + ns.read(5));
        char z = ns.read(12);
        System.out.println("12 " + z);


        ns.delete(12);
        System.out.println("del 12 " + ns.toString());

        ns.delete(1);
        System.out.println("del 1 " + ns.toString());
        ns.delete(2);
        System.out.println("del 2 " + ns.toString());
        ns.delete(0);
        System.out.println("del 0 " + ns.toString());

        ns.delete(0);
        ns.delete(0);
        ns.delete(0);
        System.out.println(ns.toString());
    }


//         ns.delete(100);
//         System.out.println(ns.toString());

//         ns.insert(100, 'e');
//         System.out.println(ns.toString());


}


