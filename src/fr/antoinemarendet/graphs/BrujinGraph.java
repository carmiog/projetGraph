package fr.antoinemarendet.graphs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BrujinGraph extends Graph {
	private String input;
	private int k;
	private List<String> subsequences;
	private HashMap<String, List<Integer>> subsequencesPositionsLists;

	public BrujinGraph(String input, int k) {
		super();
		this.input = input;
		this.k = k;
		subsequences = new ArrayList<>();
		buildGraph();
	}

	public String getSequence(int id) {
		return subsequences.get(id);
	}

	public List<TandemRepetition> getTandemRepetitions() {
		List<TandemRepetition> results = new ArrayList<>();
		List<List<Integer>> circuits = this.getCircuitsByLength(k);
		HashSet<Integer> subsequences = new HashSet<>();
		for (List<Integer> c : circuits) {
			subsequences.addAll(c);
		}
		results = getTandemRepetitionsForNodes(subsequences);
		return results;
	}

	private String getSequenceList(HashSet<Integer> list) {
		String res = "";
		for (int i : list) {
			res += this.getSequence(i) + "(" + i + ") ";
		}
		return res;
	}

	private void buildGraph() {
		Map<String, Integer> nodePositions  = new HashMap<>();
		List<HashSet<Integer>> successorLists = new ArrayList<>();
		this.subsequencesPositionsLists = new HashMap<>();
		
		String subseq1, subseq2;
		subseq2 = input.substring(0,k);
		successorLists.add(new HashSet<Integer>());
		nodePositions.put(subseq2, 0);
		
		this.subsequences.add(subseq2);
		this.subsequencesPositionsLists.put(subseq2, new ArrayList<Integer>());
		subsequencesPositionsLists.get(subseq2).add(0);
		
		for(int i=1; i < input.length() - k + 1; ++i){
			subseq1 = subseq2;
			subseq2 = input.substring(i,i+k);
			if(!nodePositions.containsKey(subseq2)){
				this.subsequences.add(subseq2);
				this.subsequencesPositionsLists.put(subseq2, new ArrayList<Integer>());
				
				nodePositions.put(subseq2, successorLists.size());
				successorLists.add(new HashSet<Integer>());
			}
			subsequencesPositionsLists.get(subseq2).add(i);
			successorLists.get(nodePositions.get(subseq1)).add(nodePositions.get(subseq2));
		}
		
		for (HashSet<Integer> n : successorLists) {
			this.addNode(n);
		}
	}

	private List<TandemRepetition> getTandemRepetitionsForNodes(HashSet<Integer> nodes) {
		List<TandemRepetition> tandemRepetitions = new ArrayList<>();
		for (Integer node : nodes) {
			String nodeSubsequence = getSequence(node);
			List<TandemRepetition> nodeTandemRepetitions = getSubsequenceTandemRepetitions(nodeSubsequence);
			tandemRepetitions.addAll(nodeTandemRepetitions);
		}
		return tandemRepetitions;
	}

	private List<TandemRepetition> getSubsequenceTandemRepetitions(String subsequence) {
		List<TandemRepetition> tandemRepetitions = new ArrayList<>();
		List<Integer> sequencePositions = subsequencesPositionsLists.get(subsequence);
		int repetitions = 1;
		int tandemRepetitionStartPosition = sequencePositions.get(0);
		int sequencePosition = 0;
		while (sequencePosition < sequencePositions.size() - 1) {
			if (sequencePositions.get(sequencePosition + 1) - sequencePositions.get(sequencePosition) == k) {
				repetitions++;
			} else if (repetitions > 1) {
				tandemRepetitions.add(new TandemRepetition(tandemRepetitionStartPosition, repetitions, subsequence));
				repetitions = 1;
				tandemRepetitionStartPosition = sequencePositions.get(sequencePosition + 1);

			}
			++sequencePosition;
		}
		if (repetitions > 1) {
			tandemRepetitions.add(new TandemRepetition(tandemRepetitionStartPosition, repetitions, subsequence));
		}
		return tandemRepetitions;
	}

	@Override
	public int numberOfNodes() {
		return subsequences.size();
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < this.numberOfNodes(); ++i) {
			res += getSequence(i) + "(" + i + ") : ";
			HashSet<Integer> adj = this.getAdjacencyList(i);
			res += getSequenceList(adj);
			res += "\n";
		}
		return res;
	}

	public String getGraphvizGraphDescription() {
		String res = "digraph G {\n";
		for (int i = 0; i < numberOfNodes(); ++i) {
			String label = getSequence(i);
			res += "A" + i + " [label=\"" + label + "\"];\n";
		}
		for (int n1 = 0; n1 < numberOfNodes(); ++n1) {
			for (Integer n2 : this.getAdjacencyList(n1)) {
				res += "A" + n1 + " -> A" + n2 + ";\n";
			}
		}
		res += "}";
		return res;
	}

	public static void main(String[] args) {
		String fileName = args[0];

		int k = 0;
		String sequence = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			k = Integer.parseInt(reader.readLine().trim());
			System.out.println("k = " + k);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				sequence += line.trim();
			}
			System.out.println("Sequence = " + sequence);
			reader.close();
		} catch (NumberFormatException e) {
			System.err.println("Wrong format for k.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error while reading input file.");
			e.printStackTrace();
		}
		String graphvizImgFile = fileName.split("\\.")[0] + "_graph.png";
		String graphvizTmpFile = fileName.split("\\.")[0] + "_graph_tmp.graphviz";

		BrujinGraph graph = new BrujinGraph(sequence, k);

		try {
			BufferedWriter wr = new BufferedWriter(new FileWriter(graphvizTmpFile));
			wr.write(graph.getGraphvizGraphDescription());
			wr.flush();
			wr.close();
			Runtime.getRuntime().exec(
					"circo -Tpng -o" + graphvizImgFile + " " + graphvizTmpFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph.toString());
		System.out.println(graph.getTandemRepetitions());
	}
}
