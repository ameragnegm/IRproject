package preprocessing;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Normalization {
    public static List<List<String>> normalizeDocuments(List<List<String>> tokenizedDocuments) {
        List<List<String>> normalizedDocuments = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            List<String> normalizedWords = new ArrayList<>();
            for (String word : tokens) {
                String normalizedWord = Normalizer.normalize(word, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");
                normalizedWords.add(normalizedWord);
            }
            normalizedDocuments.add(normalizedWords);
        }
        return normalizedDocuments;
    }
}
