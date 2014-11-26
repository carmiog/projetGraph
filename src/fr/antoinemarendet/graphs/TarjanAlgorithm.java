package fr.antoinemarendet.graphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TarjanAlgorithm {
	private Graph graph;
	private int n;
	private Stack<Integer> pointStack;
	private Stack<Integer> markedStack;
	private boolean mark[];
	private int s;

	private List<List<Integer>> circuits;

	public TarjanAlgorithm(Graph g) {
		this.graph = new Graph(g);
		this.n = graph.numberOfNodes();
		this.mark = new boolean[n];
	}

	private void compute() {
		pointStack = new Stack<Integer>();
		markedStack = new Stack<Integer>();
		circuits = new ArrayList<List<Integer>>();
		tarjan();
	}

	public List<List<Integer>> getCircuits() {
		if(circuits == null) {
			compute();
		}
		return circuits;
	}
	
	public List<List<Integer>> getCircuitByLength(int length) {
		if(circuits == null) {
			compute();
		}
		List<List<Integer>> results = new ArrayList<>();
		for(List<Integer> cycle : circuits) {
			if(cycle.size() == length) {
				results.add(cycle);
			}
		}
		return results;
	}

	private boolean backtrack(int v) {
		boolean f = false;
		boolean g;
		pointStack.push(v);
		mark[v] = true;
		markedStack.push(v);
		for (int i = 0; i < graph.getAdjacencyList(v).size(); ++i) {
			int w = graph.getAdjacencyList(v).get(i);
			if (w < s) {
				graph.getAdjacencyList(v).remove(i);
			} else if (w == s) {
				List<Integer> newCircuit = new ArrayList<>();
				for (int b : pointStack) {
					newCircuit.add(b);
				}
				circuits.add(newCircuit);
				f = true;
			} else if (!mark[w]) {
				g = backtrack(w);
				f = f || g;
			}
		}
		if (f) {
			while (markedStack.peek() != v) {
				int u = markedStack.pop();
				mark[u] = false;
			}
			mark[markedStack.pop()] = false;
		}
		pointStack.pop();
		return f;
	}

	private void tarjan() {
		for (int i = 0; i < n; ++i) {
			mark[i] = false;
		}
		for (int s = 0; s < n; ++s) {
			this.s = s;
			backtrack(s);
			while (!markedStack.isEmpty()) {
				mark[markedStack.pop()] = false;
			}
		}
	}

}
