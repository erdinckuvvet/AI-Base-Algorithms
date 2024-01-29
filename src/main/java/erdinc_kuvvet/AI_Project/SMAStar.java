package erdinc_kuvvet.AI_Project;

/**
 *
 * @author erdinc.kuvvet
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.jgrapht.Graphs;

public class SMAStar {

    public static void main(String[] args) {
        // Ağırlıklı bir graf oluştur
        Graph<String, DefaultWeightedEdge> weightedGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Dosyadan komşuluk listesini al 
        ArrayList<String> adjacencyList = getAdjacencyList();
        String[] nodeNamesWithHeuristic = adjacencyList.get(0).split(",");   //düğüm isimleri ve heuristik cost
        int[] heuristicCosts = new int[nodeNamesWithHeuristic.length];
        String[] sourceAndTarget = adjacencyList.get(1).split(","); // başlangıç ve bitiş düğümleri

        for (int i = 0; i < nodeNamesWithHeuristic.length; i++) {
            weightedGraph.addVertex(nodeNamesWithHeuristic[i].charAt(0) + "");
            heuristicCosts[i] = Integer.parseInt(nodeNamesWithHeuristic[i].substring(2, 3));
        }

        for (int i = 2; i < adjacencyList.size(); i++) {
            String[] edge = adjacencyList.get(i).split(",");
            DefaultWeightedEdge edgeX = weightedGraph.addEdge(edge[0], edge[1]);
            weightedGraph.setEdgeWeight(edgeX, Double.parseDouble(edge[2]));
        }

        String sourceVertex = sourceAndTarget[0];
        String targetVertex = sourceAndTarget[1];

        List<String> shortestPath = findShortestPath(weightedGraph, sourceVertex, targetVertex);
        if (shortestPath != null) {
            System.out.println("En kısa yol: " + shortestPath);
        } else {
            System.out.println("Yol bulunamadı.");
        }
    }

    public static ArrayList getAdjacencyList() {
        ArrayList<String> adjacencyList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("files\\map_with_heuristic.txt"))) {
            String s;
            while ((s = reader.readLine()) != null) {
                adjacencyList.add(s);
            }
        } catch (Exception e) {
            System.out.println("file error!");
        }

        return adjacencyList;
    }

    /**
     * Ağırlıklı graf içinde belirli iki düğüm arasındaki en kısa yolu bulan
     * metot. Bu metod, A* algoritmasını kullanır.
     *
     * @param graph: Ağırlıklı graf.
     * @param sourceVertex: Başlangıç düğümü.
     * @param targetVertex: Hedef düğüm.
     * @return En kısa yolun düğüm listesini içeren bir List<String> veya null,
     * eğer yol bulunamazsa.
     */
    public static List<String> findShortestPath(Graph<String, DefaultWeightedEdge> graph, String sourceVertex, String targetVertex) {
        // A* algoritması için kullanılacak öncelikli kuyruk ve düğüm haritalarını oluştur.
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getTotalCost));
        Map<String, Node> nodes = new HashMap<>();

        // Başlangıç düğümünü oluştur ve kuyruğa ekle.
        Node startNode = new Node(sourceVertex, null, 0, heuristicCostEstimate(graph, sourceVertex, targetVertex));
        openSet.add(startNode);

        // Kuyruk boş olana kadar devam et.
        while (!openSet.isEmpty()) {
            // En küçük toplam maliyete sahip düğümü seç ve kuyruktan çıkar.
            Node currentNode = openSet.poll();

            // Hedef düğüme ulaşıldıysa, en kısa yolu oluştur ve döndür.
            if (currentNode.getVertex().equals(targetVertex)) {
                return reconstructPath(currentNode);
            }

            // Mevcut düğümün komşularını kontrol et.
            for (DefaultWeightedEdge edge : graph.edgesOf(currentNode.getVertex())) {
                String neighbor = Graphs.getOppositeVertex(graph, edge, currentNode.getVertex());

                // Yeni maliyet hesaplamaları yap ve komşu düğümü güncelle.
                double tentativeCost = currentNode.getCost() + graph.getEdgeWeight(edge);
                double heuristicCost = heuristicCostEstimate(graph, neighbor, targetVertex);
                double totalCost = tentativeCost + heuristicCost;

                Node neighborNode = nodes.get(neighbor);
                if (neighborNode == null || tentativeCost < neighborNode.getCost()) {
                    if (neighborNode == null) {
                        // Yeni komşu düğümü oluştur ve haritalara ekleyerek kuyruğa ekle.
                        neighborNode = new Node(neighbor, currentNode, tentativeCost, heuristicCost);
                        nodes.put(neighbor, neighborNode);
                        openSet.add(neighborNode);
                    } else {
                        // Komşu düğüm zaten varsa, maliyetleri güncelle ve kuyruğu tekrar sırala.
                        openSet.remove(neighborNode);
                        neighborNode.setParent(currentNode);
                        neighborNode.setCost(tentativeCost);
                        neighborNode.setHeuristicCost(heuristicCost);
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null; // Hedefe ulaşılamadı.
    }

    private static double heuristicCostEstimate(Graph<String, DefaultWeightedEdge> graph, String currentVertex, String targetVertex) {
        switch (currentVertex) {
            case "S":
                return 9;
            case "A":
                return 6;
            case "B":
                return 4;
            case "C":
                return 3;
            case "D":
                return 3;
            case "E":
                return 0;
            default:
                return 0;
        }
    }

    /**
     * A* algoritması sırasında belirli bir hedef düğümünden başlayarak geriye
     * doğru yolu oluşturan yardımcı metot.
     *
     * @param targetNode: Hedef düğüm.
     * @return Belirli bir hedef düğümünden başlayarak oluşturulan en kısa yolu
     * içeren bir List<String>.
     */
    private static List<String> reconstructPath(Node targetNode) {
        List<String> path = new ArrayList<>();
        Node currentNode = targetNode;

        // Hedef düğümden başlayarak geriye doğru yolu oluştur.
        while (currentNode != null) {
            path.add(currentNode.getVertex());
            currentNode = currentNode.getParent();
        }

        // Oluşturulan yolu tersine çevir, çünkü yolu hedeften başlangıca doğru oluşturduk.
        Collections.reverse(path);
        return path;
    }

    private static class Node {

        private String vertex;
        private Node parent;
        private double cost;
        private double heuristicCost;

        public Node(String vertex, Node parent, double cost, double heuristicCost) {
            this.vertex = vertex;
            this.parent = parent;
            this.cost = cost;
            this.heuristicCost = heuristicCost;
        }

        public String getVertex() {
            return vertex;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getHeuristicCost() {
            return heuristicCost;
        }

        public void setHeuristicCost(double heuristicCost) {
            this.heuristicCost = heuristicCost;
        }

        public double getTotalCost() {
            return cost + heuristicCost;
        }
    }

}
