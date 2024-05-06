package searchenginegui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import preprocessing.Tokenization;

public class Searcher extends JFrame implements ActionListener {

    private JTextField searchField;
    private JTextArea searchOutputTextArea;
    private JButton searchButton, backButton;
    private SearchEngineGUI mainPage;
    private String selectedIndexType;
    private String selectedPreprocessingOptions;

    public Searcher(SearchEngineGUI mainPage, String selectedIndexType, String selectedPreprocessingOptions) {
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
            performSearch();
        } else if (e.getSource() == backButton) {
            mainPage.setVisible(true); 
            dispose(); 
        }
    }

    private void performSearch() {
        String searchPhrase = searchField.getText().trim().toLowerCase();
        StringBuilder searchResults = new StringBuilder();

        List<String> tokens = Tokenization.tokenizeDocument(searchPhrase);

        for (String token : tokens) {
            searchResults.append("Token: ").append(token).append("\n");
     
        }

        searchOutputTextArea.setText(searchResults.toString());
    }

}
