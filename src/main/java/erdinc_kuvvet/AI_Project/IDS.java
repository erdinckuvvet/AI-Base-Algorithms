package erdinc_kuvvet.AI_Project;

/**
 *
 * @author erdinc.kuvvet
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.jgrapht.Graphs;

public class IDS {

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

    public static <V, E> List<V> findShortestPath(Graph<V, E> graph, V sourceVertex, V targetVertex) {
        int maxDepth = graph.vertexSet().size(); // Maksimum derinlik, grafın düğüm sayısı kadar olabilir

        // Belirli bir maksimum derinlikle başlayarak en kısa yolu bulma girişimi.
        for (int depth = 1; depth <= maxDepth; depth++) {
            List<V> path = depthLimitedSearch(graph, sourceVertex, targetVertex, depth);
            if (path != null) {
                return path; // En kısa yol bulundu, döndürülür.
            }
        }

        return null; // Hedefe ulaşılamadı.
    }

    /**
     * Derinlik sınırlı arama kullanarak belirli bir derinlik seviyesinde en
     * kısa yolu bulan metot.
     *
     * @param graph: Arama yapılacak graf.
     * @param currentVertex: Şu anki düğüm.
     * @param targetVertex: Hedef düğüm.
     * @param depth: Derinlik seviyesi.
     * @return En kısa yolun düğüm listesini içeren bir List<V> veya null, eğer
     * yol bulunamazsa.
     */
    private static <V, E> List<V> depthLimitedSearch(Graph<V, E> graph, V currentVertex, V targetVertex, int depth) {
        if (depth == 0 && currentVertex.equals(targetVertex)) {
            // Derinlik sıfıra ulaşıldı ve hedef düğüme ulaşıldıysa, bir yol bulundu.
            List<V> path = new ArrayList<>();
            path.add(currentVertex);
            return path;
        }

        if (depth > 0) {
            for (E edge : graph.edgesOf(currentVertex)) {
                V neighbor = Graphs.getOppositeVertex(graph, edge, currentVertex);
                List<V> path = depthLimitedSearch(graph, neighbor, targetVertex, depth - 1);

                if (path != null) {
                    path.add(0, currentVertex); // Bulunan yolu başa ekler.
                    return path;
                }
            }
        }

        return null; // Hedefe ulaşılamadı.
    }
}
