package RidexTree;

import java.util.HashMap;
import java.util.Map;

public class RadixTree {
    private Node root;

    public RadixTree() {
        this.root = new Node("");
    }

    private class Node {
        String key;
        Map<String, Node> children;
        boolean isWord;

        Node(String key) {
            this.key = key;
            this.children = new HashMap<>();
            this.isWord = false;
        }
    }


    public void insert(String word) {
        Node current = root;

        while (word.length() > 0) {
            String l = null;
            Node childNode = null;

            for (String childKey : current.children.keySet()) {
                int plen = getLength(childKey, word);
                if (plen > 0) {
                    l = childKey;
                    childNode = current.children.get(childKey);
                    break;
                }
            }

            if (l == null) {
                current.children.put(word, new Node(word));
                current.children.get(word).isWord = true;
                return;
            }

            int clen = getLength(l, word);
            String common = l.substring(0, clen);
            String remainingPrefix = l.substring(clen);
            String remainingWord = word.substring(clen);

            if (remainingPrefix.length() > 0) {
                Node newChild = new Node(common);
                newChild.children.put(remainingPrefix, childNode);
                current.children.remove(l);
                current.children.put(common, newChild);
                current = newChild;
            } else {
                current = childNode;
            }

            word = remainingWord;
        }

        current.isWord = true;
    }


    public boolean search(String word) {
        Node current = root;

        while (word.length() > 0) {
            String l = null;
            Node childNode = null;

            for (String childKey : current.children.keySet()) {
                int prefixLength = getLength(childKey, word);
                if (prefixLength > 0) {
                    l = childKey;
                    childNode = current.children.get(childKey);
                    break;
                }
            }

            if (l == null) {
                return false;
            }

            int clen = getLength(l, word);
            String commonPrefix = l.substring(0, clen);
            String remainingWord = word.substring(clen);

            if (commonPrefix.equals(l)) {
                current = childNode;
                word = remainingWord;
            } else {
                return false;
            }
        }

        return current.isWord;
    }


    private int getLength(String str1, String str2) {
        int minlen = Math.min(str1.length(), str2.length());
        int i;
        for (i = 0; i < minlen; i++) {
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

        System.out.println(radixTree.search("apple"));
        System.out.println(radixTree.search("app"));
        System.out.println(radixTree.search("banana"));
        System.out.println(radixTree.search("ban"));
    }
}
