package searchenginegui.indexing;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import preprocessing.Tokenization;

public class BiwordIndexer {

    private String datasetFolder;
    private Map<String, String[]> index;
    private List<List<String>> processedDocuments;

    public BiwordIndexer(String datasetFolder, List<List<String>> processedDocuments) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();
        this.processedDocuments = processedDocuments;

    }

    public BiwordIndexer(String datasetFolder) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();

    }

    public static List<String> biwordIndexSearchPhrase(List<String> tokens) {
        List<String> biwords = new ArrayList<>();

        for (int i = 0; i < tokens.size() - 1; i++) {
            biwords.add(tokens.get(i) + " " + tokens.get(i + 1));
        }

        return biwords;
    }

    public List<String> searchInBiwordIndex(List<String> biwordTokens, List<List<String>> processedDocuments) {
        Map<String, String[]> biwordIndex = buildBiwordIndex(processedDocuments);
        List<String> searchResults = new ArrayList<>();

        for (int i = 0; i < biwordTokens.size() - 1; i++) {
            String biword = biwordTokens.get(i) + " " + biwordTokens.get(i + 1);
            if (biwordIndex.containsKey(biword)) {
                searchResults.addAll(Arrays.asList(biwordIndex.get(biword)));
            } else {
                searchResults.add(biword + ": not found");
            }
        }
        return searchResults;
    }

    private Map<String, String[]> buildBiwordIndex(List<List<String>> processedDocuments) {
        Map<String, String[]> biwordIndex = new HashMap<>();

        for (List<String> document : processedDocuments) {
            for (int i = 0; i < document.size() - 1; i++) {
                String biword = document.get(i) + " " + document.get(i + 1);
                if (!biwordIndex.containsKey(biword)) {
                    biwordIndex.put(biword, new String[]{});
                }
                String[] documents = biwordIndex.get(biword);
                String documentName = "Document " + (processedDocuments.indexOf(document) + 1);
                String[] newDocuments = Arrays.copyOf(documents, documents.length + 1);
                newDocuments[documents.length] = documentName;
                biwordIndex.put(biword, newDocuments);
            }
        }
        return biwordIndex;
    }

    public void buildIndex() throws IOException {
        File folder = new File(datasetFolder);
        File[] files = folder.listFiles();

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    String biword = words[i] + " " + words[i + 1];
                    if (!index.containsKey(biword)) {
                        index.put(biword, new String[]{file.getName()});
                    } else {
                        String[] documents = index.get(biword);
                        if (!containsDocument(documents, file.getName())) {
                            String[] newDocuments = new String[documents.length + 1];
                            System.arraycopy(documents, 0, newDocuments, 0, documents.length);
                            newDocuments[documents.length] = file.getName();
                            index.put(biword, newDocuments);
                        }
                    }
                }
            }
            reader.close();
        }
    }

    private boolean containsDocument(String[] documents, String document) {
        for (String doc : documents) {
            if (doc.equals(document)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String[]> getIndex() {
        return index;
    }
}
