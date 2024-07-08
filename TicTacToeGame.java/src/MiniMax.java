import java.util.Arrays;

public class MiniMax {
    public record Result(int score, int[] move) {
    }

    public static Result minimax(int[][] board, int depth, boolean isMaximizingPlayer) {
        if (checkForWin(board, 1)) {
            return new Result(1, null);
        } else if (checkForWin(board, -1)) {
            return new Result(-1, null);
        } else if (isBoardFull(board)) {
            return new Result(0, null);
        }

        int bestScore;
        int[] bestMove = null;

        if (isMaximizingPlayer) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 1;
                        int score = minimax(board, depth + 1, false).score();
                        board[i][j] = 0;
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = -1;
                        int score = minimax(board, depth + 1, true).score();
                        board[i][j] = 0;
                        if (score < bestScore) {
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                    }
                }
            }
        }

        return new Result(bestScore, bestMove);
    }

    public static void main(String[] args) {
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };

        // Make a move (player X)
        board[1][1] = 1;

        // Computer's turn (player O)
        Result result = minimax(board, 0, true);
        if (result.move() != null) {
            int row = result.move()[0];
            int col = result.move()[1];
            board[row][col] = -1;
        }

        // Print the board
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static boolean checkForWin(int[][] board, int playerMark) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == playerMark && board[i][1] == playerMark && board[i][2] == playerMark) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == playerMark && board[1][i] == playerMark && board[2][i] == playerMark) {
                return true;
            }
        }
        if (board[0][0] == playerMark && board[1][1] == playerMark && board[2][2] == playerMark) {
            return true;
        }
        return board[0][2] == playerMark && board[1][1] == playerMark && board[2][0] == playerMark;
    }


    private static boolean isBoardFull(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
