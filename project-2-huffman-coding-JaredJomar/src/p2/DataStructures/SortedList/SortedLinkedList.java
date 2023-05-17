package p2.DataStructures.SortedList;

import java.util.NoSuchElementException;

/**
 * Implementation of a Sorted List using a Singly Linked List structure
 * 
 * @author Fernando J. Bermudez - bermed28
 * @author Jared J Cruz Colon - Student #
 * @version 3.0
 * @since 03/28/2023
 * @param <E>
 */
public class SortedLinkedList<E extends Comparable<? super E>> extends AbstractSortedList<E> {

	@SuppressWarnings("unused")
	private static class Node<E> {

		private E value;
		private Node<E> next;

		public Node(E value, Node<E> next) {
			this.value = value;
			this.next = next;
		}

		public Node(E value) {
			this(value, null); // Delegate to other constructor
		}

		public Node() {
			this(null, null); // Delegate to other constructor
		}

		public E getValue() {
			return value;
		}

		public void setValue(E value) {
			this.value = value;
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		public void clear() {
			value = null;
			next = null;
		}
	} // End of Node class

	private Node<E> head; // First DATA node (This is NOT a dummy header node)

	public SortedLinkedList() {
		head = null;
		currentSize = 0;
	}

	/**
	 * Adds a new element to the list in sorted order.
	 * 
	 * @param e the element to be added
	 */
	@Override
	public void add(E e) {
		/* TODO ADD CODE HERE */
		/* Special case: Be careful when the new value is the smallest */
		Node<E> newNode = new Node<>(e);

		// Case 1: List is empty
		if (head == null) {
			head = newNode;
			currentSize++;
			return;
		}

		// Case 2: New element is smaller than the head
		if (e.compareTo(head.getValue()) < 0) {
			newNode.setNext(head);
			head = newNode;
			currentSize++;
			return;
		}

		// Case 3: New element is equal to the head
		if (e.compareTo(head.getValue()) == 0) {
			return; // do nothing, already in list
		}

		// Case 4: New element is larger than the last element
		if (e.compareTo(getLast().getValue()) > 0) {
			getLast().setNext(newNode);
			currentSize++;
			return;
		}

		// Case 5: New element is equal to some element in the middle
		Node<E> temp = head;
		while (temp.getNext() != null) {
			if (e.compareTo(temp.getNext().getValue()) == 0) {
				return; // do nothing, already in list
			} else if (e.compareTo(temp.getNext().getValue()) < 0) {
				newNode.setNext(temp.getNext());
				temp.setNext(newNode);
				currentSize++;
				return;
			}
			temp = temp.getNext();
		}

		// Edge case: List has only one element and new element is equal to it
		if (e.compareTo(head.getValue()) == 0) {
			return; // do nothing, already in list
		}

		// Edge case: List has only one element and new element is greater than it
		if (e.compareTo(head.getValue()) > 0) {
			head.setNext(newNode);
			currentSize++;
			return;
		}

		// Edge case: List has only one element and new element is smaller than it
		newNode.setNext(head);
		head = newNode;
		currentSize++;
	}

	// Helper method to get the last node in the list
	private Node<E> getLast() {
		Node<E> temp = head;
		while (temp.getNext() != null) {
			temp = temp.getNext();
		}
		return temp;
	}

	/**
	 * Removes the first occurrence of the specified element from this list, if it
	 * is present.
	 * 
	 * @param e the element to be removed from this list, if present
	 * @return {@code true} if this list contained the specified element, otherwise
	 *         {@code false}
	 * @throws NoSuchElementException if this list is empty
	 */
	@Override
	public boolean remove(E e) {
		/* TODO ADD CODE HERE */
		/* Special case: Be careful when the value is found at the head node */
		Node<E> curNode = head;
		Node<E> prevNode = null;

		// Case 1: List is empty
		if (head == null) {
			throw new NoSuchElementException();
		}

		// Case 2: Value is at the head
		if (e.compareTo(head.getValue()) == 0) {
			head = head.getNext();
			curNode.clear();
			currentSize--;
			return true;
		}

		// Case 3: Value is in the middle or tail
		while (curNode != null) {
			if (e.compareTo(curNode.getValue()) == 0) {
				if (prevNode != null) {
					prevNode.setNext(curNode.getNext());
				}
				curNode.clear();
				currentSize--;
				return true;
			}
			prevNode = curNode;
			curNode = curNode.getNext();
		}

		// Case 4: Value is not in the list
		return false;
	}

	/**
	 * Removes and returns the element at the specified index in this list.
	 * 
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws NoSuchElementException    if the list is empty
	 * @throws IndexOutOfBoundsException if the index is negative or greater than or
	 *                                   equal to the size of the list
	 */
	@Override
	public E removeIndex(int index) {
		/* TODO ADD CODE HERE */
		/* Special case: Be careful when index = 0 */
		// Case 1: List is empty
		if (head == null) {
			throw new NoSuchElementException("List is empty");
		}

		// Case 2: Index is negative
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index is negative");
		}

		// Case 3: Index is out of bounds
		if (index >= size()) {
			throw new IndexOutOfBoundsException("Index is out of bounds");
		}

		// Case 4: Index is at the head
		if (index == 0) {
			E value = head.getValue();
			head = head.getNext();
			currentSize--;
			return value;
		}

		// Case 5: Index is at the tail
		if (index == size() - 1) {
			Node<E> curNode = head;
			while (curNode.getNext().getNext() != null) {
				curNode = curNode.getNext();
			}
			E value = curNode.getNext().getValue();
			curNode.setNext(null);
			currentSize--;
			return value;
		}

		// Case 6: Index is in the middle
		Node<E> curNode = head;
		for (int i = 0; i < index - 1; i++) {
			curNode = curNode.getNext();
		}
		E value = curNode.getNext().getValue();
		curNode.setNext(curNode.getNext().getNext());
		currentSize--;
		return value;
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this
	 * list, or -1 if this list does not contain the element.
	 * 
	 * @param e the element to search for
	 * @return the index of the first occurrence of the specified element in this
	 *         list, or -1 if this list does not contain the element
	 * @throws NoSuchElementException if the list is empty
	 */
	@Override
	public int firstIndex(E e) {
		/* TODO ADD CODE HERE */
		int target = -1;
		int index = 0;

		// Case 1: List is empty
		if (head == null) {
			throw new NoSuchElementException("List is empty");
		}

		// Case 2: Target element is null
		if (e == null) {
			for (Node<E> curNode = this.head; index < size() && curNode != null; curNode = curNode.getNext(), index++) {
				if (curNode.getValue() == null) {
					target = index;
					break;
				}
			}
			return target;
		}

		// Case 3: Target element not in the list
		for (Node<E> curNode = this.head; index < size() && curNode != null; curNode = curNode.getNext(), index++) {
			if (curNode.getValue().equals(e)) {
				target = index;
				break;
			}
		}
		return target;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index the index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 ||
	 *                                   index >= size())
	 * @throws NoSuchElementException    if the list is empty
	 */
	@Override
	public E get(int index) {
		/* TODO ADD CODE HERE */
		// Case 1: List is empty
		if (head == null) {
			throw new NoSuchElementException("List is empty");
		}

		// Case 2: Index is negative or greater than or equal to the size of the list
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index is out of bounds");
		}

		// Case 3: Index is equal to zero or the size of the list minus one
		if (index == 0) {
			return head.getValue();
		} else if (index == size() - 1) {
			Node<E> curNode = head;
			while (curNode.getNext() != null) {
				curNode = curNode.getNext();
			}
			return curNode.getValue();
		}

		// Case 4: Index is in the middle
		Node<E> curNode = head.getNext();
		for (int i = 1; i < index; i++) {
			curNode = curNode.getNext();
		}
		return curNode.getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Comparable<E>[] toArray() {
		int index = 0;
		Comparable[] theArray = new Comparable[size()]; // Cannot use Object here
		for (Node<E> curNode = this.head; index < size() && curNode != null; curNode = curNode.getNext(), index++) {
			theArray[index] = curNode.getValue();
		}
		return theArray;
	}

}
