package org.cis1200;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * CheckersMainPanel allows two users play checkers against each other manually.
 * Red colored Checkers always starts the new game. If a player can jump an
 * opponent's
 * piece, then the player must jump. When a player can make no more
 * moves, the game ends.
 * 
 */
public class CheckersMainPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String CURRENT_PLAYER_CODE = "currentPlayerCode";
    private static final String CURRENT_CHECKERS_POSITION = "currentCheckersPosition";

    private JButton newGameButton; // Button for starting a new game.
    private JButton resumeGameButton; // Button for resuming an in-progress game that was paused by
                                      // closing the app earlier.
    private JButton resignButton; // Button a player can use to end the game by resigning.
    private JButton undoPreviousMoveButton; // Button a player can use to undo the previously
                                            // completed move (if any) in the game.

    private JLabel message; // Label for displaying messages to the user.

    /**
     * The constructor creates the BoardInnerPanel (which in turn creates and
     * manages
     * the buttons and message label), adds all the components, and sets
     * the bounds of the components. A null layout is used. (This is
     * the only thing that is done in the CheckersMainPanel class.)
     */
    public CheckersMainPanel() {

        setLayout(null); // manual configure the layout.
        setPreferredSize(new Dimension(600, 450)); // overall panel size
        setBackground(new Color(250, 200, 152)); // Pastel Orange background.

        /* Create the components and add them to the Main panel. */
        BoardInnerPanel boardInnerPanel = new BoardInnerPanel(); // Note: The constructor for the
                                                                 // board also creates the buttons
                                                                 // and label.
        add(boardInnerPanel);
        add(newGameButton);
        add(resignButton);
        add(resumeGameButton);
        add(undoPreviousMoveButton);

        add(message);

        /*
         * Set the position and size of each component by calling
         * its setBounds() method.
         */
        boardInnerPanel.setBounds(20, 20, 324, 324); // Note: size MUST be 324-by-324 !
        newGameButton.setBounds(410, 60, 160, 45);
        resignButton.setBounds(410, 120, 160, 45);
        resumeGameButton.setBounds(410, 180, 160, 45);
        undoPreviousMoveButton.setBounds(410, 240, 160, 45);

        message.setBounds(0, 380, 350, 30);

    } // end constructor

    /**
     * This Inner panel displays a 320-by-320 checkerboard pattern with
     * a 2-pixel black border. It is assumed that the size of the
     * panel is set to exactly 324-by-324 pixels. This class does
     * the work of letting the users play checkers, and it displays
     * the checkerboard.
     */
    private class BoardInnerPanel extends JPanel implements ActionListener, MouseListener {

        CheckersBoard checkersBoard; // The data for the checkers board is kept here.
                                     // This board is also responsible for generating
                                     // lists of legal moves.

        boolean gameInProgress; // Is a game currently in progress?

        /* The next three variables are valid only when the game is in progress. */
        int currentPlayer; // Whose turn is it now? The possible values
                           // are CheckersBoard.RED and CheckersBoard.BLACK.

        int selectedRow, selectedCol; // If the current player has selected a piece to
                                      // move, these give the row and column
                                      // containing that piece. If no piece is
                                      // yet selected, then selectedRow is -1.

        CheckersMove[] legalMoves; // An array containing the legal moves for the current player.

        List<Map<String, Object>> listOfBoardStateMap = new ArrayList<Map<String, Object>>();

        /**
         * Constructor. Create the buttons and label. Listens for mouse
         * clicks and for clicks on the buttons. Create the board and
         * start the first game.
         */
        BoardInnerPanel() {
            setBackground(Color.BLACK);
            addMouseListener(this);
            resignButton = new JButton("Resign");
            resignButton.addActionListener(this);

            newGameButton = new JButton("New Game");
            newGameButton.addActionListener(this);

            resumeGameButton = new JButton("Resume Game");
            resumeGameButton.addActionListener(this);

            undoPreviousMoveButton = new JButton("Undo Previous Move");
            undoPreviousMoveButton.addActionListener(this);

            message = new JLabel("", JLabel.CENTER);
            message.setFont(new Font("Serif", Font.BOLD, 14));
            message.setForeground(Color.BLUE);
            checkersBoard = new CheckersBoard();
            startNewGame();
        }

        /**
         * Respond to user's click on one of the four buttons.
         */
        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            if (src == newGameButton) {
                startNewGame();
            } else if (src == resignButton) {
                resignTheGame();
            } else if (src == resumeGameButton) {
                resumePausedGame();
            } else if (src == undoPreviousMoveButton) {
                undoPreviousMove();
            }
        }

        /**
         * Start a new game
         */
        private void startNewGame() {
            if (gameInProgress) {
                // This should not be possible, but it doesn't hurt to check.
                message.setText("Finish the current game first!");
                return;
            }
            checkersBoard.setUpGame(); // Set up the pieces.
            currentPlayer = CheckersBoard.RED; // RED moves first.
            legalMoves = checkersBoard.getLegalMoves(CheckersBoard.RED); // Get RED's legal moves.
            selectedRow = -1; // RED has not yet selected a piece to move.
            message.setText("RED:  Make your move.");

            newGameButton.setEnabled(false);
            resignButton.setEnabled(true);

            gameInProgress = FileDataService.loadCurrentGameInProgressData();
            if (!gameInProgress) {
                resumeGameButton.setEnabled(false);
                gameInProgress = true;
                FileDataService.writeCurrentGameInProgressData(gameInProgress);
            } else {
                resumeGameButton.setEnabled(true);
            }

            // Initial state for which undo button will remain disabled
            Map<String, Object> boardStateMap = new HashMap<String, Object>();
            boardStateMap.put(CheckersMainPanel.CURRENT_PLAYER_CODE, currentPlayer);
            boardStateMap.put(
                    CheckersMainPanel.CURRENT_CHECKERS_POSITION,
                    checkersBoard
                            .getClonedCopyOfCheckersPositions(checkersBoard.getCheckersPosition())
            );
            listOfBoardStateMap.add(boardStateMap);

            undoPreviousMoveButton.setEnabled(false);

            repaint();
        }

        /**
         * Resume an In-progress game
         */
        private void resumePausedGame() {
            gameInProgress = FileDataService.loadCurrentGameInProgressData();

            int[][] checkersPosition = FileDataService.readPreviousStatefromFile();
            checkersBoard
                    .resumeGame(checkersBoard.getClonedCopyOfCheckersPositions(checkersPosition));

            currentPlayer = FileDataService.loadCurrentPlayerData(); // CurrentPlayer moves first.
            legalMoves = checkersBoard.getLegalMoves(currentPlayer); // Get CurrentPlayer's legal
                                                                     // moves.
            selectedRow = -1; // currentPlayer is expected to select a piece to move
                              // (regardless of the auto pre-select of a piece by the system or the
                              // manual select of a piece by the then currentPlayer).

            if (currentPlayer == 1) {
                if (legalMoves != null && legalMoves[0].isJump()) {
                    message.setText("RED:  Make your move.  You must jump.");
                } else {
                    message.setText("RED:  Make your move.");
                }
            } else if (currentPlayer == 3) {
                if (legalMoves != null && legalMoves[0].isJump()) {
                    message.setText("BLACK:  Make your move.  You must jump.");
                } else {
                    message.setText("BLACK:  Make your move.");
                }
            }

            newGameButton.setEnabled(false);
            resignButton.setEnabled(true);
            resumeGameButton.setEnabled(false);

            // resume state considered equivalent to resetting to a form of initial state as
            // well, for which undo button will remain disabled
            listOfBoardStateMap = new ArrayList<Map<String, Object>>();

            Map<String, Object> boardStateMap = new HashMap<String, Object>();
            boardStateMap.put(CheckersMainPanel.CURRENT_PLAYER_CODE, currentPlayer);
            boardStateMap.put(
                    CheckersMainPanel.CURRENT_CHECKERS_POSITION,
                    checkersBoard
                            .getClonedCopyOfCheckersPositions(checkersBoard.getCheckersPosition())
            );
            listOfBoardStateMap.add(boardStateMap);

            undoPreviousMoveButton.setEnabled(false);

            /* Make sure the board is redrawn in its new state. */
            FileDataService.writeCurrentPlayerData(currentPlayer);

            repaint();
        }

        /**
         * Current player resigns. Game ends. Opponent wins.
         */
        private void resignTheGame() {
            if (!gameInProgress) { // Should be impossible.
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == CheckersBoard.RED) {
                gameOver("RED resigns.  BLACK wins.");
            } else {
                gameOver("BLACK resigns.  RED wins.");
            }

            repaint();
        }

        /**
         * The game ends. The parameter, str, is displayed as a message
         * to the user. The states of the buttons are adjusted so players
         * can start a new game. This method is called when the game
         * ends at any point in this class.
         */
        private void gameOver(String str) {
            message.setText(str);
            newGameButton.setEnabled(true);
            resignButton.setEnabled(false);
            resumeGameButton.setEnabled(false);

            // 'resign by' or 'no further legal moves for' a given player is considered
            // equivalent to completed state, for which undo button will be set to disabled
            listOfBoardStateMap = new ArrayList<Map<String, Object>>();
            undoPreviousMoveButton.setEnabled(false);

            FileDataService.removeCurrentGameInProgressData();
            FileDataService.removeCurrentPlayerData();
            FileDataService.removeCurrentStatusData();

            gameInProgress = false;
            FileDataService.writeCurrentGameInProgressData(gameInProgress);
        }

        /**
         * Undo the previous move by restoring to a state prior during an In-progress
         * game
         *
         */
        private void undoPreviousMove() {
            gameInProgress = FileDataService.loadCurrentGameInProgressData();

            int sizeOfListOfBoardStateMap = listOfBoardStateMap.size();
            listOfBoardStateMap.remove(sizeOfListOfBoardStateMap - 1);

            Map<String, Object> boardStateMap = listOfBoardStateMap
                    .get(sizeOfListOfBoardStateMap - 2);

            int[][] checkersPosition = (int[][]) boardStateMap
                    .get(CheckersMainPanel.CURRENT_CHECKERS_POSITION);
            checkersBoard
                    .resetGame(checkersBoard.getClonedCopyOfCheckersPositions(checkersPosition));

            currentPlayer = (int) boardStateMap.get(CheckersMainPanel.CURRENT_PLAYER_CODE);
            legalMoves = checkersBoard.getLegalMoves(currentPlayer); // Get CurrentPlayer's legal
                                                                     // moves.
            selectedRow = -1; // currentPlayer is expected to select a piece to move
                              // (regardless of the auto pre-select of a piece by the system or the
                              // manual select of a piece by the then currentPlayer).

            if (currentPlayer == 1) {
                if (legalMoves != null && legalMoves[0].isJump()) {
                    message.setText("RED:  Make your move.  You must jump.");
                } else {
                    message.setText("RED:  Make your move.");
                }
            } else if (currentPlayer == 3) {
                if (legalMoves != null && legalMoves[0].isJump()) {
                    message.setText("BLACK:  Make your move.  You must jump.");
                } else {
                    message.setText("BLACK:  Make your move.");
                }
            }

            newGameButton.setEnabled(false);
            resignButton.setEnabled(true);
            resumeGameButton.setEnabled(false);

            // undo button will remain disabled if the listOfBoardStateMap collection
            // contains just one element
            // if it is more than it will remain enabled
            sizeOfListOfBoardStateMap = listOfBoardStateMap.size();
            if (sizeOfListOfBoardStateMap > 1) {
                undoPreviousMoveButton.setEnabled(true);
            } else {
                undoPreviousMoveButton.setEnabled(false);
            }

            /* Make sure the board is redrawn in its new state. */
            FileDataService.writeCurrentPlayerData(currentPlayer);

            repaint();
        }

        /**
         * This is called by mousePressed() when a player clicks on the
         * square in the specified row and col. It has already been checked
         * that a game is, in fact, in progress.
         */
        private void clickCheckersSquare(int row, int col) {

            /*
             * If the player clicked on one of the pieces that the player
             * can move, mark this row and col as selected and return. (This
             * might change a previous selection.) Reset the message, in
             * case it was previously displaying an error message.
             */

            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].getFromRow() == row && legalMoves[i].getFromCol() == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersBoard.RED) {
                        message.setText("RED:  Make your move.");
                    } else {
                        message.setText("BLACK:  Make your move.");
                    }
                    repaint();
                    return;
                }
            }

            /*
             * If no piece has been selected to be moved, the user must first
             * select a piece. Show an error message and return.
             */
            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }

            /*
             * If the user clicked on a square where the selected piece can be
             * legally moved, then make the move and return.
             */
            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].getFromRow() == selectedRow
                        && legalMoves[i].getFromCol() == selectedCol
                        && legalMoves[i].getToRow() == row && legalMoves[i].getToCol() == col) {
                    moveCheckersPiece(legalMoves[i]);
                    return;
                }
            }

            /*
             * If we get to this point, there is a piece selected, and the square where
             * the user just clicked is not one where that piece can be legally moved.
             * Show an error message.
             */
            message.setText("Click the square you want to move to.");

        } // end doClickSquare()

        /**
         * This is called when the current player has chosen the specified
         * move. Make the move, and then either end or continue the game
         * appropriately.
         */
        private void moveCheckersPiece(CheckersMove move) {

            checkersBoard.moveTheCheckers(move);

            /*
             * If the move was a jump, it's possible that the player has another
             * jump. Check for legal jumps starting from the square that the player
             * just moved to. If there are any, the player must jump. The same
             * player continues moving.
             */
            if (move.isJump()) {
                legalMoves = checkersBoard
                        .getLegalJumpsFrom(currentPlayer, move.getToRow(), move.getToCol());
                if (legalMoves != null) {
                    if (currentPlayer == CheckersBoard.RED) {
                        message.setText("RED:  You must continue jumping.");
                    } else {
                        message.setText("BLACK:  You must continue jumping.");
                    }
                    selectedRow = move.getToRow(); // Since only one piece can be moved, select it.
                    selectedCol = move.getToCol();
                    FileDataService.writeCurrentPlayerData(currentPlayer);

                    // for every moved to state after which undo button will be enabled
                    Map<String, Object> boardStateMap = new HashMap<String, Object>();
                    boardStateMap.put(CheckersMainPanel.CURRENT_PLAYER_CODE, currentPlayer);
                    boardStateMap.put(
                            CheckersMainPanel.CURRENT_CHECKERS_POSITION,
                            checkersBoard.getClonedCopyOfCheckersPositions(
                                    checkersBoard.getCheckersPosition()
                            )
                    );
                    listOfBoardStateMap.add(boardStateMap);

                    undoPreviousMoveButton.setEnabled(true);

                    repaint();
                    return;
                }
            }

            /*
             * The current player's turn is ended, so change to the other player.
             * Get that player's legal moves. If the player has no legal moves,
             * then the game ends.
             */
            if (currentPlayer == CheckersBoard.RED) {
                currentPlayer = CheckersBoard.BLACK;
                legalMoves = checkersBoard.getLegalMoves(currentPlayer);
                if (legalMoves == null) {
                    gameOver("BLACK has no moves.  RED wins.");
                } else if (legalMoves[0].isJump()) {
                    message.setText("BLACK:  Make your move.  You must jump.");
                } else {
                    message.setText("BLACK:  Make your move.");
                }
            } else {
                currentPlayer = CheckersBoard.RED;
                legalMoves = checkersBoard.getLegalMoves(currentPlayer);
                if (legalMoves == null) {
                    gameOver("RED has no moves.  BLACK wins.");
                } else if (legalMoves[0].isJump()) {
                    message.setText("RED:  Make your move.  You must jump.");
                } else {
                    message.setText("RED:  Make your move.");
                }
            }

            /*
             * Set selectedRow = -1 to record that the player has not yet selected
             * a piece to move.
             */
            selectedRow = -1;

            /*
             * As a courtesy to the user, if all legal moves use the same piece, then
             * select that piece automatically so the user won't have to click on it
             * to select it.
             */
            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++) { 
                    if (legalMoves[i].getFromRow() != legalMoves[0].getFromRow()
                            || legalMoves[i].getFromCol() != legalMoves[0].getFromCol()) {
                        sameStartSquare = false;
                        break;
                    }
                }

                if (sameStartSquare) {
                    selectedRow = legalMoves[0].getFromRow();
                    selectedCol = legalMoves[0].getFromCol();
                }
            }

            /* Make sure the board is redrawn in its new state. */
            FileDataService.writeCurrentPlayerData(currentPlayer);

            Map<String, Object> boardStateMap = new HashMap<String, Object>();
            boardStateMap.put(CheckersMainPanel.CURRENT_PLAYER_CODE, currentPlayer);
            boardStateMap.put(
                    CheckersMainPanel.CURRENT_CHECKERS_POSITION,
                    checkersBoard
                            .getClonedCopyOfCheckersPositions(checkersBoard.getCheckersPosition())
            );
            listOfBoardStateMap.add(boardStateMap);

            if (legalMoves != null) {
                undoPreviousMoveButton.setEnabled(true);
            } else {
                undoPreviousMoveButton.setEnabled(false);
            }

            repaint();

        } // end doMakeMove();

        /**
         * Draw a checkerboard pattern in gray and lightGray. Draw the
         * checkers. If a game is in progress, hilite the legal moves.
         */
        public void paintComponent(Graphics g) {

            /* Turn on antialiasing to get nicer ovals. */
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            /* Draw a two-pixel black border around the edges of the canvas. */
            g.setColor(Color.black);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
            g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);

            /* Draw the squares of the checkerboard and the checkers. */
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        g.setColor(Color.LIGHT_GRAY);
                    } else {
                        g.setColor(Color.GRAY);
                    }
                    g.fillRect(2 + col * 40, 2 + row * 40, 40, 40);
                    switch (checkersBoard.pieceAt(row, col)) {
                        case CheckersBoard.RED:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col * 40, 4 + row * 40, 36, 36);
                            break;
                        case CheckersBoard.BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col * 40, 4 + row * 40, 36, 36);
                            break;
                        case CheckersBoard.RED_KING:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col * 40, 4 + row * 40, 36, 36);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 20 + col * 40, 20 + row * 40);
                            break;
                        case CheckersBoard.BLACK_KING:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col * 40, 4 + row * 40, 36, 36);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 20 + col * 40, 20 + row * 40);
                            break;
                        default:
                            break;
                    }
                }
            }

            /*
             * If a game is in progress, hilite the legal moves. Note that legalMoves
             * is never null while a game is in progress.
             */
            if (gameInProgress) {
                /* First, draw a 2-pixel cyan border around the pieces that can be moved. */
                g.setColor(Color.cyan);
                for (int i = 0; i < legalMoves.length; i++) {
                    g.drawRect(
                            2 + legalMoves[i].getFromCol() * 40,
                            2 + legalMoves[i].getFromRow() * 40, 38, 38
                    );
                }
                /*
                 * If a piece is selected for moving (i.e. if selectedRow >= 0), then
                 * draw a 2-pixel white border around that piece and draw green borders
                 * around each square that that piece can be moved to.
                 */
                if (selectedRow >= 0) {
                    g.setColor(Color.white);
                    g.drawRect(2 + selectedCol * 40, 2 + selectedRow * 40, 38, 38);
                    g.setColor(Color.green);
                    for (int i = 0; i < legalMoves.length; i++) {
                        if (legalMoves[i].getFromCol() == selectedCol
                                && legalMoves[i].getFromRow() == selectedRow) {
                            g.drawRect(
                                    2 + legalMoves[i].getToCol() * 40,
                                    2 + legalMoves[i].getToRow() * 40, 38, 38
                            );
                        }
                    }
                }
            }
        } // end paintComponent()

        /**
         * Respond to a user click on the board. If no game is in progress, show
         * an error message. Otherwise, find the row and column that the user
         * clicked and call doClickSquare() to handle it.
         */
        public void mousePressed(MouseEvent evt) {
            if (!gameInProgress) {
                message.setText("Click \"New Game\" to start a new game.");
            } else {
                int col = (evt.getX() - 2) / 40;
                int row = (evt.getY() - 2) / 40;
                if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                    clickCheckersSquare(row, col);
                }
            }
        }

        public void mouseReleased(MouseEvent evt) {
        }

        public void mouseClicked(MouseEvent evt) {
        }

        public void mouseEntered(MouseEvent evt) {
        }

        public void mouseExited(MouseEvent evt) {
        }
    } // end inner class BoardInnerPanel
} // end class CheckersMainPanel
