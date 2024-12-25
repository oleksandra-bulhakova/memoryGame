package org.university.labs;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class MemoryGame {

    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        public Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }
    }

    String[] cardList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    List<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = cardWidth * columns;
    int boardHeight = cardHeight * rows;

    JFrame frame = new JFrame("Space Memory Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartGameButton = new JButton();

    List<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton cardOneSelected;
    JButton cardTwoSelected;

    int errorCount = 0;


    public MemoryGame() {
        setupCards();
        shuffleCards();
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Errors: " + errorCount);

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));

        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (cardOneSelected == null) {
                            cardOneSelected = tile;
                            int index = board.indexOf(cardOneSelected);
                            cardOneSelected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (cardTwoSelected == null) {
                            cardTwoSelected = tile;
                            int index = board.indexOf(cardTwoSelected);
                            cardTwoSelected.setIcon(cardSet.get(index).cardImageIcon);

                            if (cardOneSelected.getIcon() != cardTwoSelected.getIcon()) {
                                errorCount++;
                                textLabel.setText("Errors: " + errorCount);
                                hideCardTimer.start();

                            } else {
                                cardOneSelected = null;
                                cardTwoSelected = null;

                                if (isGameFinished()) {
                                    JOptionPane.showMessageDialog(
                                            frame,
                                            "Game Over!\nTotal Errors: " + errorCount,
                                            "Game Finished",
                                            JOptionPane.INFORMATION_MESSAGE
                                    );
                                    restartGameButton.setEnabled(true);
                                    gameReady = false;
                                }
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartGameButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartGameButton.setFocusable(false);
        restartGameButton.setText("Restart Game");
        restartGameButton.setPreferredSize(new Dimension(boardWidth, 31));
        restartGameButton.setEnabled(false);
        restartGamePanel.add(restartGameButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);
        restartGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady && !isGameFinished()) {
                    return;
                }
                gameReady = false;
                restartGameButton.setEnabled(false);
                cardOneSelected = null;
                cardTwoSelected = null;
                shuffleCards();

                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                }
                errorCount = 0;
                textLabel.setText("Errors: " + errorCount);
                hideCardTimer.restart();
            }
        });

        frame.pack();
        frame.setVisible(true);

        hideCardTimer = new Timer(3000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    private boolean isGameFinished() {
        for (JButton tile : board) {
            if (tile.getIcon() == cardBackImageIcon) {
                return false;
            }
        }
        return true;
    }

    private void hideCards() {
        if (gameReady && cardOneSelected != null && cardTwoSelected != null) {
            cardOneSelected.setIcon(cardBackImageIcon);
            cardOneSelected = null;
            cardTwoSelected.setIcon(cardBackImageIcon);
            cardTwoSelected = null;
        }
        else {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
        }
        gameReady = true;
        restartGameButton.setEnabled(true);
    }

    void setupCards() {
        cardSet = new ArrayList<Card>();
        for (String cardName : cardList) {
            Image cardImage = new ImageIcon(getClass().getResource("/img/" + cardName + ".png")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        Image cardBackImage = new ImageIcon(getClass().getResource("/img/cover.png")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }
}
