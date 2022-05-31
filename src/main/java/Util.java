import java.util.Arrays;

public class Util {
    public static String[] nonDistinctWords = {"a", "an", "and", "the", "for", "nor", "but", "or", "yet", "so",
            "both", "whether", "either", "neither", "just", "as", "if", "then", "rather", "than", "such", "that",
            "after", "although", "as", "if", "by", "in", "though", "now", "how", "since", "to", "too", "of", "was",
            "it", "is", "have", "with", "which", "at", "be", "The"};

    public static boolean contains(String word){
        return Arrays.asList(nonDistinctWords).contains(word);
    }
}
