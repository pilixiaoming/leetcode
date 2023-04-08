package databricks;

import java.util.*;


public class ShortestWay {

    static final int[] dirx = {-1, 0, 1, 0};
    static final int[] diry = {0, -1, 0, 1};

    public char findLowestCostTravelMethod(int [] start, int [] end, char[][] travel, int[] methodTime, int[] methodCost) throws Exception {
        if (travel.length == 0 || (start[0] == end[0] && start[1] == end[1])) {
            return '-';
        }
        int m = travel.length;
        int n = travel[0].length;
        boolean[][] visitedMap = new boolean[m][n];
        PriorityQueue<CostPoint> minHeap = new PriorityQueue<>((o1, o2) -> {
            if (o1.travelCost.time == o2.travelCost.time) {
                return o1.travelCost.cost - o2.travelCost.cost;
            } else {
                return o1.travelCost.time - o2.travelCost.time;
            }
        });

        // init
        visitedMap[start[0]][start[1]] = true;
        for (int i=0; i<4; i++) {
            int nextX = start[0] + dirx[i];
            int nextY = start[1] + diry[i];
            if (checkPointToSkip(nextX, nextY, visitedMap, m, n) || travel[nextX][nextY] == 'x') {
                continue;
            }
            char travelMethod = travel[nextX][nextY];
            minHeap.offer(new CostPoint(new int [] {nextX, nextY}, travelMethod, new TravelCost(methodTime[travelMethod - '1'], methodCost[travelMethod - '1'])));
        }

        // travel
        while (!minHeap.isEmpty()) {
            CostPoint curCostPoint = minHeap.poll();
            int [] curPoint = curCostPoint.point;
            char curMethod = curCostPoint.method;

            // mark visited here
            visitedMap[curPoint[0]][curPoint[1]] = true;

            if (curPoint[0] == end[0] && curPoint[1] == end[1]) {
                return curMethod;
            }

            // check neighbors
            for (int i=0; i<4; i++) {
                int nextX = curPoint[0] + dirx[i];
                int nextY = curPoint[1] + diry[i];
                if (checkPointToSkip(nextX, nextY, visitedMap, m, n) || (travel[nextX][nextY] != 'D' && travel[nextX][nextY] != curMethod)) {
                    continue;
                }

                minHeap.offer(new CostPoint(new int []{nextX, nextY}, curMethod, new TravelCost(curCostPoint.travelCost.time + methodTime[curMethod - '1'], curCostPoint.travelCost.cost + methodCost[curMethod - '1'])));
            }
        }

        throw new Exception("unable to reach destination");
    }

    private boolean checkPointToSkip(int x, int y, boolean[][] visitedMap, int m, int n) {
        return x < 0 || x >= m || y < 0 || y >= n || visitedMap[x][y];
    }


    public static void main(String[] args) throws Exception {
        char[][] grid = {{'3', '3', 'S', '2', 'X', 'X'},
                {'3', '1', '1', '2', 'X', '2'},
                {'3', '1', '1', '2', '2', '2'},
                {'3', '1', '1', '1', 'D', '3'},
                {'3', '3', '3', '3', '3', '4'},
                {'4', '4', '4', '4', '4', '4'}};
        int[] time = {3, 2, 1, 1};
        int[] cost = {0, 1, 3, 2};
        ShortestWay shortestWay = new ShortestWay();
        char res = shortestWay.findLowestCostTravelMethod(new int [] {0, 2}, new int []{3, 4}, grid, time, cost);
        System.out.println(res);
    }
}

class TravelCost {
    public int time;
    public int cost;

    public TravelCost(int time, int cost) {
        this.time = time;
        this.cost = cost;
    }
}

class CostPoint {
    public int [] point;
    public char method;
    public TravelCost travelCost;

    public CostPoint(int [] point, char method, TravelCost travelCost) {
        this.point = point;
        this.method = method;
        this.travelCost = travelCost;
    }
}