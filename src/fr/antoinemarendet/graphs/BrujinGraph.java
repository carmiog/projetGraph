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

	private String getSequenceList(List<Integer> list) {
		String res = "";
		for (int i : list) {
			res += this.getSequence(i) + "(" + i + ") ";
		}
		return res;
	}

	private void buildGraph() {
		buildSubsequencesAndStorePositions();
		buildNodes();
	}

	private void buildNodes() {
		List<List<Integer>> adjacencyLists = new ArrayList<>();
		for (int i = 0; i < this.numberOfNodes(); ++i) {
			adjacencyLists.add(new ArrayList<>());
		}

		for (int nodeId1 = 0; nodeId1 < this.numberOfNodes() - 1; ++nodeId1) {
			String n1 = subsequences.get(nodeId1);
			for (int nodeId2 = nodeId1 + 1; nodeId2 < this.numberOfNodes(); ++nodeId2) {
				String n2 = subsequences.get(nodeId2);
				if (isNodeABrujinSuccessor(n1, n2)) {
					adjacencyLists.get(nodeId1).add(nodeId2);
				}
				if (isNodeABrujinSuccessor(n2, n1)) {
					adjacencyLists.get(nodeId2).add(nodeId1);
				}
			}
		}

		for (List<Integer> n : adjacencyLists) {
			addNode(n);
		}
	}

	private void buildSubsequencesAndStorePositions() {
		this.subsequencesPositionsLists = new HashMap<>();
		for (int charIndex = 0; charIndex < input.length() - k + 1; ++charIndex) {
			String subsequence = input.substring(charIndex, charIndex + k);
			if (!subsequences.contains(subsequence)) {
				subsequences.add(subsequence);
				subsequencesPositionsLists.put(subsequence, new ArrayList<>());
			}
			subsequencesPositionsLists.get(subsequence).add(charIndex);
		}
	}

	private boolean isNodeABrujinSuccessor(String n1, String n2) {
		return n1.substring(1, k).equals(n2.substring(0, k - 1)) && this.input.contains(n1 + n2.charAt(k - 1));
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
			List<Integer> adj = this.getAdjacencyList(i);
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
