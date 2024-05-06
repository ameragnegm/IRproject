package preprocessing;

import java.util.ArrayList;
import java.util.List;

public class Tokenization {
    public static List<String> tokenizeDocument(String document) {
        List<String> tokens = new ArrayList<>();
        String[] words = document.split("\\s+");
        for (String word : words) {
            tokens.add(word);
        }
        return tokens;
    }

    public static List<List<String>> tokenizeDataset(List<String> documents) {
        List<List<String>> tokenizedDocuments = new ArrayList<>();
        for (String document : documents) {
            List<String> tokens = tokenizeDocument(document);
            tokenizedDocuments.add(tokens);
        }
        return tokenizedDocuments;
    }
}
