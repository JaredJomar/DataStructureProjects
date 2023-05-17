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

    // Build the Huffman Tree recursively by merging the nodes in the sorted linked list until
    // there is only one node left.
    return buildHuffmanTree(treeList);
}

private static BTNode<Integer, String> buildHuffmanTree(SortedLinkedList<BTNode<Integer, String>> treeList) {
    // If there is only one node left, it is the root node of the Huffman Tree.
    if (treeList.size() == 1) {
        return treeList.get(0);
    }

    // Get the two nodes with the smallest keys (i.e., frequencies) from the sorted linked list.
    BTNode<Integer, String> node1 = treeList.get(0);
    treeList.remove(node1);

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

    // Recursively build the Huffman Tree.
    return buildHuffmanTree(treeList);
}
