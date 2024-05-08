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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import preprocessing.*;

public class Searcher extends JFrame implements ActionListener {

    private JTextField searchField;
    private JTextArea searchOutputTextArea;
    private JButton searchButton, backButton;
    private SearchEngineGUI mainPage;
    private String selectedIndexType;
    private List<String> selectedPreprocessingOptions;

    public Searcher(SearchEngineGUI mainPage, String selectedIndexType, List<String> selectedPreprocessingOptions, StringBuilder indexingResults, List<List<String>> processedDocuments) {
        System.err.println(indexingResults);
        System.err.println(processedDocuments);

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
        String searchPhrase = searchField.getText().trim().toLowerCase(); // Convert search phrase to lowercase
        StringBuilder searchResults = new StringBuilder();

        // Tokenization
        List<String> tokensAfterTokenization = Arrays.asList(searchPhrase.toLowerCase().split("\\s+"));
        searchResults.append("Preprocessing Phase: Tokenization\n");
        searchResults.append("Tokens after Tokenization: ").append(tokensAfterTokenization).append("\n\n");

        // Apply selected preprocessing options in the specified order
        List<String> processedTokens = new ArrayList<>(tokensAfterTokenization);
        for (String phase : Arrays.asList("Stop Words", "Stemming", "Normalization", "Lemmatization")) {
            if (preprocessingOptions.contains(phase)) {
                if (phase.equals("Stop Words")) {
                    processedTokens = Stopwords.removeStopWords(processedTokens);
                    searchResults.append("Preprocessing Phase: Stopwords\n");
                    searchResults.append("Tokens after Stopword Removal: ").append(processedTokens).append("\n\n");
                } else if (phase.equals("Stemming")) {
                    processedTokens = Stemming.stemDocument(processedTokens);
                    searchResults.append("Preprocessing Phase: Stemming\n");
                    searchResults.append("Tokens after Stemming: ").append(processedTokens).append("\n\n");
                } else if (phase.equals("Normalization")) {
                    processedTokens = Normalization.normalizeTokens(processedTokens);
                    searchResults.append("Preprocessing Phase: Normalization\n");
                    searchResults.append("Tokens after Normalization: ").append(processedTokens).append("\n\n");
                } else if (phase.equals("Lemmatization")) {
                    processedTokens = Lemmatization.lemmatizeTokens(processedTokens);
                    searchResults.append("Preprocessing Phase: Lemmatization\n");
                    searchResults.append("Tokens after Lemmatization: ").append(processedTokens).append("\n\n");
                }
            }
        }

        searchResults.append("Search results for: ").append(tokensAfterTokenization).append("\n");

        // Search in the specified index type and display results
        List<String> searchInIndexResults = searchInIndex(indexType, tokensAfterTokenization);
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

    // Methods for searching in different index types
private List<String> searchInTermDocumentIndex(List<String> tokens) {
    TermDocumentMatrixIndexer indexer = new TermDocumentMatrixIndexer("dataset");
    indexer.buildIndex();
    List<String> searchResults = new ArrayList<>();

    // Retrieve the document IDs from the index
    Set<String> documentIds = indexer.getIndex().values()
            .stream()
            .flatMap(map -> map.keySet().stream())
            .collect(Collectors.toSet());

    // Build the header row for the table
    StringBuilder headerBuilder = new StringBuilder("|   Term   |");
    for (String docId : documentIds) {
        headerBuilder.append(" ").append(docId).append(" |");
    }
    searchResults.add(headerBuilder.toString());

    // Build rows for each token
    for (String token : tokens) {
        // Convert the token to lowercase for case-insensitive comparison
        String lowercaseToken = token.toLowerCase();
        // Create a row for the token and its presence in each document
        StringBuilder rowBuilder = new StringBuilder("|  " + token + "  |");
        for (String docId : documentIds) {
            if (indexer.getIndex().containsKey(lowercaseToken) && indexer.getIndex().get(lowercaseToken).containsKey(docId)) {
                rowBuilder.append("   1   |");
            } else {
                rowBuilder.append("   0   |");
            }
        }
        searchResults.add(rowBuilder.toString());
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
                    searchResults.add(token + ": " + Arrays.toString(indexer.getIndex().get(token)));
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

            // Convert all tokens to lowercase for case-insensitive search
            List<String> lowercaseTokens = new ArrayList<>();
            for (String token : tokens) {
                lowercaseTokens.add(token.toLowerCase());
            }

            // Search for lowercase tokens in the indexer
            for (String token : lowercaseTokens) {
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

            // Create biwords from tokens
            List<String> biwords = new ArrayList<>();
            for (int i = 0; i < tokens.size() - 1; i++) {
                biwords.add(tokens.get(i) + " " + tokens.get(i + 1));
            }

            // Search for biwords in the indexer
            for (String biword : biwords) {
                if (indexer.getIndex().containsKey(biword)) {
                    searchResults.add(biword + ": " + Arrays.toString(indexer.getIndex().get(biword)));
                } else {
                    searchResults.add(biword + ": not found");
                }
            }

            // If a single token was provided, find the biword containing that token
            if (tokens.size() == 1) {
                String singleToken = tokens.get(0);
                for (String biword : indexer.getIndex().keySet()) {
                    if (biword.contains(singleToken)) {
                        searchResults.add(biword + ": " + Arrays.toString(indexer.getIndex().get(biword)));
                    }
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
