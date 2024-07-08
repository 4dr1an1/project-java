import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicTacToe extends JFrame {
    private final JButton[][] buttons;
    private char currentPlayerMark;
    private int xScore;
    private int oScore;
    private JLabel xScoreLabel;
    private JLabel oScoreLabel;
    private boolean isSinglePlayerMode;

    public TicTacToe() {
        super("Kółko i krzyżyk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buttons = new JButton[3][3];
        currentPlayerMark = 'X';
        xScore = 0;
        oScore = 0;
        isSinglePlayerMode = false;

        initializeButtons();
        initializeScorePanel();
        initializeOptionsPanel();

        pack();
        setSize(600, 600);
    }

    private void initializeButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton button = new JButton("");
                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 60));
                button.addActionListener(new ButtonClickListener());
                buttons[i][j] = button;
                buttonPanel.add(button);
            }
        }
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void initializeScorePanel() {
        JPanel scorePanel = new JPanel(new GridLayout(2, 2));
        JLabel xLabel = new JLabel("Krzyżyk: ");
        xScoreLabel = new JLabel(Integer.toString(xScore));
        JLabel oLabel = new JLabel("Kółko: ");
        oScoreLabel = new JLabel(Integer.toString(oScore));

        scorePanel.add(xLabel);
        scorePanel.add(xScoreLabel);
        scorePanel.add(oLabel);
        scorePanel.add(oScoreLabel);

        JButton scoreButton = new JButton("Wynik");
        scoreButton.addActionListener(new ScoreButtonClickListener());
        scorePanel.add(scoreButton);

        add(scorePanel, BorderLayout.NORTH);
    }

    private void initializeOptionsPanel() {
        JPanel optionsPanel = new JPanel(new FlowLayout());
        String[] difficultyLevels = {"Łatwy", "Trudny"};
        JComboBox<String> difficultyComboBox = new JComboBox<>(difficultyLevels);
        difficultyComboBox.setSelectedIndex(1); // Wybór trudnego poziomu trudności
        difficultyComboBox.addActionListener(new DifficultyComboBoxListener());
        optionsPanel.add(new JLabel("Poziom trudności:"));
        optionsPanel.add(difficultyComboBox);

        JButton startButton = new JButton("Nowa gra");
        startButton.addActionListener(new StartButtonClickListener());
        optionsPanel.add(startButton);

        JButton playComputerButton = new JButton("Graj z komputerem");
        playComputerButton.addActionListener(new PlayComputerButtonClickListener());
        optionsPanel.add(playComputerButton);

        add(optionsPanel, BorderLayout.SOUTH);
    }

    private void resetButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(null); // Dodano reset koloru przycisków
            }
        }
    }

    private boolean checkForWin() {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(buttons[i][1].getText())
                    && buttons[i][0].getText().equals(buttons[i][2].getText())
                    && !buttons[i][0].getText().isEmpty()) {
                highlightWinningButtons(i, 0, i, 1, i, 2);
                return true;
            }

            if (buttons[0][i].getText().equals(buttons[1][i].getText())
                    && buttons[0][i].getText().equals(buttons[2][i].getText())
                    && !buttons[0][i].getText().isEmpty()) {
                highlightWinningButtons(0, i, 1, i, 2, i);
                return true;
            }
        }

        if (buttons[0][0].getText().equals(buttons[1][1].getText())
                && buttons[0][0].getText().equals(buttons[2][2].getText())
                && !buttons[0][0].getText().isEmpty()) {
            highlightWinningButtons(0, 0, 1, 1, 2, 2);
            return true;
        }

        if (buttons[0][2].getText().equals(buttons[1][1].getText())
                && buttons[0][2].getText().equals(buttons[2][0].getText())
                && !buttons[0][2].getText().isEmpty()) {
            highlightWinningButtons(0, 2, 1, 1, 2, 0);
            return true;
        }

        return false;
    }

    private boolean checkForDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void highlightWinningButtons(int x1, int y1, int x2, int y2, int x3, int y3) {
        buttons[x1][y1].setBackground(Color.GREEN);
        buttons[x2][y2].setBackground(Color.GREEN);
        buttons[x3][y3].setBackground(Color.GREEN);
    }

    private void makeComputerMove() {
        if (!isSinglePlayerMode || currentPlayerMark != 'O') {
            return;
        }

        // Logika ruchu komputera

        int bestScore = Integer.MIN_VALUE;
        int bestMoveRow = -1;
        int bestMoveCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText("O");

                    int score = minimax(0, false);

                    buttons[i][j].setText("");

                    if (score > bestScore) {
                        bestScore = score;
                        bestMoveRow = i;
                        bestMoveCol = j;
                    }
                }
            }
        }

        if (bestMoveRow != -1 && bestMoveCol != -1) {
            buttons[bestMoveRow][bestMoveCol].setText("O");
            buttons[bestMoveRow][bestMoveCol].setEnabled(false);
        }
    }

    private int minimax(int depth, boolean isMaximizingPlayer) {
        if (checkForWin()) {
            if (isMaximizingPlayer) {
                return -1;
            } else {
                return 1;
            }
        } else if (checkForDraw()) {
            return 0;
        }

        int bestScore;
        if (isMaximizingPlayer) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().isEmpty()) {
                        buttons[i][j].setText("O");
                        int score = minimax(depth + 1, false);
                        buttons[i][j].setText("");
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().isEmpty()) {
                        buttons[i][j].setText("X");
                        int score = minimax(depth + 1, true);
                        buttons[i][j].setText("");
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
        }
        return bestScore;
    }

    private void updateScore() {
        if (currentPlayerMark == 'X') {
            xScore++;
            xScoreLabel.setText(Integer.toString(xScore));
        } else if (currentPlayerMark == 'O') {
            oScore++;
            oScoreLabel.setText(Integer.toString(oScore));
        }
    }

    private void switchPlayer() {
        currentPlayerMark = (currentPlayerMark == 'X') ? 'O' : 'X';
    }

    private void endGame(String message) {
        JOptionPane.showMessageDialog(this, message, "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
        resetButtons();
        updateScore();
    }

    private void saveScoreToFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String scoreData = currentDateTime + "," + xScore + "," + oScore + "\n";

        try {
            File file = new File("scores.csv");
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(scoreData);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showScoreDialog() {
        try {
            File file = new File("scores.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder scoreTable = new StringBuilder();

            scoreTable.append("Data              | Krzyżyk | Kółko\n");
            scoreTable.append("----------------------------------\n");

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                scoreTable.append(data[0]).append(" | ")
                        .append(data[1]).append("      | ")
                        .append(data[2]).append("\n");
            }

            JOptionPane.showMessageDialog(this, scoreTable.toString(), "Wyniki", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setText(Character.toString(currentPlayerMark));
            button.setEnabled(false);

            if (checkForWin()) {
                endGame("Gracz " + currentPlayerMark + " wygrywa!");
                return;
            } else if (checkForDraw()) {
                endGame("Remis!");
                return;
            }

            switchPlayer();
            makeComputerMove();

            if (checkForWin()) {
                endGame("Gracz " + currentPlayerMark + " wygrywa!");
            } else if (checkForDraw()) {
                endGame("Remis!");
            }

            switchPlayer();
        }
    }

    private class ScoreButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveScoreToFile();
            showScoreDialog();
        }
    }

    private static class DifficultyComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            String selectedDifficulty = (String) comboBox.getSelectedItem();

            assert selectedDifficulty != null;
        }
    }

    private class StartButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetButtons();
            xScore = 0;
            oScore = 0;
            xScoreLabel.setText(Integer.toString(xScore));
            oScoreLabel.setText(Integer.toString(oScore));
        }
    }

    private class PlayComputerButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isSinglePlayerMode = true;
            resetButtons();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe().setVisible(true));
    }
}