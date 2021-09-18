/** your file header here
 * Author: Mohammed Abdoul
 * Login: cs8bsp19aa
 * This file contains the class GameState required for psa3 in the course CSE
 * 8B  
 * References: CSE 8B Spring 2019 Website 
 **/

import java.util.*;

/** Your Class Header Here 
 * This class defines the grid on which the game of Streamline is played and
 * the positions of the pieces on the board.
 * */
public class GameState
{
    // Used to populate char[][] board below and to display the
    // current state of play.
    final static char PLAYER_CHAR = '@';
    final static char GOAL_CHAR = 'G';
    final static char SPACE_CHAR = ' ';
    final static char TRAIL_CHAR = '+';
    final static char OBSTACLE_CHAR = 'O';
    final static char NEWLINE_CHAR = '\n';
    final static char HORIZONTAL_BORDER_CHAR = '-';
    final static char SIDE_BORDER_CHAR = '|';

    // This represents a 2D map of the board
    char[][] board;

    // Location of the player
    int playerRow;
    int playerCol;

    // Location of the goal
    int goalRow;
    int goalCol;

    // true means the player completed this level
    boolean levelPassed;

    public GameState(int height, int width, int playerRow, int playerCol, 
            int goalRow, int goalCol)
    {
        // TODO
        this.board = new char[height][width];
        for (int i = 0; i < this.board.length; i++){
            for (int j = 0; j < this.board[i].length; j++){
                this.board[i][j] = SPACE_CHAR;
            }
        }
        this.board[playerRow][playerCol] = PLAYER_CHAR;
        this.board[goalRow][goalCol] = GOAL_CHAR;
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        this.levelPassed = false;
    }

    public GameState(GameState other)
    {
        // TODO
        char[][] newBoard = new char[other.board.length][];
        for (int i = 0; i < newBoard.length; i++){
            newBoard[i] = Arrays.copyOf(other.board[i], other.board[i].length);
        }
        this.board = newBoard; 
        this.playerRow = other.playerRow;
        this.playerCol = other.playerCol;
        this.goalRow = other.goalRow;
        this.goalCol = other.goalCol;
        this.levelPassed = other.levelPassed;
    }

    void addRandomObstacles(int count)
    {
        // TODO
        Random randInt = new Random();
        int numA = 0;
        int numB = 0;
        int emptySpaces = 0;
        for (int i = 0; i < this.board.length; i++){
            for (int j = 0; j < this.board[i].length; j++){
                if (this.board[i][j] == SPACE_CHAR){
                    emptySpaces++;
                }
            }
        }
        if (count < 0 || count > emptySpaces){
            return;
        }
        for (int i = 0; i < count; i++){
            numA = randInt.nextInt(this.board.length - 1);
            numB = randInt.nextInt(this.board[numA].length - 1);
            if (numA == this.goalRow && numB == this.goalCol){
                i--;
            }
            else if (numA == this.playerRow && numB == this.playerCol){
                i--;
            }
            else if (this.board[numA][numB] == OBSTACLE_CHAR){
                i--;
            }
            else {
                this.board[numA][numB] = OBSTACLE_CHAR;
            }


        }
    }

    void rotateCounterClockwise()
    {
        // TODO
        for(int NicoleAniston = 0; NicoleAniston < 3; NicoleAniston++){
            int A = this.playerRow;
            int B = this.playerCol;
            int c = this.goalRow;
            int d = this.goalCol;
            char[][] newBoard = new char[this.board[0].length][this.board.length];

            for (int i = 0; i < this.board.length; i++){
                for (int j = 0; j < this.board[i].length; j++){
                    newBoard[j][this.board.length - 1 - i] = this.board[i][j];
                }
            }
            this.playerRow = B;
            this.playerCol = this.board.length - 1 - A;
            this.goalRow = d;
            this.goalCol = this.board.length - 1 - c;
            this.board = newBoard;
        }
    }

    void moveUp()
    {
        // TODO
        for (int i = this.playerRow-1; i > -1; i--){
            if (this.board[i][playerCol] == OBSTACLE_CHAR || this.board[i]
                    [playerCol] == TRAIL_CHAR){
                break;
            }
            if (this.board[i][playerCol] == SPACE_CHAR || this.board[i]
                    [playerCol] == GOAL_CHAR){
                this.board[i+1][playerCol] = TRAIL_CHAR;
                this.playerRow--;
            }

            else if (this.playerRow == 0 || this.board[this.board.length-1]
                    [playerCol] == SPACE_CHAR){
                this.playerRow = this.board.length-1;
                this.moveUp();
            }


        }
        if (this.playerRow == this.goalRow && this.playerCol ==
                this.goalCol){
            levelPassed = true;
            return;
        }

    }

    void move(Direction direction)
    {
        // TODO
        int total = 4;
        int rotationCount = direction.getRotationCount();
        if (rotationCount == 0){
            this.moveUp();
            return;
        }
        else{
            for (int i = 0; i < rotationCount; i++){
                this.rotateCounterClockwise();
            }
            this.moveUp();
            for (int i = 0; i < total - rotationCount; i++){
                this.rotateCounterClockwise();
            }
        }
    }


    @Override
        public String toString()
        {
            // TODO

            char [][] newBoard = new char[this.board.length][];
            for (int i = 0; i < newBoard.length; i++){
                newBoard[i] = Arrays.copyOf(this.board[i], this.board[i].length);
            }
            newBoard[this.playerRow][this.playerCol] = PLAYER_CHAR;
            newBoard[this.goalRow][this.goalCol] = GOAL_CHAR;
            String result = "";
            for (int i = 0; i < (newBoard[0].length * 2 + 3); i++){
                result = result + HORIZONTAL_BORDER_CHAR;
            }
            result = result + NEWLINE_CHAR;
            for (int i = 0; i < newBoard.length; i++){
                result = result + SIDE_BORDER_CHAR;    
                for (int j = 0; j < newBoard[i].length; j++){
                    result = result + " " + String.valueOf(newBoard[i][j]);
                }
                result = result + " " + SIDE_BORDER_CHAR + NEWLINE_CHAR;
            }
            for (int i = 0; i < (newBoard[0].length * 2 + 3); i++){
                result = result + HORIZONTAL_BORDER_CHAR;
            }
            return result + NEWLINE_CHAR;
        }


    @Override
        public boolean equals(Object other)
        {
            // TODO
            boolean result = false;
            if (other == null){
                return false;
            }
            if (other instanceof GameState){
                if (Arrays.deepEquals(this.board, ((GameState)other).board)){
                    if (this.playerRow == ((GameState)other).playerRow){
                        if (this.playerCol == ((GameState)other).playerCol){
                            if (this.goalRow == ((GameState)other).goalRow){
                                if (this.goalCol == ((GameState)other).goalCol){
                                    if (this.levelPassed == ((GameState)other).levelPassed){
                                        result = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return result;
        }

    public static void main(String[] args){
        GameState state  = new GameState(6, 5, 5, 0, 0, 4);
        state.moveUp();
        System.out.println(state.toString());
    }
}
