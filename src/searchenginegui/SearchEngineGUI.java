package searchenginegui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import preprocessing.*;
import searchenginegui.indexing.BiwordIndexer;
import searchenginegui.indexing.InvertedIndexer;
import searchenginegui.indexing.PositionalIndexer;
import searchenginegui.indexing.TermDocumentMatrixIndexer;

public class SearchEngineGUI extends JFrame implements ActionListener {

    private JButton indexButton, searchPageButton;
    private JCheckBox tokenizeCheckbox, stopWordsCheckbox, lemmatizeCheckbox, stemCheckbox, normalizeCheckbox;
    private JComboBox<String> indexTypeComboBox;
    private JTextArea outputTextArea;

    public SearchEngineGUI() {
        setTitle("Search Engine GUI");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tokenizeCheckbox = new JCheckBox("Tokenization");
        stopWordsCheckbox = new JCheckBox("Stop Words");
        lemmatizeCheckbox = new JCheckBox("Lemmatization");
        stemCheckbox = new JCheckBox("Stemming");
        normalizeCheckbox = new JCheckBox("Normalization");

        indexTypeComboBox = new JComboBox<>(new String[]{"Term Document Incidence Matrix", "Inverted Index",
            "Positional Index", "Biword Index"});

        indexButton = new JButton(" Apply ");
        indexButton.addActionListener(this);

        searchPageButton = new JButton("Search Page");
        searchPageButton.addActionListener(this);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(6, 1));
        checkboxPanel.add(new JLabel("Select Preprocessing Options:"));
        checkboxPanel.add(tokenizeCheckbox);
        checkboxPanel.add(stopWordsCheckbox);
        checkboxPanel.add(lemmatizeCheckbox);
        checkboxPanel.add(stemCheckbox);
        checkboxPanel.add(normalizeCheckbox);
        checkboxPanel.add(new JLabel("Select Indexing Type:"));
        checkboxPanel.add(indexTypeComboBox);
        checkboxPanel.add(indexButton, CENTER_ALIGNMENT);
        checkboxPanel.add(searchPageButton, CENTER_ALIGNMENT);

        add(checkboxPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        setVisible(true);
    }
     // Initialize indexingResults here
    private String selectedPreprocessingOptions; // Store selected preprocessing options
    private String selectedIndexType;
    private StringBuilder indexingResults = new StringBuilder();
  @Override
public void actionPerformed(ActionEvent e) {
      List<String> documents = readDataset("dataset");

        List<List<String>> tokenizedDocuments = new ArrayList<>();
        for (String document : documents) {
            List<String> tokens = Tokenization.tokenizeDocument(document);
            tokenizedDocuments.add(tokens);
        }

        if (tokenizeCheckbox.isSelected()) {
            outputTextArea.append("Tokenization Results:\n");
            outputTextArea.append(tokenizedDocuments.toString() + "\n\n");
        }

        if (stopWordsCheckbox.isSelected()) {
            List<List<String>> cleanedDocuments = Stopwords.removeStopWordsFromDocuments(tokenizedDocuments);
            outputTextArea.append("Stop Words  Results:\n");
            outputTextArea.append(cleanedDocuments.toString() + "\n\n");
        }

        if (lemmatizeCheckbox.isSelected()) {
            List<List<String>> lemmatizedDocuments = Lemmatization.lemmatizeDocuments(tokenizedDocuments);
            outputTextArea.append("Lemmatization Results:\n");
            outputTextArea.append(lemmatizedDocuments.toString() + "\n\n");
        }

        if (stemCheckbox.isSelected()) {
            List<List<String>> stemmedDocuments = Stemming.stemDocuments(tokenizedDocuments);
            outputTextArea.append("Stemming Results:\n");
            outputTextArea.append(stemmedDocuments.toString() + "\n\n");
        }

        if (normalizeCheckbox.isSelected()) {
            List<List<String>> normalizedDocuments = Normalization.normalizeDocuments(tokenizedDocuments);
            outputTextArea.append("Normalization Results:\n");
            outputTextArea.append(normalizedDocuments.toString() + "\n\n");
        }

        String indexType = (String) indexTypeComboBox.getSelectedItem();
        indexingResults = new StringBuilder(); // Initialize indexingResults
        switch (indexType) {
            case "Term Document Incidence Matrix":
                TermDocumentMatrixIndexer indexer1 = new TermDocumentMatrixIndexer("dataset");
                indexer1.buildIndex();
                indexingResults.append("Indexing using Term-Document Matrix...\n");
                indexingResults.append(indexer1.getIndex()).append("\n");
                break;
            case "Inverted Index":
                InvertedIndexer indexer2 = new InvertedIndexer("dataset");
                try {
                    indexer2.buildIndex();
                    indexingResults.append("Indexing using Inverted Index...\n");
                    indexingResults.append(formatIndex(indexer2.getIndex())).append("\n");
                } catch (IOException ex) {
                    indexingResults.append("Error indexing using Inverted Index.\n");
                    ex.printStackTrace();
                }
                break;
            case "Positional Index":
                PositionalIndexer indexer3 = new PositionalIndexer("dataset");
                try {
                    indexer3.buildIndex();
                    indexingResults.append("Indexing using Positional Index...\n");
                    indexingResults.append(indexer3.getIndex()).append("\n");
                } catch (IOException ex) {
                    indexingResults.append("Error indexing using Positional Index.\n");
                    ex.printStackTrace();
                }
                break;
            case "Biword Index":
                BiwordIndexer indexer4 = new BiwordIndexer("dataset");
                try {
                    indexer4.buildIndex();
                    indexingResults.append("Indexing using Biword Index...\n");
                    indexingResults.append(formatIndexBiword(indexer4.getIndex())).append("\n");
                } catch (IOException ex) {
                    indexingResults.append("Error indexing using Biword Index.\n");
                    ex.printStackTrace();
                }
                break;
            default:
                indexingResults.append("Invalid index type selected.\n");
                break;
        }
    if (e.getSource() == indexButton) {
        clearOutput(); // Clear the text area 
        outputTextArea.append(indexingResults.toString());
        outputTextArea.append("Indexing complete! Result displayed.");
    } else if (e.getSource() == searchPageButton) {
        List<String> selectedPreprocessingOptions = getSelectedPreprocessingOptions();
        selectedIndexType = (String) indexTypeComboBox.getSelectedItem();
        Searcher searchPage = new Searcher(this, selectedIndexType, selectedPreprocessingOptions, indexingResults.toString()); // Pass indexingResults to constructor
        searchPage.setVisible(true);
        dispose();
    }
}

    private List<String> getSelectedPreprocessingOptions() {
        List<String> optionsList = new ArrayList<>();
        if (tokenizeCheckbox.isSelected()) {
            optionsList.add("Tokenization");
        }
        if (stopWordsCheckbox.isSelected()) {
            optionsList.add("Stop Words");
        }
        if (lemmatizeCheckbox.isSelected()) {
            optionsList.add("Lemmatization");
        }
        if (stemCheckbox.isSelected()) {
            optionsList.add("Stemming");
        }
        if (normalizeCheckbox.isSelected()) {
            optionsList.add("Normalization");
        }
        return optionsList;
    }

    private void clearOutput() {
        outputTextArea.setText("");
    }

    private String formatIndex(Map<String, String[]> index) {
        StringBuilder formattedIndex = new StringBuilder();
        for (Map.Entry<String, String[]> entry : index.entrySet()) {
            formattedIndex.append(entry.getKey()).append(": ");
            String[] values = entry.getValue();
            for (String value : values) {
                formattedIndex.append(value).append(", ");
            }
            formattedIndex.delete(formattedIndex.length() - 2, formattedIndex.length()); // Remove the last comma and space
            formattedIndex.append("\n");
        }
        return formattedIndex.toString();
    }

    private String formatIndexBiword(Map<String, String[]> index) {
        StringBuilder formattedIndex = new StringBuilder();
        for (Map.Entry<String, String[]> entry : index.entrySet()) {
            formattedIndex.append(entry.getKey()).append(": ");
            String[] values = entry.getValue();
            for (String value : values) {
                formattedIndex.append(value).append(", ");
            }
            formattedIndex.delete(formattedIndex.length() - 2, formattedIndex.length()); // Remove the last comma and space
            formattedIndex.append("\n");
        }
        return formattedIndex.toString();
    }

    public List<String> readDataset(String folderPath) {
        List<String> documents = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try {
                        String content = new String(Files.readAllBytes(file.toPath()));
                        documents.add(content);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return documents;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SearchEngineGUI::new);
    }

}
