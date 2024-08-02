package RidexTree;

import java.util.HashMap;
import java.util.Map;

public class RadixTree {
    private Node root;

    public RadixTree() {
        this.root = new Node("");
    }

    // Node class representing each node in the radix tree
    private class Node {
        String key;
        Map<String, Node> children;
        boolean isEndOfWord;

        Node(String key) {
            this.key = key;
            this.children = new HashMap<>();
            this.isEndOfWord = false;
        }
    }

    // Insert a word into the radix tree
    public void insert(String word) {
        Node current = root;

        while (word.length() > 0) {
            String longestPrefix = null;
            Node childNode = null;

            for (String childKey : current.children.keySet()) {
                int prefixLength = getCommonPrefixLength(childKey, word);
                if (prefixLength > 0) {
                    longestPrefix = childKey;
                    childNode = current.children.get(childKey);
                    break;
                }
            }

            if (longestPrefix == null) {
                current.children.put(word, new Node(word));
                current.children.get(word).isEndOfWord = true;
                return;
            }

            int commonPrefixLength = getCommonPrefixLength(longestPrefix, word);
            String commonPrefix = longestPrefix.substring(0, commonPrefixLength);
            String remainingPrefix = longestPrefix.substring(commonPrefixLength);
            String remainingWord = word.substring(commonPrefixLength);

            if (remainingPrefix.length() > 0) {
                Node newChild = new Node(commonPrefix);
                newChild.children.put(remainingPrefix, childNode);
                current.children.remove(longestPrefix);
                current.children.put(commonPrefix, newChild);
                current = newChild;
            } else {
                current = childNode;
            }

            word = remainingWord;
        }

        current.isEndOfWord = true;
    }

    // Search for a word in the radix tree
    public boolean search(String word) {
        Node current = root;

        while (word.length() > 0) {
            String longestPrefix = null;
            Node childNode = null;

            for (String childKey : current.children.keySet()) {
                int prefixLength = getCommonPrefixLength(childKey, word);
                if (prefixLength > 0) {
                    longestPrefix = childKey;
                    childNode = current.children.get(childKey);
                    break;
                }
            }

            if (longestPrefix == null) {
                return false;
            }

            int commonPrefixLength = getCommonPrefixLength(longestPrefix, word);
            String commonPrefix = longestPrefix.substring(0, commonPrefixLength);
            String remainingWord = word.substring(commonPrefixLength);

            if (commonPrefix.equals(longestPrefix)) {
                current = childNode;
                word = remainingWord;
            } else {
                return false;
            }
        }

        return current.isEndOfWord;
    }

    // Get the length of the common prefix between two strings
    private int getCommonPrefixLength(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());
        int i;
        for (i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        return i;
    }

    public static void main(String[] args) {
        RadixTree radixTree = new RadixTree();
        radixTree.insert("apple");
        radixTree.insert("app");
        radixTree.insert("banana");

        System.out.println(radixTree.search("apple"));  // true
        System.out.println(radixTree.search("app"));    // true
        System.out.println(radixTree.search("banana")); // true
        System.out.println(radixTree.search("ban"));    // false
    }
}
