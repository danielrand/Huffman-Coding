import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

// Daniel Rand Project 5 CS323
// 3/17/2019

public class Huffman_Encode_Decode {

	public static void main(String args[]) {
		int array [] [] = new int [10] [10];
		if (args.length != 5) {
			System.out.println("ERROR: Illegal arguments, please enter 5 text file arguments: \n\n1) Probability Table"
					+ "\n2) outFile1\n3) outFile2\n4) outFile3\n5) outFile4\n\nPlease run again accordingly.");
			System.exit(0);
		}
		try {
			Scanner inFile = new Scanner(new FileReader(args[0]));
			PrintWriter outFile1 = new PrintWriter(new BufferedWriter(new FileWriter(args[1])), true);
			PrintWriter outFile2 = new PrintWriter(new BufferedWriter(new FileWriter(args[2])), true);
			PrintWriter outFile3 = new PrintWriter(new BufferedWriter(new FileWriter(args[3])), true);
			PrintWriter outFile4 = new PrintWriter(new BufferedWriter(new FileWriter(args[4])), true);
			HuffmanBinaryTree huff = new HuffmanBinaryTree();
			LinkedList huffList = huff.constructHuffmanLList(inFile, outFile1);
			huff.constructHuffmanBinTree(huffList, outFile1);
			Encoder encoder = new Encoder();
			encoder.constructCode(huff.getRoot(), "");
			outFile2.println("Pre Order:");
			huff.preOrderTraversal(huff.getRoot(), outFile2);
			outFile3.println("In Order:");
			huff.inOrderTraversal(huff.getRoot(), outFile3);
			outFile4.println("Post Order:");
			huff.postOrderTraversal(huff.getRoot(), outFile4);
			userInterface(encoder, huff);
			inFile.close();
			outFile1.close();
			outFile2.close();
			outFile3.close();
			outFile4.close();
		} catch (FileNotFoundException e) {
			System.out.println("One or more input files not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void userInterface (Encoder encoder, HuffmanBinaryTree tree) {
		String nameOrg, nameCompress, nameDecompress;
		char yesNo;
		String nother = "";
		Scanner input = new Scanner (System.in);
		while (true) {
		yesNo = ' ';
		while (yesNo != 'Y') {
			System.out.print("\nWould you like to encode a" + nother + " file? (Y/N): ");
			yesNo = input.nextLine().charAt(0);
			if (yesNo == 'N') {
				System.out.println("\nThank you, terminating program...");
				input.close();
				System.exit(0);
			}
			if (yesNo != 'Y' && yesNo != 'y')
				System.out.println("Please enter Y or N!");
		}
		nother = "nother"; 
		System.out.print("\nEnter the name of the file (if in current directory) or full path of the file you'd like to compress: ");
		nameOrg = input.nextLine();
		if (nameOrg.contains(".txt")) 
			nameOrg = nameOrg.replace(".txt", "");
		nameCompress = nameOrg + "_Compressed.txt";
		nameDecompress = nameOrg + "_DeCompressed.txt";
		try {
			Scanner orgFile = new Scanner (new FileReader(nameOrg+=".txt"));
			PrintWriter compFile = new PrintWriter(new BufferedWriter(new FileWriter(nameCompress)), true);
			PrintWriter deCompFile = new PrintWriter(new BufferedWriter(new FileWriter(nameDecompress)), true);
			System.out.println("\nEncoding " + nameOrg + "...");
			encoder.encode(orgFile, compFile);
			compFile.close();
			System.out.println("Encoded bits have been written to " + nameCompress);
			Scanner compressed = new Scanner (new FileReader (nameCompress));
			Decoder decoder = new Decoder(tree);
			System.out.println("\nDecoding " + nameCompress + "...");
			decoder.decode(compressed, deCompFile);
			System.out.println("Decoded, reconstructed file has been written to " + nameDecompress);
			orgFile.close();
			compressed.close();
			deCompFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("\nERROR: File not found. \nIf entering just file name, please make sure it is in the same directory."
					+ "\nIf entering file path, please make sure the path is correct.");
			userInterface(encoder,tree);
		} catch (IOException e) {
			System.out.println("IO EXCEPTION!!");
		}
		}
	}
}

class TreeNode {

	String chStr, code;
	TreeNode next, left, right;
	int prob;

	public String getChStr() {
		return chStr;
	}
	
	public void setChStr(String str) {
		chStr = str;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TreeNode getNext() {
		return next;
	}

	public void setNext(TreeNode next) {
		this.next = next;
	}

	public TreeNode getLeft() {
		return left;
	}

	public void setLeft(TreeNode left) {
		this.left = left;
	}

	public TreeNode getRight() {
		return right;
	}

	public void setRight(TreeNode right) {
		this.right = right;
	}

	public int getProb() {
		return prob;
	}

	public TreeNode(String chars, int probability) {
		chStr = chars;
		code = null;
		prob = probability;
		next = right = left = null;
	}

	public void printNode(PrintWriter outFile) {
		String output = "";
		output += "Node: chStr=" + chStr + "  prob=" + prob + "  code=" + code + "  NextChStr=";
		output += (next != null ? next.getChStr() : "null");
		output += " LeftChStr=" + (left != null ? left.getChStr() : "null");
		output += " RightChrSt=" + (right != null ? right.getChStr() : "null");
		outFile.println(output);
	}

	public boolean isLeaf() {
		return (getLeft() == null && getRight() == null);
	}

}

class LinkedList {

	TreeNode listHead;

	public LinkedList() {
		listHead = new TreeNode("dummy", 0);
		listHead.setNext(null);
	}

	public TreeNode findSpot(TreeNode input) {
		TreeNode spot = listHead;
		while (spot.getNext() != null) {
			TreeNode next = spot.getNext();
			if (next.getProb() >= input.getProb())
				break;
			spot = next;
		}
		return spot;
	}

	public void insertOneNode(TreeNode newNode) {
		TreeNode spot = findSpot(newNode);
		newNode.setNext(spot.getNext());
		spot.setNext(newNode);
	}

	public void printList(PrintWriter file) {
		TreeNode n = listHead;
		file.print("listHead-->");
		while (true) {
			file.print("(\"" + n.getChStr() + "\", " + n.getProb() + ", ");
			if (n.getNext() == null) {
				file.print("NULL)--> NULL");
				break;
			} else
				file.print("\"" + n.getNext().getChStr() + "\")-->");
			n = n.getNext();
		}
		file.println();
	}

	public TreeNode getListHead() {
		return listHead;
	}

}

class HuffmanBinaryTree {

	TreeNode root;

	public HuffmanBinaryTree() {
		root = null;
	}

	public TreeNode getRoot() {
		return root;
	}

	LinkedList constructHuffmanLList(Scanner input, PrintWriter outFile1) {
		outFile1.println("\nCONSTRUCT HUFFMAN LINKED LIST OUTPUT:\n");
		LinkedList list = new LinkedList();
		while (input.hasNext()) {
			String next = input.next();
			String chr;
			int prob;
			if (next.length() == 1)
				chr = next;
			else {
				System.out.println("File format is wrong");
				break;
			}
			if (chr.equals("@"))
				chr = " ";
			if (input.hasNext())
				prob = input.nextInt();
			else {
				System.out.println("File format is wrong");
				break;
			}
			TreeNode newNode = new TreeNode(chr, prob);
			newNode.setNext(null);
			list.insertOneNode(newNode);
			list.printList(outFile1);
		}
		return list;
	}

	void constructHuffmanBinTree(LinkedList list, PrintWriter outFile1) {
		outFile1.println("\n\nCONSTRUCT HUFFMAN BINARY TREE OUTPUT:\n");
		TreeNode head, newNode;
		do {
			head = list.getListHead();
			TreeNode first = head.getNext();
			TreeNode second = first.getNext();
			String concat = first.getChStr() + second.getChStr();
			int probSum = first.getProb() + second.getProb();
			newNode = new TreeNode(concat, probSum);
			newNode.setLeft(first);
			newNode.setRight(second);
			list.insertOneNode(newNode);
			head.setNext(head.getNext().getNext().getNext());
			list.printList(outFile1);
		} while (!(head.getNext() == newNode && newNode.getNext() == null));
		root = newNode;
	}

	void preOrderTraversal(TreeNode n, PrintWriter outFile) {
		if (n == null)
			return;
		else {
			n.printNode(outFile);
			preOrderTraversal(n.getLeft(), outFile);
			preOrderTraversal(n.getRight(), outFile);
		}
	}

	void inOrderTraversal(TreeNode n, PrintWriter outFile) {
		if (n == null)
			return;
		else {
			inOrderTraversal(n.getLeft(), outFile);
			n.printNode(outFile);
			inOrderTraversal(n.getRight(), outFile);
		}
	}

	void postOrderTraversal(TreeNode n, PrintWriter outFile) {
		if (n == null)
			return;
		else {
			postOrderTraversal(n.getLeft(), outFile);
			postOrderTraversal(n.getRight(), outFile);
			n.printNode(outFile);
		}
	}
}

class Encoder {

	String charCode[];

	public Encoder() {
		charCode = new String[256];
	}

	public void constructCode(TreeNode node, String code) {
		if (node.isLeaf()) {
			node.setCode(code);
			if (node.getChStr().equals("#"))
				node.setChStr("\n");
			int index = (int) node.getChStr().charAt(0);
			charCode[index] = code;
		} else {
			constructCode(node.getLeft(), code + "0");
			constructCode(node.getRight(), code + "1");
		}
	}

	public void encode(Scanner input, PrintWriter outFile) {
		while (input.hasNextLine()) {
			String textLine = input.nextLine();
			for (int i = 0; i < textLine.length(); i++) {
				char charIn = textLine.charAt(i);
				int index = (int) charIn;
				String code = charCode[index];
				outFile.print(code);				
			}
			String newLine = charCode [(int)'\n'];
			outFile.print(newLine);
		}
	}
}

class Decoder {

	HuffmanBinaryTree tree;

	public Decoder(HuffmanBinaryTree tree) {
		this.tree = tree;
	}

	public void decode(Scanner input, PrintWriter outFile) {
		TreeNode spot = tree.getRoot();
		while (input.hasNextLine()) {
			String textLine = input.nextLine();
			for (int i = 0; i < textLine.length(); i++) {
				if (spot.isLeaf()) {
					outFile.print(spot.getChStr());
					spot = tree.getRoot();
				}
				char oneBit = textLine.charAt(i);
				if (oneBit == '0')
					spot = spot.getLeft();
				else if (oneBit == '1')
					spot = spot.getRight();
				else {
					outFile.println("Error! The compressed file contains invalid character!”");
					return;
				}
			}
		}
		if (!spot.isLeaf())
			outFile.println("Error: The compressed file is corrupted!");
	}

}
