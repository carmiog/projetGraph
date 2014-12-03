package fr.antoinemarendet.graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
private List<Set<Integer>> adjacencyLists = new ArrayList<>();
	
	public Graph() {
		
	}
	
	public Graph(Graph g) {
		this();
		for(Set<Integer> nodeAdjacency : g.adjacencyLists) {
			Set<Integer> newList = Collections.synchronizedSet(new HashSet<Integer>());
			for(Integer adjacentNode : nodeAdjacency) {
				newList.add(new Integer(adjacentNode));
			}
			adjacencyLists.add(newList);
		}
	}
	
	public Set<Integer> getAdjacencyList(int id) {
		return adjacencyLists.get(id);
	}
	
	public void addNode(HashSet<Integer> adjList) {
		adjacencyLists.add(adjList);
	}
	
	public int numberOfNodes() {
		return adjacencyLists.size();
	}
	
	public List<List<Integer>> getCircuitsByLength(int circuitLength) {
		TarjanAlgorithm tarjan = new TarjanAlgorithm(this);
		List<List<Integer>> circuits = tarjan.getCircuitByLength(circuitLength);
		return circuits;
	}
	
	@Override
	public String toString() {
		String res = "";
		for(int i = 0; i < adjacencyLists.size(); ++i) {
			res += i + " : ";
			for(int j = 0; j < adjacencyLists.get(i).size(); ++j) {
				//res += adjacencyLists.get(i).get(j) + ", ";
			}
			res += "\n";
		}
		return res;
	}
}
