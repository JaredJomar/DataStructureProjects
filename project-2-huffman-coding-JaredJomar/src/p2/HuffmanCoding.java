package p2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import p2.DataStructures.List.List;
import p2.DataStructures.Map.HashTableSC;
import p2.DataStructures.Map.Map;
import p2.DataStructures.SortedList.SortedLinkedList;
import p2.DataStructures.SortedList.SortedList;
import p2.DataStructures.Tree.BTNode;
import p2.Utils.BinaryTreePrinter;

/**
 * The Huffman Encoding Algorithm
 * 
 * This is a data compression algorithm designed by David A. Huffman and
 * published in 1952
 * 
 * What it does is it takes a string and by constructing a special binary tree
 * with the frequencies of each character.
 * 
 * This tree generates special prefix codes that make the size of each string
 * encoded a lot smaller, thus saving space.
 * 
 * @author Fernando J. Bermudez Medina (Template)
 * @author Jared J Cruz Colon - Student #
 * @version 3.0
 * @since 03/28/2023
 */
public class HuffmanCoding {

	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		/* You can create other test input files and add them to the inputData Folder */
		String data = load_data("input6.txt");

		/* If input string is not empty we can encode the text using our algorithm */
		if (!data.isEmpty()) {
			Map<String, Integer> fD = compute_fd(data);
			BTNode<Integer, String> huffmanRoot = huffman_tree(fD);
			Map<String, String> encodedHuffman = huffman_code(huffmanRoot);
			String output = encode(encodedHuffman, data);
			process_results(fD, encodedHuffman, data, output);
		} else
			System.out.println("Input Data Is Empty! Try Again with a File that has data inside!");

	}

	/**
	 * Receives a file named in parameter inputFile (including its path), and
	 * returns a single string with the contents.
	 * 
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/**
			 * We create a new reader that accepts UTF-8 encoding and extract the input
			 * string from the file, and we return it
			 */
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/**
			 * If input file is empty just return an empty string, if not just extract the
			 * data
			 */
			String extracted = in.readLine();
			if (extracted != null)
				line = extracted;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return line;
	}

	/**
	 * Computes the frequency distribution of characters in a given string and
	 * returns it as a map.
	 * 
	 * @param inputString the string to compute the frequency distribution of
	 *                    characters for.
	 * @return a map containing the frequency distribution of characters in the
	 *         input string.
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		HashTableSC<String, Integer> result = new HashTableSC<>();

		// If the input string is null or empty, return an error message.
		if (inputString == null || inputString.isEmpty()) {
			result.put("Error", -1);
			return result;
		}

		// Loop through each character in the input string and compute its frequency
		// distribution.
		for (char c : inputString.toCharArray()) {
			String character;
			if (Character.isWhitespace(c)) {
				// If the character is whitespace, categorize it as "Space", "Tab", "Newline",
				// or "Other".
				if (c == ' ') {
					character = "Space";
				} else if (c == '\t') {
					character = "Tab";
				} else if (c == '\n') {
					character = "Newline";
				} else {
					character = "Other";
				}
			} else if (Character.isLetterOrDigit(c)) {
				// If the character is a letter or digit, use it as the category.
				character = Character.toString(c);
			} else {
				// If the character is not whitespace or a letter/digit, categorize it as
				// "Quote", "Bracket", "Punctuation", or "Other".
				if (c == '\'' || c == '\"') {
					character = "Quote";
				} else if (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}') {
					character = "Bracket";
				} else if (c == ',' || c == '.' || c == ';' || c == ':') {
					character = "Punctuation";
				} else {
					character = "Other";
				}
			}

			// Increment the frequency count for the character in the result map.
			if (result.containsKey(character)) {
				result.put(character, result.get(character) + 1);
			} else {
				result.put(character, 1);
			}
		}

		// Return the frequency distribution map.
		return result;
	}

	/**
	 * This method builds a Huffman Tree from the given frequency distribution map,
	 * which maps symbols to their frequency in a dataset. The method returns the
	 * root node of the resulting tree.
	 * 
	 * @param frequencyDistribution A map that contains the frequency of each symbol
	 *                              in the dataset.
	 * @return The root node of the Huffman Tree that represents the given frequency
	 *         distribution map.
	 */
	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> frequencyDistribution) {
		// If the frequency distribution map is null or empty, return null.
		if (frequencyDistribution == null || frequencyDistribution.isEmpty()) {
			return null;
		}

		// Create a sorted linked list of BTNodes, where each node corresponds to a
		// symbol in the frequency distribution map.
		SortedLinkedList<BTNode<Integer, String>> treeList = new SortedLinkedList<>();
		for (String symbol : frequencyDistribution.getKeys()) {
			// Get the frequency of the current symbol. If it's negative, set it to 0.
			int frequency = frequencyDistribution.get(symbol);
			if (frequency < 0) {
				frequency = 0;
			}

			// Create a new BTNode for the current symbol, with the frequency as its key and
			// the symbol as its value.
			BTNode<Integer, String> node = new BTNode<>();
			node.setKey(frequency);
			node.setValue(symbol);

			// Add the new node to the sorted linked list.
			treeList.add(node);
		}

		// If the sorted linked list is empty, return null.
		if (treeList.isEmpty()) {
			return null;
		}

		// Build the Huffman Tree by merging the nodes in the sorted linked list until
		// there is only one node left.
		BTNode<Integer, String> rootNode = null;
		while (!treeList.isEmpty()) {
			// Get the two nodes with the smallest keys (i.e., frequencies) from the sorted
			// linked list.
			BTNode<Integer, String> node1 = treeList.get(0);
			treeList.remove(node1);

			// If there is only one node left, it is the root node of the Huffman Tree.
			if (treeList.isEmpty()) {
				rootNode = node1;
				break;
			}

			BTNode<Integer, String> node2 = treeList.get(0);
			treeList.remove(node2);

			// Create a new parent node for the two nodes with the sum of their keys as its
			// key and the concatenation of their values as its value.
			BTNode<Integer, String> parent = new BTNode<>();
			parent.setLeftChild(node1);
			parent.setRightChild(node2);
			parent.setKey(node1.getKey() + node2.getKey());
			parent.setValue(node1.getValue() + node2.getValue());

			// Set the parent node as the parent of the two child nodes.
			node1.setParent(parent);
			node2.setParent(parent);

			// Add the parent node to the sorted linked list.
			treeList.add(parent);
		}

		// Print the Huffman Tree for debugging purposes.
		BinaryTreePrinter.print(rootNode);

		// Return the root node of the Huffman Tree.
		return rootNode;
	}

	/**
	 * Generates a Huffman code table from the given Huffman tree.
	 * 
	 * @param huffmanRoot The root node of the Huffman tree.
	 * @return A mapping of each symbol to its corresponding Huffman code.
	 */
	public static Map<String, String> huffman_code(BTNode<Integer, String> huffmanRoot) {
		if (huffmanRoot == null) {
			return new HashTableSC<>();
		}

		// Create an empty hash table to store the codes
		HashTableSC<String, String> code = new HashTableSC<>();

		try {
			// Call the helper function to recursively traverse the Huffman tree and
			// generate the codes
			huffman_codeHelper(code, huffmanRoot, "");
		} catch (NullPointerException e) {
			// Handle any null pointer exceptions gracefully
			System.err.println("NullPointerException caught: " + e.getMessage());
		}

		return code;
	}

	/**
	 * Helper function to generate Huffman codes recursively.
	 * 
	 * @param code   The hash table to store the codes.
	 * @param root   The current node being traversed in the Huffman tree.
	 * @param prefix The current prefix for the code.
	 */
	public static void huffman_codeHelper(HashTableSC<String, String> code, BTNode<Integer, String> root,
			String prefix) {
		if (root == null) {
			return;
		}

		// If the node is a leaf, add the symbol and its code to the hash table
		if (root.getLeftChild() == null && root.getRightChild() == null) {
			code.put(root.getValue(), prefix);
			return;
		}

		// Recursively traverse the left and right subtrees, adding "0" to the prefix
		// for the left subtree
		// and "1" for the right subtree
		huffman_codeHelper(code, root.getLeftChild(), prefix + "0");
		huffman_codeHelper(code, root.getRightChild(), prefix + "1");
	}

	/**
	 * Encodes an input string using the given Huffman encoding map.
	 * 
	 * @param encodingMap The Huffman encoding map, mapping symbols to their
	 *                    corresponding codes.
	 * @param inputString The input string to be encoded.
	 * @return The encoded string.
	 * @throws IllegalArgumentException If either parameter is null.
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) throws IllegalArgumentException {
		// Check for null parameters
		if (encodingMap == null) {
			throw new IllegalArgumentException("Encoding map cannot be null");
		}

		if (inputString == null) {
			throw new IllegalArgumentException("Input string cannot be null");
		}

		// If the input string is empty, return an empty string
		if (inputString.isEmpty()) {
			return "";
		}

		// Encode each character in the input string using the encoding map
		StringBuilder result = new StringBuilder(inputString.length());
		for (int i = 0; i < inputString.length(); i++) {
			String character = String.valueOf(inputString.charAt(i));
			result.append(encodingMap.get(character));
		}

		// Return the encoded string
		return result.toString();
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable,
	 * the input string, and the output string, and prints the results to the screen
	 * (per specifications).
	 * 
	 * Output Includes: symbol, frequency and code. Also includes how many bits has
	 * the original and encoded string, plus how much space was saved using this
	 * encoding algorithm
	 * 
	 * @param fD             Frequency Distribution of all the characters in input
	 *                       string
	 * @param encodedHuffman Prefix Code Map
	 * @param inputData      text string from the input file
	 * @param output         processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData,
			String output) {
		/*
		 * To get the bytes of the input string, we just get the bytes of the original
		 * string with string.getBytes().length
		 */
		int inputBytes = inputData.getBytes().length;

		/**
		 * For the bytes of the encoded one, it's not so easy.
		 * 
		 * Here we have to get the bytes the same way we got the bytes for the original
		 * one but we divide it by 8, because 1 byte = 8 bits and our huffman code is in
		 * bits (0,1), not bytes.
		 * 
		 * This is because we want to calculate how many bytes we saved by counting how
		 * many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage. the number
		 * of encoded bytes divided by the number of original bytes will give us how
		 * much space we "chopped off".
		 * 
		 * So we have to subtract that "chopped off" percentage to the total (which is
		 * 100%) and that's the difference in space required
		 */
		String savings = d.format(100 - (((float) (outputBytes / (float) inputBytes)) * 100));

		/**
		 * Finally we just output our results to the console with a more visual pleasing
		 * version of both our Hash Tables in decreasing order by frequency.
		 * 
		 * Notice that when the output is shown, the characters with the highest
		 * frequency have the lowest amount of bits.
		 * 
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer, String>> sortedList = new SortedLinkedList<BTNode<Integer, String>>();

		/**
		 * To print the table in decreasing order by frequency, we do the same thing we
		 * did when we built the tree.
		 * 
		 * We add each key with it's frequency in a node into a SortedList, this way we
		 * get the frequencies in ascending order
		 */
		for (String key : fD.getKeys()) {
			BTNode<Integer, String> node = new BTNode<Integer, String>(fD.get(key), key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order, we just traverse the list
		 * backwards and start printing the nodes key (character) and value (frequency)
		 * and find the same key in our prefix code "Lookup Table" we made earlier on in
		 * huffman_code().
		 * 
		 * That way we get the table in decreasing order by frequency
		 */
		for (int i = sortedList.size() - 1; i >= 0; i--) {
			BTNode<Integer, String> node = sortedList.get(i);
			System.out.println(node.getValue() + "\t" + node.getKey() + "\t    " + encodedHuffman.get(node.getValue()));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output);
		System.out.println("Decoded String: \n" + decodeHuff(output, encodedHuffman) + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + (int) outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}

	/*************************************************************************************
	 ** ADD ANY AUXILIARY METHOD YOU WISH TO IMPLEMENT TO FACILITATE YOUR SOLUTION
	 * HERE **
	 *************************************************************************************/

	/**
	 * Auxiliary Method that decodes the generated string by the Huffman Coding
	 * Algorithm
	 * 
	 * Used for output Purposes
	 * 
	 * @param output      - Encoded String
	 * @param lookupTable
	 * @return The decoded String, this should be the original input string parsed
	 *         from the input file
	 */
	public static String decodeHuff(String output, Map<String, String> lookupTable) {
		String result = "";
		int start = 0;
		List<String> prefixCodes = lookupTable.getValues();
		List<String> symbols = lookupTable.getKeys();

		/**
		 * Loop through output until a prefix code is found on map and adding the symbol
		 * that the code that represents it to result
		 */
		for (int i = 0; i <= output.length(); i++) {

			String searched = output.substring(start, i);

			int index = prefixCodes.firstIndex(searched);

			if (index >= 0) { // Found it!
				result = result + symbols.get(index);
				start = i;
			}
		}
		return result;
	}
}