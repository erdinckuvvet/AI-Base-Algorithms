package erdinc_kuvvet.AI_Project;

/**
 *
 * @author erdinc.kuvvet
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFS {

    public static void main(String[] args) {
        // Ağırlıklı bir graf oluştur
        Graph<String, DefaultWeightedEdge> weightedGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Dosyadan komşuluk listesini al 
        ArrayList<String> adjacencyList = getAdjacencyList();
        String[] nodeNames = adjacencyList.get(0).split(",");   //düğüm isimleri
        String[] sourceAndTarget = adjacencyList.get(1).split(","); // başlangıç ve bitiş düğümleri

        for (String nodeName : nodeNames) {
            weightedGraph.addVertex(nodeName);
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

    private static List<String> findShortestPath(Graph<String, DefaultWeightedEdge> graph, String source, String target) {
        Set<String> visited = new HashSet<>();  // Ziyaret edilen düğümleri takip etmek için kullanılan küme.
        Queue<String> queue = new LinkedList<>(); // Sıraya eklenen düğümleri tutmak için kullanılan kuyruk.

        // Başlangıç düğümünü sıraya ekle ve ziyaret edildi olarak işaretle.
        queue.add(source);
        visited.add(source);

        Map<String, String> parent = new HashMap<>(); // Her düğümün parent'ını tutan bir harita.

        while (!queue.isEmpty()) {
            String currentVertex = queue.poll(); // Kuyruktan bir düğüm çıkar.

            if (currentVertex.equals(target)) {
                // Hedef düğüme ulaşıldı, en kısa yolu oluştur.
                List<String> path = new ArrayList<>();
                path.add(currentVertex);
                while (currentVertex != null) {
                    currentVertex = parent.get(currentVertex);
                    if (currentVertex != null) {
                        path.add(0, currentVertex);
                    }
                }
                return path;
            }

            // Bağlantılı düğümleri ziyaret et.
            for (DefaultWeightedEdge edge : graph.outgoingEdgesOf(currentVertex)) {
                String neighbor = graph.getEdgeTarget(edge);

                if (!visited.contains(neighbor)) {
                    // Ziyaret edilmemişse, ziyaret edildi olarak işaretle, kuyruğa ekle ve parent'ını güncelle.
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parent.put(neighbor, currentVertex);
                }
            }
        }

        return null; // Hedefe ulaşılamadı, yol bulunamadı.
    }

    public static ArrayList getAdjacencyList() {
        ArrayList<String> adjacencyList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("files\\map.txt"))) {
            String s;
            while ((s = reader.readLine()) != null) {
                adjacencyList.add(s);
            }
        } catch (Exception e) {
            System.out.println("file error!");
        }

        return adjacencyList;
    }

}
