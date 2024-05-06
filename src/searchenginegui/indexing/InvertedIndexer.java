package searchenginegui.indexing;import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvertedIndexer {
    private String datasetFolder;
    private Map<String, String[]> index;

    public InvertedIndexer(String datasetFolder) {
        this.datasetFolder = datasetFolder;
        this.index = new HashMap<>();
    }

    public void buildIndex() throws IOException {
        File folder = new File(datasetFolder);
        File[] files = folder.listFiles();
        
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!index.containsKey(word)) {
                        index.put(word, new String[]{file.getName()});
                    } else {
                        String[] documents = index.get(word);
                        if (!containsDocument(documents, file.getName())) {
                            String[] newDocuments = new String[documents.length + 1];
                            System.arraycopy(documents, 0, newDocuments, 0, documents.length);
                            newDocuments[documents.length] = file.getName();
                            index.put(word, newDocuments);
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

