package erdinc_kuvvet.AI_Project;

/**
 *
 * @author erdinc.kuvvet
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.jgrapht.Graphs;

public class RBFS {

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

    /**
     * Ağırlıklı graf içinde belirli iki düğüm arasındaki en kısa yolu bulan
     * metot. Bu metod, genişlik öncelikli arama algoritmasını kullanır.
     *
     * @param graph: Ağırlıklı graf.
     * @param sourceVertex: Başlangıç düğümü.
     * @param targetVertex: Hedef düğüm.
     * @return En kısa yolun düğüm listesini içeren bir List<String> veya null,
     * eğer yol bulunamazsa.
     */
    public static List<String> findShortestPath(Graph<String, DefaultWeightedEdge> graph, String sourceVertex, String targetVertex) {
        // Kuyruk veri yapısı kullanılarak genişlik öncelikli arama için gerekli veri yapıları oluşturulur.
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();

        // Başlangıç düğümü kuyruğa eklenir ve ziyaret edildi olarak işaretlenir.
        queue.add(sourceVertex);
        visited.add(sourceVertex);

        // Kuyruk boş olana kadar devam et.
        while (!queue.isEmpty()) {
            // Kuyruktan bir düğüm çıkarılır.
            String currentVertex = queue.poll();

            // Hedef düğüme ulaşıldıysa, en kısa yolu oluşturup döndür.
            if (currentVertex.equals(targetVertex)) {
                return reconstructPath(parentMap, sourceVertex, targetVertex);
            }

            // Mevcut düğümün komşularını kontrol et.
            for (DefaultWeightedEdge edge : graph.edgesOf(currentVertex)) {
                String neighbor = Graphs.getOppositeVertex(graph, edge, currentVertex);

                // Eğer komşu ziyaret edilmemişse, ziyaret edildi olarak işaretle, kuyruğa ekle ve parent'ını güncelle.
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parentMap.put(neighbor, currentVertex);
                }
            }
        }

        return null; // Hedefe ulaşılamadı.
    }

    /**
     * Düğüm-parent eşleştirmesini kullanarak belirli bir başlangıç ve hedef
     * düğüm arasındaki yolu oluşturan yardımcı metot.
     *
     * @param parentMap: Düğüm-parent eşleştirmesi.
     * @param sourceVertex: Başlangıç düğümü.
     * @param targetVertex: Hedef düğüm.
     * @return Belirli bir başlangıç ve hedef düğüm arasındaki en kısa yolu
     * içeren bir List<String>.
     */
    private static List<String> reconstructPath(Map<String, String> parentMap, String sourceVertex, String targetVertex) {
        List<String> path = new ArrayList<>();
        String currentVertex = targetVertex;

        // Hedef düğümden başlayarak geriye doğru yolu oluştur.
        while (currentVertex != null) {
            path.add(currentVertex);
            currentVertex = parentMap.get(currentVertex);
        }

        // Oluşturulan yolu tersine çevir, çünkü yolu hedeften başlangıca doğru oluşturduk.
        Collections.reverse(path);
        return path;
    }

}
