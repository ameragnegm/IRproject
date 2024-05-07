package preprocessing;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Normalization {
    public static List<String> normalizeTokens(List<String> tokens) {
        List<String> normalizedTokens = new ArrayList<>();
        for (String token : tokens) {
            String normalizedToken = Normalizer.normalize(token, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
            normalizedTokens.add(normalizedToken);
        }
        return normalizedTokens;
    }

    public static List<List<String>> normalizeDocuments(List<List<String>> tokenizedDocuments) {
        List<List<String>> normalizedDocuments = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            List<String> normalizedTokens = normalizeTokens(tokens);
            normalizedDocuments.add(normalizedTokens);
        }
        return normalizedDocuments;
    }
}
