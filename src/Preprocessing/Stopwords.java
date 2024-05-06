package preprocessing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stopwords {
    private static final List<String> STOP_WORDS = Arrays.asList(
            "a", "an", "the", "and", "or", "in", "on", "at", "to", "for", "with", "of" ,"is"
    );

    public static List<List<String>> removeStopWords(List<List<String>> tokenizedDocuments) {
        List<List<String>> processedDocuments = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            List<String> filteredWords = new ArrayList<>();
            for (String word : tokens) {
                if (!STOP_WORDS.contains(word.toLowerCase())) {
                    filteredWords.add(word);
                }
            }
            processedDocuments.add(filteredWords);
        }
        return processedDocuments;
    }
}
