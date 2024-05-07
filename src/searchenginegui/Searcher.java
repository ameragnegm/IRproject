package searchenginegui;
import searchenginegui.indexing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import preprocessing.*;

public class Searcher extends JFrame implements ActionListener {

    private JTextField searchField;
    private JTextArea searchOutputTextArea;
    private JButton searchButton, backButton;
    private SearchEngineGUI mainPage;
    private String selectedIndexType;
    private List<String> selectedPreprocessingOptions;

    public Searcher(SearchEngineGUI mainPage, String selectedIndexType, List<String> selectedPreprocessingOptions, String indexingResults) {
        this.mainPage = mainPage;
        this.selectedIndexType = selectedIndexType;
        this.selectedPreprocessingOptions = selectedPreprocessingOptions;
        setTitle("Search Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        searchOutputTextArea = new JTextArea();
        searchOutputTextArea.setEditable(false);

        backButton = new JButton("Back");
        backButton.addActionListener(this);

        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel searchInputPanel = new JPanel();
        searchInputPanel.add(new JLabel("Search Phrase:"));
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(new JScrollPane(searchOutputTextArea), BorderLayout.CENTER);
        searchPanel.add(backButton, BorderLayout.SOUTH);

        add(searchPanel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            performSearch(selectedIndexType, selectedPreprocessingOptions);
        } else if (e.getSource() == backButton) {
            mainPage.setVisible(true);
            dispose();
        }
    }

    private void performSearch(String indexType, List<String> preprocessingOptions) {
        String searchPhrase = searchField.getText().trim().toLowerCase();
        StringBuilder searchResults = new StringBuilder();

        // Tokenization
        List<String> tokensAfterTokenization = Tokenization.tokenizeDocument(searchPhrase);
        searchResults.append("Preprocessing Phase: Tokenization\n");
        searchResults.append("Tokens after Tokenization: ").append(tokensAfterTokenization).append("\n\n");

        // Apply selected preprocessing options in the specified order
        List<String> processedTokens = new ArrayList<>(tokensAfterTokenization);
        for (String phase : preprocessingOptions) {
            switch (phase) {
                case "Stop Words":
                    processedTokens = Stopwords.removeStopWords(processedTokens);
                    searchResults.append("Preprocessing Phase: Stopwords\n");
                    searchResults.append("Tokens after Stopword Removal: ").append(processedTokens).append("\n\n");
                    break;
                case "Stemming":
                    processedTokens = Stemming.stemDocument(processedTokens);
                    searchResults.append("Preprocessing Phase: Stemming\n");
                    searchResults.append("Tokens after Stemming: ").append(processedTokens).append("\n\n");
                    break;
                case "Normalization":
                    processedTokens = Normalization.normalizeTokens(processedTokens);
                    searchResults.append("Preprocessing Phase: Normalization\n");
                    searchResults.append("Tokens after Normalization: ").append(processedTokens).append("\n\n");
                    break;
                case "Lemmatization":
                    processedTokens = Lemmatization.lemmatizeTokens(processedTokens);
                    searchResults.append("Preprocessing Phase: Lemmatization\n");
                    searchResults.append("Tokens after Lemmatization: ").append(processedTokens).append("\n\n");
                    break;
            }
        }

        searchResults.append("Search results for: ").append(processedTokens).append("\n");

        // Search in the specified index type and display results
        List<String> searchInIndexResults;
        if (indexType.equalsIgnoreCase("Biword Index")) {
            searchPhrase = BiwordIndexer.biwordIndexSearchPhrase(processedTokens);
            searchInIndexResults = searchInIndex(indexType, Tokenization.tokenizeDocument(searchPhrase));
        } else {
            searchInIndexResults = searchInIndex(indexType, processedTokens);
        }
        searchResults.append(formatSearchResults(searchInIndexResults)).append("\n");

        // Set the search results in the text area
        searchOutputTextArea.setText(searchResults.toString());
    }

    private List<String> searchInIndex(String indexType, List<String> tokens) {
        switch (indexType) {
            case "Term Document Incidence Matrix":
                return searchInTermDocumentIndex(tokens);
            case "Inverted Index":
                return searchInInvertedIndex(tokens);
            case "Positional Index":
                return searchInPositionalIndex(tokens);
            case "Biword Index":
                return searchInBiwordIndex(tokens);
            default:
                System.out.println("Invalid index type selected.");
                return new ArrayList<>();
        }
    }

    private List<String> searchInTermDocumentIndex(List<String> tokens) {
        TermDocumentMatrixIndexer indexer = new TermDocumentMatrixIndexer("dataset");
        indexer.buildIndex();
        List<String> searchResults = new ArrayList<>();
        for (String token : tokens) {
            if (indexer.getIndex().containsKey(token)) {
                searchResults.add(token + ": " + indexer.getIndex().get(token));
            } else {
                searchResults.add(token + ": not found");
            }
        }
        return searchResults;
    }

    private List<String> searchInInvertedIndex(List<String> tokens) {
        InvertedIndexer indexer = new InvertedIndexer("dataset");
        try {
            indexer.buildIndex();
            List<String> searchResults = new ArrayList<>();
            for (String token : tokens) {
                if (indexer.getIndex().containsKey(token)) {
                    searchResults.add(token + ": " + indexer.getIndex().get(token));
                } else {
                    searchResults.add(token + ": not found");
                }
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> searchInPositionalIndex(List<String> tokens) {
        PositionalIndexer indexer = new PositionalIndexer("dataset");
        try {
            indexer.buildIndex();
            List<String> searchResults = new ArrayList<>();
            for (String token : tokens) {
                if (indexer.getIndex().containsKey(token)) {
                    searchResults.add(token + ": " + indexer.getIndex().get(token));
                } else {
                    searchResults.add(token + ": not found");
                }
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> searchInBiwordIndex(List<String> tokens) {
        BiwordIndexer indexer = new BiwordIndexer("dataset");
        try {
            indexer.buildIndex();
            List<String> searchResults = new ArrayList<>();
            for (String token : tokens) {
                if (indexer.getIndex().containsKey(token)) {
                    searchResults.add(token + ": " + indexer.getIndex().get(token));
                } else {
                    searchResults.add(token + ": not found");
                }
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String formatSearchResults(List<String> searchResults) {
        StringBuilder formattedResults = new StringBuilder();
        for (String result : searchResults) {
            formattedResults.append(result).append("\n");
        }
        return formattedResults.toString();
    }

}
