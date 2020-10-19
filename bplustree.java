import java.util.*;
import java.io.*;
public class bplustree {
	
	public static class Node {

		//The list of key values pairs in the node.
		private List<Key> keys;

		//The children of this node. Set only for internal Nodes */
		private List<Node> children;

		//The previous element in the linked list. Set only for external Nodes. */
		private Node prev;

		//The next element in the linked list. Set only for external Nodes. */
		private Node next;

		//The parent of this node. NULL for root */
		private Node parent;

		
		 //Instantiates a new node.
		 
		public Node() {
			this.keys = new ArrayList<>();
			this.children = new ArrayList<>();
			this.prev = null;
			this.next = null;
		}

		
		
		 
		public List<Key> getKeys() {
			return keys;
		}

		
		 
		 
		public void setKeys(List<Key> keys) {
			Iterator<Key> iter = keys.iterator();
			while (iter.hasNext()) {
				this.keys.add(iter.next());
			}
		}

		
		
		 
		public List<Node> getChildren() {
			return children;
		}

		/**
		 * Sets the children.
		 *
		 * @param children
		 *            the new children
		 */
		public void setChildren(List<Node> children) {
			this.children = children;
		}

		/**
		 * Gets the prev.
		 *
		 * @return the prev
		 */
		public Node getPrev() {
			return prev;
		}

		
		 //Sets the prev.
		 
		 		 
		public void setPrev(Node prev) {
			this.prev = prev;
		}

		
		 //returns the next node
		 
		public Node getNext() {
			return next;
		}

		
		 
		//Sets the next.
			 
		public void setNext(Node next) {
			this.next = next;
		}

		//Gets the parent and then returns it.
		
		public Node getParent() {
			return parent;
		}

		
		 //Sets the parent.
		 
		public void setParent(Node parent) {
			this.parent = parent;
		}

		@Override
		public String toString() {
			return "Keys =" + keys.toString();
		}

	}

	public static class Key {

		// intializing the key with integer datatype.
		int key;

		
		private List<Double> values = new ArrayList<>();

		
		 // Instantiating a new key along with its value.
		
		public Key(int key, double value) {
			this.key = key;
			if (null == this.values) {
				values = new ArrayList<>();
			}
			this.values.add(value);
		}
		
		/**
		 * Instantiates a new key
		 *
		 * @param key
		 *            the key
		 */
		public Key(int key) {
			this.key = key;
			this.values = new ArrayList<>();
		}

		/**
		 * Gets the key.
		 *
		 * @return the key
		 */
		public int getKey() {
			return key;
		}

		/**
		 * Sets the key.
		 *
		 * @param key
		 *            the new key
		 */
		public void setKey(int key) {
			this.key = key;
		}

		/**
		 * Gets the values.
		 *
		 * @return the values
		 */
		public List<Double> getValues() {
			return values;
		}

		/**
		 * Sets the values.
		 *
		 * @param values
		 *            the new values
		 */
		public void setValues(List<Double> values) {
			this.values = values;
		}

		public String toString() {
			return "Key [key=" + key + ", values=" + values + "]";
		}

	}
	
	public static class BPlusTree {

	//	private static final Double DOUBLE = (Double) null;

		//The degree of the b plus tree.
		private int m;

		
		private Node root;

		//Instantiating a new b plus tree.Making a constructor.
		
		public BPlusTree() {

		}

		//Initializing the b plus tree with order m.
		public void initialize(int order) {
		
			this.m = order;
			this.root = null;
			
		}

		/**
		 * Insert a key and value pair to the B Plus Tree
		 *
		 * @param key
		 *            the key to be inserted
		 * @param value
		 *            the value to be inserted
		 */
		public void insert(int key, double value) {

			//Inserting to an Empty B Plus Tree
			if (null == this.root) {
				
				Node newNode = new Node();
				newNode.getKeys().add(new Key(key, value));
				this.root = newNode;
				//Setting root parent set to null
				this.root.setParent(null);
			}

			//Only one node that is not full
			else if (this.root.getChildren().isEmpty() && this.root.getKeys().size() < (this.m - 1)) {
				// For all insertions until the root gets overfull for the first
				insertionExternalNode(key, value, this.root);
			}

			//Normal insert
			else {
				Node curr = this.root;
				// Since we insert the element only at the external node, we will go the last level
				
				while (!curr.getChildren().isEmpty()) {
					curr = curr.getChildren().get(binarySearchNodes(key, curr.getKeys()));
				}
				insertionExternalNode(key, value, curr);
				if (curr.getKeys().size() == this.m) {
					// If the external node becomes full, we split it
					splitFunctionExtNode(curr, this.m);
				}
			}

		}

		//Insert the key value pair to the external node.
		
		private void insertionExternalNode(int key, double value, Node node) {
			// Performing binary search to find the correct place where the node is to be inserted.
			
			int indexOfKey = binarySearchNodes(key, node.getKeys());
			if (indexOfKey != 0 && node.getKeys().get(indexOfKey - 1).getKey() == key) {
				node.getKeys().get(indexOfKey - 1).getValues().add(value);
			} else {				
				Key newKey = new Key(key, value);
				node.getKeys().add(indexOfKey, newKey);
			}
		}

		// Spliting the external node.
		
		private void splitFunctionExtNode(Node curr, int m) {

			// Spliting across middle element index
			int midElement = m / 2;

			Node middle = new Node();
			Node rightPart = new Node();

			// Set the right part to have middle element and the elements right to
			// the middle element
			rightPart.setKeys(curr.getKeys().subList(midElement, curr.getKeys().size()));
			rightPart.setParent(middle);
			// While making middle as the internal node, we add only the key since
			// internal nodes of bplus tree do not contain values
			middle.getKeys().add(new Key(curr.getKeys().get(midElement).getKey()));
			middle.getChildren().add(rightPart);
			// Curr holds the left part, so update the split node to contain just
			// the left part
			curr.getKeys().subList(midElement, curr.getKeys().size()).clear();

			boolean firstSplit = true;
			// propogate the middle element up the tree and merge with parent of
			// previously overfull node
			splitFunctionIntNode(curr.getParent(), curr, m, middle, firstSplit);

		}

		/**
		 * Split internal node.
		 *
		 * @param curr
		 *            the current Internal Node
		 * @param prev
		 *            the child of the current Internal Node (Previous internal
		 *            node)
		 * @param m
		 *            the degree of the B Plus Tree
		 * @param toBeInserted
		 *            the part split to be inserted to this internal Node
		 * @param firstSplit
		 *            indicates if the split is happening at the first internal node
		 *            from the bottom
		 */
		private void splitFunctionIntNode(Node curr, Node prev, int m, Node toBeInserted, boolean firstSplit) {
			if (null == curr) {
				// if we split the root before, then a new root has to be created
				this.root = toBeInserted;
				// we find where the child has to be inserted by doing a binary
				// search on keys
				int indexForPrev = binarySearchNodes(prev.getKeys().get(0).getKey(), toBeInserted.getKeys());
				prev.setParent(toBeInserted);
				toBeInserted.getChildren().add(indexForPrev, prev);
				if (firstSplit) {
					// update the linked list only for first split (for external
					// node)
					if (indexForPrev == 0) {
						toBeInserted.getChildren().get(0).setNext(toBeInserted.getChildren().get(1));
						toBeInserted.getChildren().get(1).setPrev(toBeInserted.getChildren().get(0));
					} else {
						toBeInserted.getChildren().get(indexForPrev + 1)
								.setPrev(toBeInserted.getChildren().get(indexForPrev));
						toBeInserted.getChildren().get(indexForPrev - 1)
								.setNext(toBeInserted.getChildren().get(indexForPrev));
					}
				}
			} else {
				// merge the internal node with the mid + right of previous split
				mergeFunctionIntNodes(toBeInserted, curr);
				if (curr.getKeys().size() == m) {
					// do a split again if the internal node becomes full
					int midIndex = (int) Math.ceil(m / 2.0) - 1;
					Node middle = new Node();
					Node rightPart = new Node();

					// since internal nodes follow a split like the b tree, right
					// part contains elements right of the mid element, and the
					// middle becomes parent of right part
					rightPart.setKeys(curr.getKeys().subList(midIndex + 1, curr.getKeys().size()));
					rightPart.setParent(middle);

					middle.getKeys().add(curr.getKeys().get(midIndex));
					middle.getChildren().add(rightPart);

					List<Node> childrenOfCurr = curr.getChildren();
					List<Node> childrenOfRight = new ArrayList<>();

					int lastChildOfLeft = childrenOfCurr.size() - 1;

					// update the children that have to be sent to the right part
					// from the split node
					for (int i = childrenOfCurr.size() - 1; i >= 0; i--) {
						List<Key> currKeysList = childrenOfCurr.get(i).getKeys();
						if (middle.getKeys().get(0).getKey() <= currKeysList.get(0).getKey()) {
							childrenOfCurr.get(i).setParent(rightPart);
							childrenOfRight.add(0, childrenOfCurr.get(i));
							lastChildOfLeft--;
						} else {
							break;
						}
					}

					rightPart.setChildren(childrenOfRight);

					// update the overfull node to contain just the left part and
					// update its children
					curr.getChildren().subList(lastChildOfLeft + 1, childrenOfCurr.size()).clear();
					curr.getKeys().subList(midIndex, curr.getKeys().size()).clear();

					// propogate split one level up
					splitFunctionIntNode(curr.getParent(), curr, m, middle, false);
				}
			}
		}

		/**
		 * Merge internal nodes.
		 *
		 * @param mergeFrom
		 *            to part from which we have to merge (middle of the previous
		 *            split node)
		 * @param mergeInto
		 *            the internal node to be merged to
		 */
		private void mergeFunctionIntNodes(Node mergeFrom, Node mergeInto) {
			Key keyToBeInserted = mergeFrom.getKeys().get(0);
			Node childToBeInserted = mergeFrom.getChildren().get(0);
			// Find the index where the key has to be inserted to by doing a binary
			// search
			int indexToBeInsertedAt = binarySearchNodes(keyToBeInserted.getKey(), mergeInto.getKeys());
			int childInsertPos = indexToBeInsertedAt;
			if (keyToBeInserted.getKey() <= childToBeInserted.getKeys().get(0).getKey()) {
				childInsertPos = indexToBeInsertedAt + 1;
			}
			childToBeInserted.setParent(mergeInto);
			mergeInto.getChildren().add(childInsertPos, childToBeInserted);
			mergeInto.getKeys().add(indexToBeInsertedAt, keyToBeInserted);

			// Update Linked List of external nodes
			if (!mergeInto.getChildren().isEmpty() && mergeInto.getChildren().get(0).getChildren().isEmpty()) {

				// If merge is happening at the last element, then only pointer
				// between new node and previously last element
				// needs to be updated
				if (mergeInto.getChildren().size() - 1 != childInsertPos
						&& mergeInto.getChildren().get(childInsertPos + 1).getPrev() == null) {
					mergeInto.getChildren().get(childInsertPos + 1).setPrev(mergeInto.getChildren().get(childInsertPos));
					mergeInto.getChildren().get(childInsertPos).setNext(mergeInto.getChildren().get(childInsertPos + 1));
				}
				// If merge is happening at the last element, then only pointer
				// between new node and previously last element
				// needs to be updated
				else if (0 != childInsertPos && mergeInto.getChildren().get(childInsertPos - 1).getNext() == null) {
					mergeInto.getChildren().get(childInsertPos).setPrev(mergeInto.getChildren().get(childInsertPos - 1));
					mergeInto.getChildren().get(childInsertPos - 1).setNext(mergeInto.getChildren().get(childInsertPos));
				}
				// If merge is happening in between, then the next element and the
				// previous element's prev and next pointers have to be updated
				else {
					mergeInto.getChildren().get(childInsertPos)
							.setNext(mergeInto.getChildren().get(childInsertPos - 1).getNext());
					mergeInto.getChildren().get(childInsertPos).getNext()
							.setPrev(mergeInto.getChildren().get(childInsertPos));
					mergeInto.getChildren().get(childInsertPos - 1).setNext(mergeInto.getChildren().get(childInsertPos));
					mergeInto.getChildren().get(childInsertPos).setPrev(mergeInto.getChildren().get(childInsertPos - 1));
				}
			}

		}

		/**
		 * Helper method - Prints the tree using a level order traversal
		 */
		public void printTree() {
			Queue<Node> queue = new LinkedList<Node>();
			queue.add(this.root);
			queue.add(null);
			Node curr = null;
			int levelNumber = 2;
			System.out.println("Printing level 1");
			while (!queue.isEmpty()) {
				curr = queue.poll();
				if (null == curr) {
					queue.add(null);
					if (queue.peek() == null) {
						break;
					}
					System.out.println("\n" + "Printing level " + levelNumber++);
					continue;
				}

				printNode(curr);

				if (curr.getChildren().isEmpty()) {
					break;
				}
				for (int i = 0; i < curr.getChildren().size(); i++) {
					queue.add(curr.getChildren().get(i));
				}
			}

			curr = curr.getNext();
			while (null != curr) {
				printNode(curr);
				curr = curr.getNext();
			}

		}

		/**
		 * Helper method Prints a node of the tree.
		 *
		 * @param curr
		 *            the node to be printed
		 */
		private void printNode(Node curr) {
			for (int i = 0; i < curr.getKeys().size(); i++) {
				System.out.print(curr.getKeys().get(i).getKey() + ":(");
				String values = "";
				for (int j = 0; j < curr.getKeys().get(i).getValues().size(); j++) {
					values = values + curr.getKeys().get(i).getValues().get(j) + ",";
				}
				System.out.print(values.isEmpty() ? ");" : values.substring(0, values.length() - 1) + ");");
			}
			System.out.print("||");
		}

		/**
		 * Modified Binary search within internal node.
		 *
		 * @param key
		 *            the key to be searched
		 * @param keyList
		 *            the list of keys to be searched
		 * @return the first index of the list at which the key which is greater
		 *         than the input key
		 */
		public int binarySearchNodes(double key, List<Key> keyList) {
			int st = 0;
			int end = keyList.size() - 1;
			int mid;
			int index = -1;
			// Return first index if key is less than the first element
			if (key < keyList.get(st).getKey()) {
				return 0;
			}
			// Return array size + 1 as the new positin of the key if greater than
			// last element
			if (key >= keyList.get(end).getKey()) {
				return keyList.size();
			}
			while (st <= end) {
				mid = (st + end) / 2;
				// Following condition ensures that we find a location s.t. key is
				// smaller than element at that index and is greater than or equal
				// to the element at the previous index. This location is where the
				// key would be inserted
				if (key < keyList.get(mid).getKey() && key >= keyList.get(mid - 1).getKey()) {
					index = mid;
					break;
				} // Following conditions follow normal Binary Search
				else if (key >= keyList.get(mid).getKey()) {
					st = mid + 1;
				} else {
					end = mid - 1;
				}
			}
			return index;
		}
	
			
	       /* public void delete(int key) {
	        	this.insert(key, null);
			
		}*/

		/**
		 * Search values for a key
		 *
		 * @param key
		 *            the key to be searched
		 * @return the list of values for the key
		 */
		public List<Double> searchFunction(int key) {
			List<Double> searchValues = null;

			Node curr = this.root;
			// Traverse to the corresponding external node that would 'should'
			// contain this key
			while (curr.getChildren().size() != 0) {
				curr = curr.getChildren().get(binarySearchNodes(key, curr.getKeys()));
			}
			List<Key> keyList = curr.getKeys();
			// Do a linear search in this node for the key. Set the parameter
			// 'searchValues' only if success
			for (int i = 0; i < keyList.size(); i++) {
				if (key == keyList.get(i).getKey()) {
					searchValues = keyList.get(i).getValues();
				}
				if (key < keyList.get(i).getKey()) {
					break;
				}
			}

			return searchValues;
		}
		
		
	/**
		 * Search for all key values pairs between key1 and key2.
		 *
		 * @param key1
		 *            the starting key
		 * @param key2
		 *            the ending key
		 * @return the list of key value pairs between the two keys
		 */
		
		public List<Key> searchFunction(double key1, double key2) {
			//System.out.println("Searching between keys " + key1 + ", " + key2);
			List<Key> searchKeys = new ArrayList<>();
			Node currNode = this.root;
			// Traverse to the corresponding external node that would 'should'
			// contain starting key (key1)
			while (currNode.getChildren().size() != 0) {
				currNode = currNode.getChildren().get(binarySearchNodes(key1, currNode.getKeys()));
			}
			
			// Start from current node and add keys whose value lies between key1 and key2 with their corresponding pairs
			// Stop if end of list is encountered or if value encountered in list is greater than key2
			
			boolean endSearch = false;
			while (null != currNode && !endSearch) {
				for (int i = 0; i < currNode.getKeys().size(); i++) {
					if (currNode.getKeys().get(i).getKey() >= key1 && currNode.getKeys().get(i).getKey() <= key2)
						searchKeys.add(currNode.getKeys().get(i));
					if (currNode.getKeys().get(i).getKey() > key2) {
						endSearch = true;
					}
				}
				currNode = currNode.getNext();
			}

			return searchKeys;
		}
		
	/*	public List<Double> search(double key1, double key2) {
			//System.out.println("Searching between keys " + key1 + ", " + key2);
			List<Double> searchvalues = new ArrayList<>();
			Node currNode = this.root;
			// Traverse to the corresponding external node that would 'should'
			// contain starting key (key1)
			while (currNode.getChildren().size() != 0) {
				currNode = currNode.getChildren().get(binarySearchWithinInternalNode(key1, currNode.getKeys()));
			}
			
			// Start from current node and add keys whose value lies between key1 and key2 with their corresponding pairs
			// Stop if end of list is encountered or if value encountered in list is greater than key2
			
			boolean endSearch = false;
			while (null != currNode && !endSearch) {
				for (int i = 0; i < currNode.getKeys().size(); i++) {
					if (currNode.getKeys().get(i).getKey() >= key1 && currNode.getKeys().get(i).getKey() <= key2)
						searchvalues=currNode.getKeys().get(i).getValues();
					if (currNode.getKeys().get(i).getKey() > key2) {
						endSearch = true;
					}
				}
				currNode = currNode.getNext();
			}

			return searchvalues;
		}*/

	}
		/** The Constant OPERATION_INSERT. */
		private static final String OPERATION_INSERT = "Insert";

		/** The Constant OPERATION_SEARCH. */
		private static final String OPERATION_SEARCH = "Search";
		
		private static final String OPERATION_DELETE = "Delete";

		/** The Constant RESULT_NOT_FOUND. */
		private static final String RESULT_NOT_FOUND = "Null";

		/** The Constant OUTPUT_FILENAME. */
		private static final String OUTPUT_FILENAME = "output_file";

		/** The Constant OUTPUT_FILEEXTENSION. */
		private static final String OUTPUT_FILEEXTENSION = ".txt";

		// Sample inputs: Insert(0.02,Value98) Search(3.55) Search(-3.91,30.96)

		/**
		 * The main method.
		 *
		 * @param args
		 *            the name of the input file
		 */
		public static void main(String args[]) {

			// Read name of input file from command line argument
			String fileName = args[0];
			File inputFile = new File(fileName);
			try {
				Scanner sc = new Scanner(inputFile);
				// For creating output file
				BufferedWriter bw = openNewFile();

				BPlusTree tree = new BPlusTree();
				String s = null;
				s = sc.nextLine();
				s= s.substring(s.indexOf("(")+1,s.indexOf(")"));
				int m = Integer.parseInt(s.trim());
				tree.initialize(m);

				while (sc.hasNextLine()) {
					String newLine = sc.nextLine();
					// splitting input file line based on regex
					String[] input = newLine.split("\\(|,|\\)");

					switch (input[0]) {
					// for inserting element into B Plus Tree
					case OPERATION_INSERT: {
						tree.insert(Integer.parseInt(input[1]), Double.parseDouble(input[2]));
						break;
					}
					case OPERATION_SEARCH: {
						// for finding all key value pairs between two keys
						if (input.length == 2) {
							List<Double> res = tree.searchFunction(Integer.parseInt(input[1]));
							searchWriteFunctionByKey(res, bw);
						} 
						// for finding all values for a key
						else {
							List<Key> res = tree.searchFunction(Double.parseDouble(input[1]), Double.parseDouble(input[2]));
							searchWriteFunctionByKeys(res, bw);
						}
						break;
					}
					case OPERATION_DELETE: {
						//tree.delete(Integer.parseInt(input[1]));
						break;
					}
					}

				}
				// closing scanner and buffered writer
				sc.close();
				bw.close();
			} catch (FileNotFoundException e) {
				// LOGGER.severe("File is not found");
				System.out.println("Error: File not found with name: " + fileName);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error: Failed to create new file");
				e.printStackTrace();
			} catch (NumberFormatException e) {	
				System.out.println("Enter valid degree");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Open new file to which output has to be written to.
		 *
		 * @return the buffered writer
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		private static BufferedWriter openNewFile() throws IOException {
			// Creating a new file to write output to (output_file.txt)
			File file = new File(OUTPUT_FILENAME + OUTPUT_FILEEXTENSION);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			return bw;
		}

		/**
		 * Write search by key result to the output file.
		 *
		 * @param res
		 *            the list of values to be written
		 * @param bw
		 *            the BufferedWriter object
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		private static void searchWriteFunctionByKey(List<Double> res, BufferedWriter bw) throws IOException {
			String newLine = "";
			if (null == res) {
				// if no values are found for key
				bw.write(RESULT_NOT_FOUND);
			} else {
				// if values are found, write to file in given format
				Iterator<Double> valueIterator = res.iterator();
				while (valueIterator.hasNext()) {
					newLine = newLine + valueIterator.next() + ", ";
				}
				bw.write(newLine.substring(0, newLine.length() - 2));
			}
			bw.newLine();

		}

		/**
		 * Write search by keys result to the output file.
		 *
		 * @param res
		 *            the list of key value pairs to be written
		 * @param bw
		 *            the BufferedWriter object
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		private static void searchWriteFunctionByKeys(List<Key> res, BufferedWriter bw) throws IOException {
			String newLine = "";
			if (res.isEmpty()) {
				// if no values are found between given keys
				bw.write(RESULT_NOT_FOUND);
			} else {
				// if pairs are found, write to file in given format
				Iterator<Key> keyIterator = res.iterator();
				Iterator<Double> valueIterator;
				Key key;
				while (keyIterator.hasNext()) {
					key = keyIterator.next();
					valueIterator = key.getValues().iterator();
					while (valueIterator.hasNext()) {
						newLine = newLine + "(" + valueIterator.next() + "), ";
					}
				}
				bw.write(newLine.substring(0, newLine.length() - 2));
			}
			bw.newLine();

		}

	}