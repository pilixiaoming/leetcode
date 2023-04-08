package databricks;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dijkstra {

    public void calculateShortestPath(Node source) {
        source.distance = 0;
        Set<Node> settledNodes = new HashSet();
        Queue<Node> unsettledNodes = new PriorityQueue(Collections.singleton(source));
        while (!unsettledNodes.isEmpty()) {
            Node currentNode = unsettledNodes.poll();
            currentNode.adjacentNodes
                    .entrySet().stream()
                    .filter(entry -> !settledNodes.contains(entry.getKey()))
                    .forEach(entry -> {
                        evaluateDistanceAndPath(entry.getKey(), entry.getValue(), currentNode);
                        unsettledNodes.add(entry.getKey());
                    });
            settledNodes.add(currentNode);
        }
    }

    private void evaluateDistanceAndPath(Node adjacentNode, Integer edgeWeight, Node sourceNode) {
        Integer newDistance = sourceNode.distance + edgeWeight;
        if (newDistance < adjacentNode.distance) {
            adjacentNode.distance = newDistance;
            adjacentNode.shortestPath = Stream.concat(sourceNode.shortestPath.stream(), Stream.of(sourceNode))
                    .collect(Collectors.toList());
        }
    }

    public void printPaths(Node node){
        for (Node n : node.shortestPath) {
            System.out.print(n.name);
        }
        System.out.println(node.name);
        System.out.println(node.distance);
    }

//    public void printPaths(List<Node> nodes) {
//        nodes.forEach(node -> {
//            String path = node.shortestPath.stream()
//                    .map(Node::getName)
//                    .collect(Collectors.joining(" -> "));
//            System.out.println((path.isEmpty()
//                    ? node.getName() + node.distance
//                    : (path + node.getName() + ": "+node.distance))
//            );
//        });
//    }

    public static void main(String[] args) {

        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");

        nodeA.addAdjacentNode(nodeB, 2);
        nodeA.addAdjacentNode(nodeC, 4);

        nodeB.addAdjacentNode(nodeC, 3);
        nodeB.addAdjacentNode(nodeD, 1);
        nodeB.addAdjacentNode(nodeE, 5);

        nodeC.addAdjacentNode(nodeD, 2);

        nodeD.addAdjacentNode(nodeE, 1);
        nodeD.addAdjacentNode(nodeF, 4);

        nodeE.addAdjacentNode(nodeF, 2);

        Dijkstra dijkstra = new Dijkstra();
        dijkstra.calculateShortestPath(nodeA);
        dijkstra.printPaths(nodeF);


    }
}

class Node implements Comparable<Node> {

    String name;
    Integer distance = Integer.MAX_VALUE;
    List<Node> shortestPath = new LinkedList();
    Map<Node, Integer> adjacentNodes = new HashMap();

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addAdjacentNode(Node node, int weight) {
        adjacentNodes.put(node, weight);
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(this.distance, node.distance);
    }

}

