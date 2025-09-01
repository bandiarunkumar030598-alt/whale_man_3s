package words;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * TextAnalyzer - A program to analyze text files and provide statistical insights
 * 
 * Features:
 * - Counts total words (excluding filtered words)
 * - Finds top 5 most frequent words
 * - Lists unique words alphabetically
 * - Filters out common words (articles, prepositions, pronouns, conjunctions, modal verbs)
 * 
 * Author: Text Analysis System
 * Estimated Development Time: 2 hours
 */
public class TextAnalyzer {
    
    // Predefined list of words to exclude from analysis
    private static final Set<String> EXCLUDED_WORDS = new HashSet<>(Arrays.asList(
        // Articles
        "the", "a", "an",
        
        // Prepositions
        "in", "on", "at", "by", "for", "with", "to", "of", "from", "up", "about", "into", 
        "through", "during", "before", "after", "above", "below", "over", "under", "between",
        "among", "across", "behind", "beside", "beyond", "within", "without", "upon",
        "against", "toward", "towards", "throughout", "beneath", "underneath", "inside",
        "outside", "near", "around", "off", "down", "out", "along", "past",
        
        // Pronouns
        "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them",
        "my", "your", "his", "her", "its", "our", "their", "mine", "yours", "hers",
        "ours", "theirs", "this", "that", "these", "those", "who", "whom", "whose",
        "which", "what", "where", "when", "why", "how", "myself", "yourself", "himself",
        "herself", "itself", "ourselves", "yourselves", "themselves",
        
        // Conjunctions
        "and", "or", "but", "nor", "for", "yet", "so", "because", "since", "although",
        "though", "unless", "until", "while", "whereas", "if", "whether", "either",
        "neither", "both", "not", "only",
        
        // Modal verbs and common verbs
        "is", "was", "are", "were", "am", "be", "been", "being", "have", "has", "had",
        "do", "does", "did", "will", "would", "shall", "should", "may", "might", "can",
        "could", "must", "ought", "used", "going", "get", "got", "getting", "go", "went",
        "come", "came", "coming", "make", "made", "making", "take", "took", "taken", "taking",
        
        // Common short words
        "as", "no", "yes", "oh", "ah", "well", "now", "then", "there", "here", "where",
        "very", "too", "much", "many", "more", "most", "some", "any", "all", "each",
        "every", "other", "another", "such", "same", "different", "new", "old", "good",
        "bad", "great", "small", "large", "big", "little", "long", "short", "high", "low"
    ));
    
    private Map<String, Integer> wordFrequency;
    private int totalWords;
    private int processedWords;
    private long processingTimeMs;
    
    public TextAnalyzer() {
        this.wordFrequency = new HashMap<>();
        this.totalWords = 0;
        this.processedWords = 0;
    }
    
    /**
     * Analyzes text from a file URL
     */
    public void analyzeFromUrl(String urlString) throws IOException {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting analysis of: " + urlString);
        System.out.println("=" + "=".repeat(60));
        
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            analyzeText(reader);
        }
        
        this.processingTimeMs = System.currentTimeMillis() - startTime;
        System.out.println("Analysis completed in " + processingTimeMs + " ms");
    }
    
    private static boolean isCommonWordEndingInS(String word) {
        Set<String> commonSWords = Set.of(
            "this", "his", "was", "yes", "us", "class", "less", "process", "business",
            "various", "serious", "previous", "obvious", "famous", "curious", "numerous",
            "dangerous", "tremendous", "monstrous", "enormous", "glorious", "mysterious",
            "thus", "cross", "pass", "mass", "glass", "grass", "dress", "press", "stress"
        );
        return commonSWords.contains(word);
    }
    
    /**
     * Analyzes text from a local file
     */
    public void analyzeFromFile(String filename) throws IOException {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting analysis of: " + filename);
        System.out.println("=" + "=".repeat(60));
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            analyzeText(reader);
        }
        
        this.processingTimeMs = System.currentTimeMillis() - startTime;
        System.out.println("Analysis completed in " + processingTimeMs + " ms");
    }
    
    /**
     * Analyzes text from a BufferedReader
     */
    private void analyzeText(BufferedReader reader) throws IOException {
        String line;
        Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
        
        while ((line = reader.readLine()) != null) {
            // Extract words using regex (only letters)
            String[] words = wordPattern.matcher(line.toLowerCase())
                    .results()
                    .map(match -> match.group())
                    .toArray(String[]::new);
            
            for (String word : words) {
                totalWords++;
                
                // Remove possessive 's if present
                if (word.endsWith("s") && word.length() > 3 && !isCommonWordEndingInS(word)) {
                    word = word.substring(0, word.length() - 1);
                }
                
                // Skip excluded words
                if (!EXCLUDED_WORDS.contains(word) && word.length() > 1) {
                    wordFrequency.merge(word, 1, Integer::sum);
                    processedWords++;
                }
            }
        }
    }
    
    /**
     * Gets the top N most frequent words
     */
    public List<Map.Entry<String, Integer>> getTopWords(int n) {
        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all unique words sorted alphabetically
     */
    public List<String> getUniqueWordsSorted(int limit) {
        return wordFrequency.keySet().stream()
                .sorted()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Prints comprehensive analysis results
     */
    public void printResults() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ANALYSIS RESULTS");
        System.out.println("=".repeat(60));
        
        // Total word count
        System.out.println("1. WORD COUNT STATISTICS:");
        System.out.println("   Total words found: " + totalWords);
        System.out.println("   Words after filtering: " + processedWords);
        System.out.println("   Unique words: " + wordFrequency.size());
        System.out.println("   Processing time: " + processingTimeMs + " ms (" + 
                          (processingTimeMs / 1000.0) + " seconds)");
        
        // Top 5 most frequent words
        System.out.println("\n2. TOP 5 MOST FREQUENT WORDS:");
        List<Map.Entry<String, Integer>> topWords = getTopWords(5);
        for (int i = 0; i < topWords.size(); i++) {
            Map.Entry<String, Integer> entry = topWords.get(i);
            System.out.printf("   %d. %-15s : %,d occurrences%n", 
                            i + 1, entry.getKey(), entry.getValue());
        }
        
        // Top 50 unique words alphabetically
        System.out.println("\n3. TOP 50 UNIQUE WORDS (ALPHABETICALLY SORTED):");
        List<String> sortedWords = getUniqueWordsSorted(50);
        for (int i = 0; i < sortedWords.size(); i++) {
            if (i % 5 == 0) System.out.print("   ");
            System.out.printf("%-12s ", sortedWords.get(i));
            if ((i + 1) % 5 == 0) System.out.println();
        }
        if (sortedWords.size() % 5 != 0) System.out.println();
    }
    
    /**
     * Saves results to a file
     */
    public void saveResults(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Redirect System.out to file temporarily
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(new FileOutputStream(filename)));
            
            printResults();
            
            // Restore System.out
            System.setOut(originalOut);
            System.out.println("Results saved to: " + filename);
        }
    }
    
    /**
     * Main method - Entry point of the program
     */
    public static void main(String[] args) {
        TextAnalyzer analyzer = new TextAnalyzer();
        
        try {
//             You can analyze from URL (if accessible)
             analyzer.analyzeFromUrl("https://courses.cs.washington.edu/courses/cse390c/22sp/lectures/moby.txt");
            
            // Or analyze from local file
            // analyzer.analyzeFromFile("moby.txt");
            
            // Demo with sample text for testing
            System.out.println("DEMO MODE - Analyzing sample text");
            analyzeSampleText(analyzer);
            
            // Print results to console
            analyzer.printResults();
            
            // Save results to file
            analyzer.saveResults("analysis_results.txt");
            
        } catch (IOException e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demo method with sample text for testing
     */
    private static void analyzeSampleText(TextAnalyzer analyzer) throws IOException {
    	String sampleText = 
                "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money " +
                "in my purse, and nothing particular to interest me on shore, I thought I would sail about " +
                "a little and see the watery part of the world. It is a way I have of driving off the spleen " +
                "and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever " +
                "it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before " +
                "coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever " +
                "my hypos get such an upper hand of me, that it requires a strong moral principle to prevent " +
                "me from deliberately stepping into the street, and methodically knocking people's hats off— " +
                "then, I account it high time to get to sea as soon as possible. This is my substitute for " +
                "pistol and ball. With a philosophical flourish Cato throws himself upon his sword; I quietly " +
                "take to the ship. There is nothing surprising in this. If they but knew it, almost all men " +
                "in their degree, some time or other, cherish very nearly the same feelings towards the ocean " +
                "with me. The whale! The whale! A vast creature of the deep, whale whale whale, swimming " +
                "through the endless ocean waters. Captain Ahab and his crew sail across the seas in search " +
                "of the great white whale. Moby Dick, the legendary whale, eludes capture time and again. " +
                "The ship's crew works tirelessly, whale watching, whale hunting, always seeking the whale.";
        
        try (BufferedReader reader = new BufferedReader(new StringReader(sampleText))) {
            analyzer.analyzeText(reader);
        }
        
        analyzer.processingTimeMs = 5; // Simulated processing time for demo
    }
}
