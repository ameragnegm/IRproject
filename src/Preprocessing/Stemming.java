package preprocessing;
import org.tartarus.snowball.ext.englishStemmer;

import java.util.ArrayList;
import java.util.List;

public class Stemming {
    public static List<String> stemDocument(List<String> tokens) {
        List<String> stems = new ArrayList<>();
        englishStemmer stemmer = new englishStemmer();
        for (String word : tokens) {
            stemmer.setCurrent(word);
            stemmer.stem();
            stems.add(stemmer.getCurrent());
        }
        return stems;
    }

    public static List<List<String>> stemDocuments(List<List<String>> tokenizedDocuments) {
        List<List<String>> stemmedDocuments = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            List<String> stems = stemDocument(tokens);
            stemmedDocuments.add(stems);
        }
        return stemmedDocuments;
    }
}
