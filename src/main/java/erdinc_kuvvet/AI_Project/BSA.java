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

public class BSA {

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
            shortestPath.add(0, sourceVertex);
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
     * Ağırlıklı graf içinde iki düğüm arasındaki en kısa yolu bulan metot. Bu
     * metod, çift yönlü genişlik öncelikli arama (Bidirectional Search) algoritmasını kullanır.
     *
     * @param graph: Ağırlıklı graf.
     * @param sourceVertex: Başlangıç düğümü.
     * @param targetVertex: Hedef düğüm.
     * @return En kısa yolun düğüm listesini içeren bir List<String> veya null,
     * eğer yol bulunamazsa.
     */
    public static List<String> findShortestPath(Graph<String, DefaultWeightedEdge> graph, String sourceVertex, String targetVertex) {
        // İleri ve geri arama sırasında ziyaret edilen düğümleri takip etmek için kullanılan küme veri yapıları.
        Set<String> forwardExplored = new HashSet<>();
        Set<String> backwardExplored = new HashSet<>();

        // İleri ve geri arama sırasında kullanılan kuyruk veri yapıları.
        Queue<String> forwardQueue = new LinkedList<>();
        Queue<String> backwardQueue = new LinkedList<>();

        // İleri ve geri arama sırasında düğüm-parent eşleştirmelerini tutan harita veri yapıları.
        Map<String, String> forwardParentMap = new HashMap<>();
        Map<String, String> backwardParentMap = new HashMap<>();

        // Başlangıç düğümlerini kuyruklara ekleyerek aramaya başla.
        forwardQueue.add(sourceVertex);
        backwardQueue.add(targetVertex);

        // Başlangıç düğümlerini ziyaret edildi olarak işaretle.
        forwardExplored.add(sourceVertex);
        backwardExplored.add(targetVertex);

        // İki yönlü arama sırasında kuyruklar boş olana kadar devam et.
        while (!forwardQueue.isEmpty() && !backwardQueue.isEmpty()) {
            // İleri ve geri yönlü düğümleri kuyruklardan çıkar.
            String forwardCurrent = forwardQueue.poll();
            String backwardCurrent = backwardQueue.poll();

            // İleri yönlü komşuları kontrol et.
            List<String> forwardNeighbors = new ArrayList<>(graph.vertexSet());
            forwardNeighbors.removeAll(forwardExplored);

            for (String forwardNeighbor : forwardNeighbors) {
                if (graph.containsEdge(forwardCurrent, forwardNeighbor)) {
                    // Eğer kenar varsa, ileri yönlü kuyruğa ekle, ziyaret edildi olarak işaretle ve parent'ını güncelle.
                    forwardQueue.add(forwardNeighbor);
                    forwardExplored.add(forwardNeighbor);
                    forwardParentMap.put(forwardNeighbor, forwardCurrent);

                    // Geri yönlü arama sırasında bu düğümü ziyaret etmişsek, en kısa yol bulundu demektir.
                    if (backwardExplored.contains(forwardNeighbor)) {
                        return constructPath(forwardNeighbor, forwardParentMap, backwardParentMap);
                    }
                }
            }

            // Geri yönlü komşuları kontrol et (Benzer şekilde işlemleri gerçekleştir).
            List<String> backwardNeighbors = new ArrayList<>(graph.vertexSet());
            backwardNeighbors.removeAll(backwardExplored);

            for (String backwardNeighbor : backwardNeighbors) {
                if (graph.containsEdge(backwardCurrent, backwardNeighbor)) {
                    backwardQueue.add(backwardNeighbor);
                    backwardExplored.add(backwardNeighbor);
                    backwardParentMap.put(backwardNeighbor, backwardCurrent);

                    if (forwardExplored.contains(backwardNeighbor)) {
                        return constructPath(backwardNeighbor, forwardParentMap, backwardParentMap);
                    }
                }
            }
        }

        return null; // İki yönlü arama sonucunda en kısa yol bulunamazsa null döndürülür.
    }

    /**
     * İki yönlü arama sırasında bulunan ortak düğüm ile birleştirilmiş ileri ve
     * geri düğüm-parent eşleştirmelerini kullanarak en kısa yolu oluşturan
     * metot.
     *
     * @param intersectionVertex: İki yönlü arama sırasında bulunan ortak düğüm.
     * @param forwardParentMap: İleri yönlü düğüm-parent eşleştirmesi.
     * @param backwardParentMap: Geri yönlü düğüm-parent eşleştirmesi.
     * @return En kısa yolun düğüm listesini içeren bir List<String>.
     */
    private static List<String> constructPath(String intersectionVertex, Map<String, String> forwardParentMap, Map<String, String> backwardParentMap) {
        List<String> path = new ArrayList<>();
        String current = intersectionVertex;

        // İleri yönlü düğüm-parent eşleştirmesini kullanarak ileri yönlü yolu oluştur.
        while (forwardParentMap.containsKey(current)) {
            path.add(0, current);
            current = forwardParentMap.get(current);
        }

        // Geri yönlü düğüm-parent eşleştirmesini kullanarak geri yönlü yolu oluştur.
        current = backwardParentMap.get(intersectionVertex);
        while (current != null) {
            path.add(current);
            current = backwardParentMap.get(current);
        }

        return path;
    }
}
