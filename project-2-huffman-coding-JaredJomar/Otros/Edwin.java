public class HuffmanCoding {

	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		/* You can create other test input files and add them to the inputData Folder */
		String data = load_data("input1.txt");

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
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 * 
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/**
			 * We create a new reader that accepts UTF-8 encoding and
			 * extract the input string from the file, and we return it
			 */
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/**
			 * If input file is empty just return an
			 * empty string, if not just extract the data
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
	 * 
	 * 
	 * @param inputString
	 * @return
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		HashTableSC<String, Integer> result = new HashTableSC<>();

		if (inputString == null || inputString.isEmpty()) {
			result.put("Error", -1);
			return result;
		}

		for (char c : inputString.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				String character = Character.toString(c);
				if (result.containsKey(character)) {
					result.put(character, result.get(character) + 1);
				} else {
					result.put(character, 1);
				}
			}
		}

		return result;
	}

	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> frequencyDistribution) {
		if (frequencyDistribution == null || frequencyDistribution.isEmpty()) {
			return null;
		}

		SortedLinkedList<BTNode<Integer, String>> treeList = new SortedLinkedList<>();
		for (String symbol : frequencyDistribution.getKeys()) {
			int frequency = frequencyDistribution.get(symbol);
			if (frequency < 0) {
				frequency = 0;
			}

			BTNode<Integer, String> node = new BTNode<>();
			node.setKey(frequency);
			node.setValue(symbol);

			treeList.add(node);
		}

		return buildHuffmanTree(treeList);
	}

	private static BTNode<Integer, String> buildHuffmanTree(SortedLinkedList<BTNode<Integer, String>> treeList) {
		if (treeList.size() == 1) {
			return treeList.get(0);
		}

		BTNode<Integer, String> node1 = treeList.get(0);
		treeList.remove(node1);
		BTNode<Integer, String> node2 = treeList.get(0);
		treeList.remove(node2);

		BTNode<Integer, String> parent = new BTNode<>();
		parent.setLeftChild(node1);
		parent.setRightChild(node2);
		parent.setKey(node1.getKey() + node2.getKey());
		parent.setValue(node1.getValue() + node2.getValue());

		node1.setParent(parent);
		node2.setParent(parent);

		treeList.add(parent);

		return buildHuffmanTree(treeList);
	}

	public static Map<String, String> huffman_code(BTNode<Integer, String> huffmanRoot) {
		Map<String, String> code = new HashTableSC<>();

		if (huffmanRoot == null) {
			return code;
		}

		huffman_codeHelper(code, huffmanRoot, "");
		return code;
	}

	private static void huffman_codeHelper(Map<String, String> code, BTNode<Integer, String> node, String prefix) {
		if (node.getLeftChild() == null && node.getRightChild() == null) {
			code.put(node.getValue(), prefix);
			return;
		}

		huffman_codeHelper(code, node.getLeftChild(), prefix + "0");
		huffman_codeHelper(code, node.getRightChild(), prefix + "1");
	}

	/**
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) throws IllegalArgumentException {

		if (encodingMap == null) {
			throw new IllegalArgumentException("Encoding map cannot be null");
		}

		if (inputString == null) {
			throw new IllegalArgumentException("Input string cannot be null");
		}

		if (inputString.isEmpty()) {
			return "";
		}

		return encodeHelper(encodingMap, inputString, 0, new StringBuilder());
	}

	public static String encodeHelper(Map<String, String> encodingMap, String inputString, int index,
			StringBuilder result) {

		if (index == inputString.length()) {
			return result.toString();
		}

		String character = String.valueOf(inputString.charAt(index));
		result.append(encodingMap.get(character));

		return encodeHelper(encodingMap, inputString, index + 1, result);
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable,
	 * the input string,
	 * and the output string, and prints the results to the screen (per
	 * specifications).
	 * 
	 * Output Includes: symbol, frequency and code.
	 * Also includes how many bits has the original and encoded string, plus how
	 * much space was saved using this encoding algorithm
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
		 * Here we have to get the bytes the same way we got the bytes
		 * for the original one but we divide it by 8, because
		 * 1 byte = 8 bits and our huffman code is in bits (0,1), not bytes.
		 * 
		 * This is because we want to calculate how many bytes we saved by
		 * counting how many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes
		 * will give us how much space we "chopped off".
		 * 
		 * So we have to subtract that "chopped off" percentage to the total (which is
		 * 100%)
		 * and that's the difference in space required
		 */
		String savings = d.format(100 - (((float) (outputBytes / (float) inputBytes)) * 100));

		/**
		 * Finally we just output our results to the console
		 * with a more visual pleasing version of both our
		 * Hash Tables in decreasing order by frequency.
		 * 
		 * Notice that when the output is shown, the characters
		 * with the highest frequency have the lowest amount of bits.
		 * 
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer, String>> sortedList = new SortedLinkedList<BTNode<Integer, String>>();

		/**
		 * To print the table in decreasing order by frequency,
		 * we do the same thing we did when we built the tree.
		 * 
		 * We add each key with it's frequency in a node into a SortedList,
		 * this way we get the frequencies in ascending order
		 */
		for (String key : fD.getKeys()) {
			BTNode<Integer, String> node = new BTNode<Integer, String>(fD.get(key), key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order,
		 * we just traverse the list backwards and start printing
		 * the nodes key (character) and value (frequency) and find
		 * the same key in our prefix code "Lookup Table" we made
		 * earlier on in huffman_code().
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
		 * Loop through output until a prefix code is found on map and
		 * adding the symbol that the code that represents it to result
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