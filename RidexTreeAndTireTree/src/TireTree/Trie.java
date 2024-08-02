package TireTree;

import java.util.HashMap;
import java.util.Map;

public class Trie {
    private TrieNode root;

    // TrieNode class representing each node in the trie
    private class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;

        TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }

    // Constructor to initialize the root node
    public Trie() {
        root = new TrieNode();
    }

    // Insert a word into the trie
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    // Search for a word in the trie
    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord;
    }

    // Check if any word in the trie starts with a given prefix
    public boolean startsWith(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return true;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("banana");

        System.out.println(trie.search("apple"));  // true
        System.out.println(trie.search("app"));    // true
        System.out.println(trie.search("banana")); // true
        System.out.println(trie.search("ban"));    // false
        System.out.println(trie.startsWith("ban")); // true
        System.out.println(trie.startsWith("app")); // true
        System.out.println(trie.startsWith("apl")); // false
    }
}