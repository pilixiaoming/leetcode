package databricks;

import java.util.*;

public class Revenue {

    private int idCounter;
    private Map<Integer, Integer> idToRev;
    private TreeMap<Integer, Set<Integer>> revToIds;

    public Revenue() {
        this.idCounter = 0;
        this.idToRev = new HashMap<>();
        this.revToIds = new TreeMap<>();
    }

    public int insert(int rev) {
        int id = idCounter++;
        idToRev.put(id, rev);
//        revToIds.computeIfAbsent(rev, k -> new HashSet<>()).add(id);
        revToIds.putIfAbsent(rev, new HashSet<>());
        revToIds.get(rev).add(id);
        return id;
    }

    public int insert(int rev, int referralId) {
        int id = insert(rev);

        int oldRev = idToRev.get(referralId);
        idToRev.put(referralId, oldRev + rev);

        revToIds.get(oldRev).remove(referralId);
        if (revToIds.get(oldRev).isEmpty()) {
            revToIds.remove(oldRev);
        }
//        revToIds.computeIfAbsent(oldRev + rev, k -> new HashSet<>()).add(referralId);
        revToIds.putIfAbsent(oldRev + rev, new HashSet<>());
        revToIds.get(oldRev + rev).add(referralId);
        return id;
    }

    public List<Integer> getKLowestRevenue(int k, int targetRevenue) {
        System.out.println(revToIds);

        List<Integer> closetRevenueConsumerIds = new ArrayList<>();

        int nextTargetRevenue = targetRevenue;

        while (closetRevenueConsumerIds.size() < k) {
            Map.Entry<Integer, Set<Integer>> nextHigherRevenueEntry = revToIds.higherEntry(nextTargetRevenue);
            if (nextHigherRevenueEntry == null) {
                break;
            }

            Iterator<Integer> consumerIds = nextHigherRevenueEntry.getValue().iterator();
            while (closetRevenueConsumerIds.size() < k && consumerIds.hasNext()) {
                closetRevenueConsumerIds.add(consumerIds.next());
            }

            nextTargetRevenue = nextHigherRevenueEntry.getKey();
        }

        return closetRevenueConsumerIds;
    }

    public void print() {
        for (int k:idToRev.keySet()) {
            System.out.println(k +" : " + idToRev.get(k));
        }
    }



    public static void main(String[] args) {
        Revenue rs = new Revenue();

        int id1 = rs.insert(10);
        int id2 = rs.insert(50, id1);
        int id3 = rs.insert(20, id2);
        int id4 = rs.insert(30);
        int id5 = rs.insert(2);
        int id6 = rs.insert(100);
        int id7 = rs.insert(13, id3);
        int id8 = rs.insert(23, id5);
        int id9 = rs.insert(100, id7);

        rs.print();
        List<Integer> list = rs.getKLowestRevenue(4, 70);
        for (int i:list) {
            System.out.println(i);
        }
    }
}
