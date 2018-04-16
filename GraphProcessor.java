package p5;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class adds additional functionality to the graph as a whole.
 * 
 * Contains an instance variable, {@link #graph}, which stores information for all the vertices and edges.
 * @see #populateGraph(String)
 *  - loads a dictionary of words as vertices in the graph.
 *  - finds possible edges between all pairs of vertices and adds these edges in the graph.
 *  - returns number of vertices added as Integer.
 *  - every call to this method will add to the existing graph.
 *  - this method needs to be invoked first for other methods on shortest path computation to work.
 * @see #shortestPathPrecomputation()
 *  - applies a shortest path algorithm to precompute data structures (that store shortest path data)
 *  - the shortest path data structures are used later to 
 *    to quickly find the shortest path and distance between two vertices.
 *  - this method is called after any call to populateGraph.
 *  - It is not called again unless new graph information is added via populateGraph().
 * @see #getShortestPath(String, String)
 *  - returns a list of vertices that constitute the shortest path between two given vertices, 
 *    computed using the precomputed data structures computed as part of {@link #shortestPathPrecomputation()}.
 *  - {@link #shortestPathPrecomputation()} must have been invoked once before invoking this method.
 * @see #getShortestDistance(String, String)
 *  - returns distance (number of edges) as an Integer for the shortest path between two given vertices
 *  - this is computed using the precomputed data structures computed as part of {@link #shortestPathPrecomputation()}.
 *  - {@link #shortestPathPrecomputation()} must have been invoked once before invoking this method.
 *  
 * @author sapan (sapan@cs.wisc.edu)
 * 
 */
public class GraphProcessor {

    /**
     * Graph which stores the dictionary words and their associated connections
     */
    private GraphADT<String> graph;
    private ArrayList<String> wordList;
    private String [][] nextWords;
    private int [][] D;
    private String [][] A;

    /**
     * Constructor for this class. Initializes instances variables to set the starting state of the object
     */
    public GraphProcessor() {
        this.graph = new Graph<>();
    }
        
    /**
     * Builds a graph from the words in a file. Populate an internal graph, by adding words from the dictionary as vertices
     * and finding and adding the corresponding connections (edges) between 
     * existing words.
     * 
     * Reads a word from the file and adds it as a vertex to a graph.
     * Repeat for all words.
     * 
     * For all possible pairs of vertices, finds if the pair of vertices is adjacent {@link WordProcessor#isAdjacent(String, String)}
     * If a pair is adjacent, adds an undirected and unweighted edge between the pair of vertices in the graph.
     * 
     * @param filepath file path to the dictionary
     * @return Integer the number of vertices (words) added
     */
    public Integer populateGraph(String filepath) {
    		Stream <String> stream = null;
    		try {
    			stream = WordProcessor.getWordStream(filepath);
    		}
    		catch (IOException e) {
    			System.out.println("Unable to find the file: " + filepath);
    			System.exit(0);
    		}
		
    		List<String> wordList = stream.collect(Collectors.toList());
    		for (String str : wordList) {
    			graph.addVertex(str);
    		}
    		for(int i = 0; i < wordList.size(); i++) {
    			for(int p = 0; p < wordList.size(); p++) {
    				if(WordProcessor.isAdjacent(wordList.get(i), wordList.get(p)))
    					graph.addEdge(wordList.get(i), wordList.get(p));
    			}
    		}
    		
    		return wordList.size();
    }

    
    /**
     * Gets the list of words that create the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     *             cat
     *             rat
     *             hat
     *             neat
     *             wheat
     *             kit
     *  shortest path between cat and wheat is the following list of words:
     *     [cat, hat, heat, wheat]
     * 
     * @param word1 first word
     * @param word2 second word
     * @return List<String> list of the words
     */
    public List<String> getShortestPath(String word1, String word2) {
    	int i = wordList.indexOf(word1);
    	int j = wordList.indexOf(word2);
    	String nextWord = "";
    	List<String> path = new ArrayList<String>();
    	if (nextWords[i][j] == null) {
    		return path;
    	} 
    	else {
    		path.add(word1);
    		while (!nextWord.equals(word2)) {
    			nextWord = next[i][j];
    			index1 = wordList.indexOf(nextWord);
    			path.add(nextWord);
    	}
    	return path; 
    	}
    }
    
    /**
     * Gets the distance of the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     *             cat
     *             rat
     *             hat
     *             neat
     *             wheat
     *             kit
     *  distance of the shortest path between cat and wheat, [cat, hat, heat, wheat]
     *   = 3 (the number of edges in the shortest path)
     * 
     * @param word1 first word
     * @param word2 second word
     * @return Integer distance
     */
    public Integer getShortestDistance(String word1, String word2) {
        //return distance from index of word 1 to word 2
        int i = wordList.indexOf(word1);
        int j = wordList.indexOf(word2);
        return distance[i][j];
    }
    
    /**
     * Computes shortest paths and distances between all possible pairs of vertices.
     * This method is called after every set of updates in the graph to recompute the path information.
     * Any shortest path algorithm can be used (Djikstra's or Floyd-Warshall recommended).
     */
    
    /*
     * D the amount of vertices visited from one edge to another
     * A tracks the shortest path to and from all edges
     */
    public void shortestPathPrecomputation() {
    	V = wordList.size();
    	D = [V][V]; //distance matrix initialized to amount of words in graph
    	A = [V][V]; // adjacencyMatrix, matrix to find shortest path
	final static int INF = 9999;    
    	for (int[] row: A) {
    		Arrays.fill(A, INF);
    	}	    
    	// iterate through graph to find edges to update distance and next edge		
    	Iterator<String> vertexList = graph.getAllVertices().iterator();	
    	while(vertexList.hasNext()) {
    		String vertex = vertexList.next();
    		int vertexIndex = vertexList.indexOf(vertex));
    		Iterator<String> edgeList = graph.getNeighbors(vertex).iterator();
    		while(edgeList.hasNext()) {
    			String edge = edgeList.next();
    			edgeIndex = edgeList.indexOf(edge);
    			D[vertexIndex][edgeIndex] = 1;
    			A[vertexIndex][edgeIndex] = edge;
    		}
    	}
    	// Floyd-Warshall algorithm with path reconstruction
    	for (int k = 0; k < V; k++) {
    		for (int i = 0; i < V; i++) {
    			for (int j = 0; j < V; j++) {
    				if (D[i][k] + D[k][j] < D[i][j]) {
    					D[i][j] = D[i][k] + D[k][j];
    					A[i][j] = A[i][k];
    				}
    			}
		}
	}
    }
}
