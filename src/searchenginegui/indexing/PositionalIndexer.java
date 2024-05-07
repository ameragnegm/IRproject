package searchenginegui.indexing;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionalIndexer {

    private String datasetFolder;
    private Map<String, Map<String, Integer>> index;
    private List<List<String>> processedDocuments;

    public PositionalIndexer(String datasetFolder) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();
    }

    public PositionalIndexer(String datasetFolder, List<List<String>> processedDocuments) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();
        this.processedDocuments = processedDocuments;

    }

    public List<String> searchInPositionalIndex(List<String> tokens) {
        List<String> searchResults = new ArrayList<>();
        for (String token : tokens) {
            if (index.containsKey(token)) {
                searchResults.add(token);
            }
        }
        return searchResults;
    }

    public void buildIndex() throws IOException {
        File folder = new File(datasetFolder);
        File[] files = folder.listFiles();

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int position = 0;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!index.containsKey(word)) {
                        index.put(word, new HashMap<>());
                    }
                    if (!index.get(word).containsKey(file.getName())) {
                        index.get(word).put(file.getName(), position);
                    } else {
                        int prevPosition = index.get(word).get(file.getName());
                        index.get(word).put(file.getName(), prevPosition + position);
                    }
                    position++;
                }
            }
            reader.close();
        }
    }

    public Map<String, Map<String, Integer>> getIndex() {
        return index;
    }
}
