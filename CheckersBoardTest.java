package org.cis1200;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckersBoardTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testSetupGame() {
        CheckersBoard checkersBoard = new CheckersBoard();
        checkersBoard.setUpGame();
        int[][] checkersPosition = checkersBoard.getCheckersPosition();

        assertEquals(checkersPosition[0][0], 3);
        assertEquals(checkersPosition[1][1], 3);
        assertEquals(checkersPosition[2][2], 3);

        assertEquals(checkersPosition[3][0], 0);
        assertEquals(checkersPosition[3][3], 0);
        assertEquals(checkersPosition[3][6], 0);
        assertEquals(checkersPosition[4][1], 0);
        assertEquals(checkersPosition[4][4], 0);
        assertEquals(checkersPosition[4][7], 0);

        assertEquals(checkersPosition[5][0], 0);
        assertEquals(checkersPosition[6][1], 0);
        assertEquals(checkersPosition[7][2], 0);
    }

    @Test
    void testClonedCopyOfCheckersPosition() {
        CheckersBoard checkersBoard = new CheckersBoard();
        checkersBoard.setUpGame();
        int[][] checkersPosition = checkersBoard.getCheckersPosition();
        int[][] clonecCopyOfCheckersPosition = checkersBoard
                .getClonedCopyOfCheckersPositions(checkersPosition);

        assertEquals(checkersPosition[0][0], clonecCopyOfCheckersPosition[0][0]);
        assertEquals(checkersPosition[1][1], clonecCopyOfCheckersPosition[1][1]);
        assertEquals(checkersPosition[2][2], clonecCopyOfCheckersPosition[2][2]);

        assertEquals(checkersPosition[3][0], clonecCopyOfCheckersPosition[3][0]);
        assertEquals(checkersPosition[3][3], clonecCopyOfCheckersPosition[3][3]);
        assertEquals(checkersPosition[3][6], clonecCopyOfCheckersPosition[3][6]);
        assertEquals(checkersPosition[4][1], clonecCopyOfCheckersPosition[4][1]);
        assertEquals(checkersPosition[4][4], clonecCopyOfCheckersPosition[4][4]);
        assertEquals(checkersPosition[4][7], clonecCopyOfCheckersPosition[4][7]);

        assertEquals(checkersPosition[5][0], clonecCopyOfCheckersPosition[5][0]);
        assertEquals(checkersPosition[6][1], clonecCopyOfCheckersPosition[6][1]);
        assertEquals(checkersPosition[7][2], clonecCopyOfCheckersPosition[7][2]);
    }

    @Test
    void testLegalMoves() {
        CheckersBoard checkersBoard = new CheckersBoard();
        checkersBoard.setUpGame();

        CheckersMove[] movesForRed = checkersBoard.getLegalMoves(1);
        assertEquals(movesForRed.length, 7);

        CheckersMove[] movesForBlack = checkersBoard.getLegalMoves(3);
        assertEquals(movesForBlack.length, 7);
    }

    @Test
    void testResumeGameForBlack() {
        CheckersBoard checkersBoard = new CheckersBoard();
        checkersBoard.setUpGame();

        int[][] checkersPosition = checkersBoard.getCheckersPosition();
        int[][] clonedCheckersPosition = checkersBoard
                .getClonedCopyOfCheckersPositions(checkersPosition);
        clonedCheckersPosition[5][5] = 0;
        clonedCheckersPosition[4][4] = 1;

        checkersBoard.resumeGame(clonedCheckersPosition);
        CheckersMove[] movesForBlack = checkersBoard.getLegalMoves(3);
        assertEquals(movesForBlack.length, 7);
    }

    @Test
    void testResumeGameForRed() {
        CheckersBoard checkersBoard = new CheckersBoard();
        checkersBoard.setUpGame();

        int[][] checkersPosition = checkersBoard.getCheckersPosition();
        int[][] clonedCheckersPosition = checkersBoard
                .getClonedCopyOfCheckersPositions(checkersPosition);
        clonedCheckersPosition[5][5] = 0;
        clonedCheckersPosition[4][4] = 1;

        checkersBoard.resumeGame(clonedCheckersPosition);
        CheckersMove[] movesForRed = checkersBoard.getLegalMoves(1);
        assertEquals(movesForRed.length, 8);
    }

    @Test
    void testGetLegalJumpsForRed() {
        CheckersBoard checkersBoard = new CheckersBoard();
        checkersBoard.setUpGame();

        CheckersMove[] jumpsForRed = checkersBoard.getLegalJumpsFrom(1, 5, 1);
        assertNull(jumpsForRed);
    }

}
