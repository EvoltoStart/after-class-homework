package TireTree;

import java.util.HashMap;
import java.util.Map;

public class Trie {
    private TrieNode root;

    private class TrieNode {
        Map<Character, TrieNode> children;
        boolean isWord;
        TrieNode() {
            children = new HashMap<>();
            isWord = false;
        }
    }
    public Trie() {
        root = new TrieNode();
    }
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isWord = true;
    }
    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isWord;
    }
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

        System.out.println(trie.search("apple"));
        System.out.println(trie.search("app"));
        System.out.println(trie.search("banana"));
        System.out.println(trie.search("ban"));
        System.out.println(trie.startsWith("ban"));
        System.out.println(trie.startsWith("app"));
        System.out.println(trie.startsWith("apl"));
    }
}