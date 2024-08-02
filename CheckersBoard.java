package org.cis1200;

import java.util.ArrayList;
import java.util.List;

/**
 * CheckersBoard class holds data about a game of checkers with complete details
 * on every piece and its location on the checkers board.
 * Note that RED moves "up" the checkers board (i.e. row number decreases)
 * while BLACK moves "down" the checkers board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersBoard {

    /*
     * The following constants represent the possible contents of a square
     * on the checkers board. The constants RED and BLACK also represent players
     * in the game.
     */

    public static final int EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;

    int[][] checkersPosition; // checkersPosition[r][c] is the contents of row r, column c.

    /**
     * Constructor. Create the checkersPosition and set it up for a new game.
     */
    public CheckersBoard() {
        checkersPosition = new int[8][8];
    }

    /**
     * Set up the checkers board with checkers in position for the beginning
     * of a game. Note that checkers can only be found in squares
     * that satisfy row % 2 == col % 2. At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    public void setUpGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == col % 2) {
                    if (row < 3) {
                        checkersPosition[row][col] = BLACK;
                    } else if (row > 4) {
                        checkersPosition[row][col] = RED;
                    } else {
                        checkersPosition[row][col] = EMPTY;
                    }
                } else {
                    checkersPosition[row][col] = EMPTY;
                }
            }
        }
    } // end setUpGame()

    /**
     * Resume the checkers board with checkers in position for the continuity
     * of the game.
     */
    public void resumeGame(int[][] checkersPosition) {
        this.checkersPosition = checkersPosition;
        FileDataService.writeCurrentStateToFile(this.checkersPosition);
    } // end resumeGame()

    /**
     * Reset the checkers board with checkers in position for the previous state of
     * the game.
     */
    public void resetGame(int[][] checkersPosition) {
        this.checkersPosition = checkersPosition;
        FileDataService.writeCurrentStateToFile(this.checkersPosition);
    } // end resetGame()

    /**
     * Get checkers in position AS IS from Checkers Board.
     */
    public int[][] getCheckersPosition() {
        return checkersPosition;
    } // end getCheckersPosition()

    /**
     * Get a current copy of the checkers in position.
     */
    public int[][] getClonedCopyOfCheckersPositions(int[][] checkersPosition) {
        int rows = checkersPosition.length;
        int cols = 0;

        if (rows >= 1) {
            cols = checkersPosition[0].length;
        }
        int[][] clonedArray = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                clonedArray[i][j] = checkersPosition[i][j];
            }
        }

        return clonedArray;
    } // end getClonedCopyOfCheckersPositions()

    /**
     * Return the contents of the square in the specified row and column.
     */
    public int pieceAt(int row, int col) {
        return checkersPosition[row][col];
    }

    /**
     * Move the Checkers Piece from one location to another. It is assumed that move
     * is non-null and that the move it represents is legal.
     */
    public void moveTheCheckers(CheckersMove move) {
        moveTheCheckers(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
        FileDataService.writeCurrentStateToFile(checkersPosition);
    }

    /**
     * Move the Checkers Piece from (fromRow,fromCol) to (toRow,toCol). It is
     * assumed that this move is legal. If the move is a jump, the
     * jumped piece is removed from the board. If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     */
    private void moveTheCheckers(int fromRow, int fromCol, int toRow, int toCol) {
        checkersPosition[toRow][toCol] = checkersPosition[fromRow][fromCol];
        checkersPosition[fromRow][fromCol] = EMPTY;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            // The move is a jump. Remove the jumped piece from the board.
            int jumpRow = (fromRow + toRow) / 2; // Row of the jumped piece.
            int jumpCol = (fromCol + toCol) / 2; // Column of the jumped piece.
            checkersPosition[jumpRow][jumpCol] = EMPTY;
        }
        if (toRow == 0 && checkersPosition[toRow][toCol] == RED) {
            checkersPosition[toRow][toCol] = RED_KING;
        }

        if (toRow == 7 && checkersPosition[toRow][toCol] == BLACK) {
            checkersPosition[toRow][toCol] = BLACK_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board. If the player
     * has no legal moves, null is returned. The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned. If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     */
    public CheckersMove[] getLegalMoves(int player) {

        if (player != RED && player != BLACK) {
            return null;
        }

        int playerKing; // The constant representing a King belonging to player.
        if (player == RED) {
            playerKing = RED_KING;
        } else {
            playerKing = BLACK_KING;
        }

        List<CheckersMove> moves = new ArrayList<CheckersMove>(); // Moves will be stored in this
                                                                  // list.

        /*
         * First, check for any possible jumps. Look at each square on the board.
         * If that square contains one of the player's pieces, look at a possible
         * jump in each of the four directions from that square. If there is
         * a legal jump in that direction, put it in the moves ArrayList.
         */

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (checkersPosition[row][col] == player
                        || checkersPosition[row][col] == playerKing) {
                    if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2)) {
                        moves.add(new CheckersMove(row, col, row + 2, col + 2));
                    }
                    if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2)) {
                        moves.add(new CheckersMove(row, col, row - 2, col + 2));
                    }
                    if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2)) {
                        moves.add(new CheckersMove(row, col, row + 2, col - 2));
                    }
                    if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2)) {
                        moves.add(new CheckersMove(row, col, row - 2, col - 2));
                    }
                }
            }
        }

        /*
         * If any jump moves were found, then the user must jump, so we don't
         * add any regular moves. However, if no jumps were found, check for
         * any legal regular moves. Look at each square on the board.
         * If that square contains one of the player's pieces, look at a possible
         * move in each of the four directions from that square. If there is
         * a legal move in that direction, put it in the moves ArrayList.
         */

        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (checkersPosition[row][col] == player
                            || checkersPosition[row][col] == playerKing) {
                        if (canMove(player, row, col, row + 1, col + 1)) {
                            moves.add(new CheckersMove(row, col, row + 1, col + 1));
                        }
                        if (canMove(player, row, col, row - 1, col + 1)) {
                            moves.add(new CheckersMove(row, col, row - 1, col + 1));
                        }
                        if (canMove(player, row, col, row + 1, col - 1)) {
                            moves.add(new CheckersMove(row, col, row + 1, col - 1));
                        }
                        if (canMove(player, row, col, row - 1, col - 1)) {
                            moves.add(new CheckersMove(row, col, row - 1, col - 1));
                        }
                    }
                }
            }
        }

        /*
         * If no legal moves have been found, return null. Otherwise, create
         * an array just big enough to hold all the legal moves, copy the
         * legal moves from the ArrayList into the array, and return the array.
         */

        if (moves.size() == 0) {
            return null;
        } else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)  {
                moveArray[i] = moves.get(i);
            }
            return moveArray;
        }

    } // end getLegalMoves

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column. If no such
     * jumps are possible, null is returned. The logic is similar
     * to the logic of the getLegalMoves() method.
     */
    public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        if (player != RED && player != BLACK) {
            return null;
        }
        int playerKing; // The constant representing a King belonging to player.
        if (player == RED) {
            playerKing = RED_KING;
        } else {
            playerKing = BLACK_KING;
        }
        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>(); // The legal jumps will be
                                                                       // stored in this list.
        if (checkersPosition[row][col] == player || checkersPosition[row][col] == playerKing) {
            if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2)) {
                moves.add(new CheckersMove(row, col, row + 2, col + 2));
            }
            if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2)) {
                moves.add(new CheckersMove(row, col, row - 2, col + 2));
            }
            if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2)) {
                moves.add(new CheckersMove(row, col, row + 2, col - 2));
            }
            if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2)) {
                moves.add(new CheckersMove(row, col, row - 2, col - 2));
            }
        }
        if (moves.size() == 0) {
            return null;
        } else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++) {
                moveArray[i] = moves.get(i);
            }
            return moveArray;
        }
    } // end getLegalMovesFrom()

    /**
     * This is called by the two previous methods to check whether the
     * player can legally jump from (r1,c1) to (r3,c3). It is assumed
     * that the player has a piece at (r1,c1), that (r3,c3) is a position
     * that is 2 rows and 2 columns distant from (r1,c1) and that
     * (r2,c2) is the square between (r1,c1) and (r3,c3).
     */
    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8) {
            return false; // (r3,c3) is off the board.
        }

        if (checkersPosition[r3][c3] != EMPTY) {
            return false; // (r3,c3) already contains a piece.
        }

        if (player == RED) {
            if (checkersPosition[r1][c1] == RED && r3 > r1) {
                return false; // Regular red piece can only move up.
            }
            if (checkersPosition[r2][c2] != BLACK && checkersPosition[r2][c2] != BLACK_KING) {
                return false; // There is no black piece to jump.
            }
            return true; // The jump is legal.
        } else {
            if (checkersPosition[r1][c1] == BLACK && r3 < r1) {
                return false; // Regular black piece can only move down.
            }
            if (checkersPosition[r2][c2] != RED && checkersPosition[r2][c2] != RED_KING) {
                return false; // There is no red piece to jump.
            }
            return true; // The jump is legal.
        }

    } // end canJump()

    /**
     * This is called by the getLegalMoves() method to determine whether
     * the player can legally move from (r1,c1) to (r2,c2). It is
     * assumed that (r1,r2) contains one of the player's pieces and
     * that (r2,c2) is a neighboring square.
     */
    private boolean canMove(int player, int r1, int c1, int r2, int c2) {

        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) {
            return false; // (r2,c2) is off the board.
        }

        if (checkersPosition[r2][c2] != EMPTY) {
            return false; // (r2,c2) already contains a piece.
        }

        if (player == RED) {
            if (checkersPosition[r1][c1] == RED && r2 > r1) {
                return false; // Regular red piece can only move down.
            }
            return true; // The move is legal.
        } else {
            if (checkersPosition[r1][c1] == BLACK && r2 < r1) {
                return false; // Regular black piece can only move up.
            }
            return true; // The move is legal.
        }

    } // end canMove()

} // end class CheckersBoard
