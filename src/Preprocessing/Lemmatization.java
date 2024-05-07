package preprocessing;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Lemmatization {
    private static StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public static List<String> lemmatizeTokens(List<String> tokens) {
        List<String> lemmas = new ArrayList<>();
        String sentenceText = String.join(" ", tokens);
        Annotation document = new Annotation(sentenceText);
        pipeline.annotate(document);
        List<CoreLabel> words = document.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : words) {
            lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
        }
        return lemmas;
    }

    public static List<List<String>> lemmatizeDocuments(List<List<String>> tokenizedDocuments) {
        List<List<String>> lemmatizedDocuments = new ArrayList<>();
        for (List<String> tokens : tokenizedDocuments) {
            List<String> lemmas = lemmatizeTokens(tokens);
            lemmatizedDocuments.add(lemmas);
        }
        return lemmatizedDocuments;
    }
}
