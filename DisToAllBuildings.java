package databricks;


import java.util.*;

public class DisToAllBuildings {
    public int shortestDistance(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int totalHouses = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    totalHouses++;
                }
            }
        }

        int res = Integer.MAX_VALUE;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                boolean[][] visited = new boolean[m][n];
                if (grid[i][j] == 0 && !visited[i][j]) {
                    int dis = dfs(grid, visited, i, j, m, n, totalHouses);
                    res = Math.min(res, dis);
                }
            }
        }

        return res;
    }

    private int dfs(int[][] grid, boolean[][] visited, int x, int y, int m, int n, int houseToVisit) {
        Queue<int[]> queue = new LinkedList<>();
        int step = 0;
        int[] dirx = {-1, 0, 1, 0};
        int[] diry = {0, -1, 0, 1};
        queue.add(new int[]{x, y});
        visited[x][y] = true;
        int dis = 0;
        while (!queue.isEmpty()) {
            Queue<int[]> childrenQ = new LinkedList<>();
            step++;
            while (!queue.isEmpty()) {
                int[] pos = queue.poll();
                x = pos[0];
                y = pos[1];
                for (int i = 0; i < 4; i++) {
                    int x1 = x + dirx[i];
                    int y1 = y + diry[i];
                    if (x1 < 0 || x1 >= m || y1 < 0 || y1 >= n) {
                        continue;
                    } else if (grid[x1][y1] == 0 && !visited[x1][y1]) {
                        childrenQ.offer(new int[]{x1, y1});
                        visited[x1][y1] = true;
                    } else if (grid[x1][y1] == 1 && !visited[x1][y1]) {
                        visited[x1][y1] = true;
                        dis += step;
                        houseToVisit--;
                    }
                }

            }
            if (houseToVisit == 0) {
                return dis;
            }
            queue = childrenQ;
        }
        return Integer.MAX_VALUE;
    }

    public static void main(String[] args) {
        DisToAllBuildings disToAllBuildings = new DisToAllBuildings();
        System.out.println(disToAllBuildings.shortestDistance(new int[][]{{1, 0, 2, 0, 1}, {0, 0, 0, 0, 0}, {0, 0, 1, 0, 0}}));
    }
}