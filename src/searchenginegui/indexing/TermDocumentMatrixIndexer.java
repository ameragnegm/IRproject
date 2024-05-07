package searchenginegui.indexing;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermDocumentMatrixIndexer {
    private String datasetFolder;
    private Map<String, Map<String, Integer>> index;
    private List<List<String>> processedDocuments;
    public TermDocumentMatrixIndexer(String datasetFolder) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();
    }
public TermDocumentMatrixIndexer(String datasetFolder, List<List<String>> processedDocuments) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();
    this.processedDocuments = processedDocuments;
    }
    public List<String> searchInTermDocumentIndex(List<String> tokens) {
        List<String> searchResults = new ArrayList<>();
        for (String token : tokens) {
            if (index.containsKey(token)) {
                searchResults.add(token);
            }
        }
        return searchResults;
    }
public void buildIndex() {
    File folder = new File(datasetFolder);
    File[] files = folder.listFiles();
    
    if (files != null) {
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    for (String word : words) {
                        index.putIfAbsent(word, new HashMap<>());
                        index.get(word).merge(file.getName(), 1, Integer::sum);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName());
                e.printStackTrace();
            }
        }
    } else {
        System.err.println("No files found in the dataset folder.");
    }
}

    public Map<String, Map<String, Integer>> getIndex() {
        return index;
    }
}
