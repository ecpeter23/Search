import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Website {
    public String url;
    Connection connection;
    Document doc;

    private List<String> words;
    private HashMap<String, Integer> map;
    private int totalWords;
    private final DecimalFormat df;

    public Website(String url) throws IOException {
        this.url = url;
        connection = Jsoup.connect(this.url);
        doc = connection.get();
        df = new DecimalFormat("#.##");

        /*
        System.out.println(doc.body().text());
        System.out.println(doc.text());
        System.out.println(Arrays.toString(doc.wholeText().lines().toArray()));
        Object[] line = doc.wholeText().lines().toArray();
        for(int i = 0; i < line.length; i++){
            System.out.println(line[i]);
        }
        */
    }

    /**
     * Gets the plain-text of the website and the words in the plain-text
     */
    public void read(){
        // =======================================================
        // Step 1: Get the plain text of the website
        // =======================================================
        String result = doc.body().text();

        // =======================================================
        // Step 2: Create a list of all words from that plain text
        // =======================================================
        words = new ArrayList<>();

        // Creates a index of the first word 'break', which is where a word ends
        int lastIndex = -1;
        int index = index(result, 0,"\s", ".", "(", ")", "[", "]", "\"", ":", ";", "?", "!", ",", "/");  // result.indexOf("\s");

        // This loop repeats at least once
        do{
            // Gets the word by taking the previous index plus one and the index of the next word 'break'
            String word = result.substring(lastIndex + 1, (index == -1)? result.length() : index);

            // This checks if the word is a real word, if its greater than 1, and it contains at least one real ASIC char
            if (word.length() > 1 && word.matches(".*\\w.*")) words.add((word.charAt(0) != 39)? word : word.substring(1)); // this checks if the word starts with quotation marks and removes them

            // sets lastIndex to the previous index and gets the next closest word 'break'
            lastIndex = index;
            index = index(result, lastIndex + 1, "\s", ".", "(", ")", "[", "]", "\"", ":", ";", "?", "!", ",", "/");

        } while(lastIndex != -1); // loops until the previous index is -1, NOT THE CURRENT INDEX

        totalWords = words.size(); // stores the total word count before removing duplicates for later use in the stats
    }

    /**
     * Sorts the words by most occurring to least occurring
     */
    public void sort(){
        // =============================================================================
        // Step 1: Instantiate a hashmap to cache the number of occurrences of each word
        // =============================================================================

        map = new HashMap<>();

        // Goes through each word and if it exists in the hashmap adds one to its value, which is the amount of times it occurs
        // If it doesn't exist it puts it in the map with a default value of 1
        for (String word : words) {
            map.putIfAbsent(word, 0);
            map.replace(word, map.get(word) + 1);
        }

        // ==========================================================================================
        // Step 2: Sort the words array with the number of occurrences which is stored in the hashmap
        // ==========================================================================================

        // removes all duplicates in words
        words = words.parallelStream().distinct().collect(Collectors.toList());

        // loops through words to sort them by occurrences from greatest to least
        // goes from right to left while sorting
        for(int i = 0; i < words.size() - 1; i++){
            String word = words.get(i); // This is a holder of the word
            int ind = i; // index of where the greatest word is

            for(int j = i + 1; j < words.size(); j++) {
                if (map.get(words.get(j)) > map.get(word)) {
                    word = words.get(j);
                    ind = j;
                }
            }

            if (ind == i) continue; // if the greatest word is already in place we can just continue te loop

            // swaps the index's of the greatest word
            word = words.get(i);
            words.set(i, words.get(ind));
            words.set(ind, word);
        }

        // System.out.println(map);
        // System.out.println(Arrays.toString(words.toArray()));
    }

    /**
     * Prints the most used words in the website
     *
     * @param amount amount of words to print
     * @param unique Should the list include articles and conjunctions
     */
    public void topWords(int amount, boolean unique){
        int count = 0;

        for(int i = 0; i < amount + count; i++){
            if (unique && Util.contains(words.get(i))) {
                count++;
                continue;
            }

            System.out.println((i + 1) + ": " + words.get(i) + ", occurrences: " + map.get(words.get(i)));
        }
    }


    /**
     * Prints the word statistics of the website
     */
    public void stats(){
        System.out.println(" ");
        System.out.println(doc.title());                                                              // Title
        System.out.println("url: " + url);                                                            // URL
        System.out.println(" ");
        System.out.println("Total Words: " + totalWords + ", Total Distinct Words: " + words.size()); // Word Count
        System.out.println("Total Characters: " + doc.body().text().length());                        // Char Count
        System.out.println("Sentences: " + doc.wholeText().lines().toArray().length);                 // Sentence Count
        System.out.println(" ");
        System.out.println("Reading Time: " + df.format(totalWords / 238.0) + " minutes");    // Reading Time
        System.out.println(" ");
        System.out.println("Top 10 Most Popular Words:");
        topWords(10, false);                                                           // Top 10 Words
        System.out.println(" ");
        System.out.println("Top 10 Most Popular Unique Words:");
        topWords(10, true);                                                            // Top 10 Unique Words

    }

    public int occurrences(String word){ // gets the occurrences of a specific word
        return map.getOrDefault(word, 0);
    }

    private int index(String string, int startindex, String... chs){ // custom method to get the lowest index
        int min = Integer.MAX_VALUE;

        // Finds the lowest index from multiple indexOf
        for(String ch : chs) {
            int index = string.indexOf(ch, startindex);
            if (index < min && index != -1) min = index;
        }

        return (min == Integer.MAX_VALUE)? -1 : min; // if the min is the max value there is no occurrences
    }
}
