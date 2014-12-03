package fr.antoinemarendet.graphs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		HashSet<Integer> subsequences = new HashSet<>();
		List<List<Integer>> circuits = this.getCircuits();
		for (List<Integer> c : circuits) {
			subsequences.addAll(c);
			results.addAll(getTandemRepetitionsForNodesInCirc(subsequences));
			subsequences.clear();
		}
		return results;
	}

	private String getSequenceList(Set<Integer> adj) {
		String res = "";
		for (int i : adj) {
			res += this.getSequence(i) + "(" + i + ") ";
		}
		return res;
	}

	private void buildGraph() {
		Map<String, Integer> nodePositions = new HashMap<>();
		List<HashSet<Integer>> successorLists = new ArrayList<>();
		this.subsequencesPositionsLists = new HashMap<>();

		String subseq1, subseq2;
		subseq2 = input.substring(0, k);
		successorLists.add(new HashSet<Integer>());
		nodePositions.put(subseq2, 0);

		this.subsequences.add(subseq2);
		this.subsequencesPositionsLists.put(subseq2, new ArrayList<Integer>());
		subsequencesPositionsLists.get(subseq2).add(0);

		for (int i = 1; i < input.length() - k + 1; ++i) {
			subseq1 = subseq2;
			subseq2 = input.substring(i, i + k);
			if (!nodePositions.containsKey(subseq2)) {
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
	
	@Deprecated
	private List<TandemRepetition> getTandemRepetitionsForNodes(Set<Integer> nodes) {
		List<TandemRepetition> tandemRepetitions = new ArrayList<>();
		for (Integer node : nodes) {
			String nodeSubsequence = getSequence(node);
			List<TandemRepetition> nodeTandemRepetitions = getSubsequenceTandemRepetitions(nodeSubsequence);
			tandemRepetitions.addAll(nodeTandemRepetitions);
		}

		return tandemRepetitions;
	}

	private List<TandemRepetition> getTandemRepetitionsForNodesInCirc(Set<Integer> nodes) {
		List<TandemRepetition> tandemRepetitions = new ArrayList<>();
		System.out.println(nodes);
		for (Integer node : nodes) {
			String nodeSubsequence = getSequence(node);
			if (nodes.size() >= k) {
				List<TandemRepetition> nodeTandemRepetitions = getSubsequenceTandemRepetitionsByLength(nodeSubsequence,
						nodes.size());
				tandemRepetitions.addAll(nodeTandemRepetitions);
			} else {
				// Comb cycles avec noeuds
			}
		}

		return tandemRepetitions;
	}

	private List<TandemRepetition> getSubsequenceTandemRepetitionsByLength(String subsequence, int cSize) {
		List<TandemRepetition> tandemRepetitions = new ArrayList<>();
		List<Integer> sequencePositions = subsequencesPositionsLists.get(subsequence);
		int repetitions = 1;
		int tandemRepetitionStartPosition = sequencePositions.get(0);
		int listIndex = 0;
		int seqPos1 = sequencePositions.get(listIndex);
		int seqPos2 = sequencePositions.get(listIndex);
		while (listIndex < sequencePositions.size() - 1) {
			seqPos1 = sequencePositions.get(listIndex);
			seqPos2 = sequencePositions.get(listIndex + 1);
			// Si on sort de la séquence, break
			if (seqPos1 + cSize * 2 > input.length() - 1) {
				break;
			}
			String seq1 = input.substring(seqPos1 + k, seqPos1 + cSize);
			String seq2 = input.substring(seqPos1 + cSize + k, seqPos1 + cSize * 2);
			if (seqPos2 - seqPos1 == cSize && (cSize == k || seq1.equals(seq2))) {
				repetitions++;
			} else if (repetitions > 1) {
				tandemRepetitions.add(new TandemRepetition(tandemRepetitionStartPosition, repetitions, seq1));
				repetitions = 1;
				tandemRepetitionStartPosition = sequencePositions.get(listIndex + 1);

			}
			++listIndex;
		}
		if (repetitions > 1) {
			tandemRepetitions.add(new TandemRepetition(tandemRepetitionStartPosition, repetitions, input.substring(
					seqPos1, seqPos1 + cSize)));
		}
		return tandemRepetitions;
	}

	@Deprecated
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
			Set<Integer> adj = this.getAdjacencyList(i);
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
		String[] fileSplit = fileName.split("/");
		String fileNameWithoutExt = fileSplit[fileSplit.length - 1].split("\\.")[0];
		String graphvizImgFile = "res/" + fileNameWithoutExt + "_graph.png";
		String graphvizTmpFile = "res/" + fileNameWithoutExt + "_graph.tmp";
		String resFile = "res/" + fileNameWithoutExt + "_res.txt";
		BrujinGraph graph = new BrujinGraph(sequence, k);

		try {
			BufferedWriter wr = new BufferedWriter(new FileWriter(graphvizTmpFile));
			wr.write(graph.getGraphvizGraphDescription());
			wr.flush();
			wr.close();
			Runtime.getRuntime().exec("circo -Tpng -o" + graphvizImgFile + " " + graphvizTmpFile);
			File f = new File(graphvizTmpFile);
			// f.delete();
			wr = new BufferedWriter(new FileWriter(resFile));
			wr.write(graph.toString() + "\n");
			wr.write(graph.getTandemRepetitions().toString() + "\n");
			wr.flush();
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(graph.toString());
		System.out.println(graph.getTandemRepetitions());
	}
}
