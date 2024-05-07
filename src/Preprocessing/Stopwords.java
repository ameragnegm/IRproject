package preprocessing;import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stopwords {
    private static final List<String> STOP_WORDS = Arrays.asList(
            "a", "an", "the", "and", "or", "in", "on", "at", "to", "for", "with", "of", "i"
    );

    public static List<String> removeStopWords(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<>();
        for (String word : tokens) {
            if (!STOP_WORDS.contains(word.toLowerCase())) {
                filteredTokens.add(word);
            }
        }
        return filteredTokens;
    }

    public static List<List<String>> removeStopWordsFromDocuments(List<List<String>> tokenizedDocuments) {
        List<List<String>> processedDocuments = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            List<String> filteredTokens = removeStopWords(tokens);
            processedDocuments.add(filteredTokens);
        }
        return processedDocuments;
    }
}
