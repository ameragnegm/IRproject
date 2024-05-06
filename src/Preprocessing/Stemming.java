package preprocessing;
import org.tartarus.snowball.ext.englishStemmer ;

import java.util.ArrayList;
import java.util.List;

public class Stemming {
    public static List<List<String>> stemDataset(List<List<String>> tokenizedDocuments) {
        List<List<String>> stemmedDocuments = new ArrayList<>();
        englishStemmer stemmer = new englishStemmer(); 
        for (List<String> tokens : tokenizedDocuments) {
            List<String> stems = new ArrayList<>();
            for (String word : tokens) {
                stemmer.setCurrent(word);
                stemmer.stem();
                stems.add(stemmer.getCurrent());
            }
            stemmedDocuments.add(stems);
        }
        return stemmedDocuments;
    }
}
