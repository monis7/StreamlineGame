/* TODO 
 * Author: Mohammed Abdoul
 * Login: cs8bsp19aa
 * Email: mabdoul@ucsd.edu
 * References: CSE 8B website
 * This file contains the class GuiStreamline
 * for psa6 in CSE 8B
 * */
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.util.Duration;

/* TODO 
 * this class GuiStreamline
 * provides the frontend functionality for the game of Streamline
 * */
public class GuiStreamline extends Application {
    static final double MAX_SCENE_WIDTH = 600;
    static final double MAX_SCENE_HEIGHT = 600;
    static final double PREFERRED_SQUARE_SIZE = 100;
    static final double MIDDLE_OFFSET = 0.5;
    static final double SCALE_TIME = 175;  // milliseconds for scale animation
    static final double FADE_TIME = 250;   // milliseconds for fade animation
    static final double DOUBLE_MULTIPLIER = 2;

    static final String TITLE = "CSE 8b Streamline GUI";
    static final String USAGE = 
        "Usage: \n" + 
        "> java GuiStreamline               - to start a game with default" +
        " size 6*5 and random obstacles\n" + 
        "> java GuiStreamline <filename>    - to start a game by reading g" +
        "ame state from the specified file\n" +
        "> java GuiStreamline <directory>   - to start a game by reading a" +
        "ll game states from files in\n" +
        "                                     the specified directory and " +
        "playing them in order\n";
    static final char TRAIL_CHAR = '+';
    static final char OBSTACLE_CHAR = 'O';
    static final Color TRAIL_COLOR = Color.PALEVIOLETRED;
    static final Color GOAL_COLOR = Color.MEDIUMAQUAMARINE;
    static final Color OBSTACLE_COLOR = Color.DIMGRAY;

    // Trail radius will be set to this fraction of the size of a board square.
    static final double TRAIL_RADIUS_FRACTION = 0.1;

    // Squares will be resized to this fraction of the size of a board square.
    static final double SQUARE_FRACTION = 0.8;

    Stage mainStage;
    Scene mainScene;
    Group levelGroup;                   // For obstacles and trails
    Group rootGroup;                    // Parent group for everything else
    Player playerRect;                  // GUI representation of the player
    RoundedSquare goalRect;             // GUI representation of the goal

    Shape[][] grid;                     // Same dimensions as the game board

    Streamline game;                    // The current level
    ArrayList<Streamline> nextGames;    // Future levels

    MyKeyHandler myKeyHandler;          // for keyboard input

    /** 
     * Coverts the given board column and row into scene coordinates.
     * Gives the center of the corresponding tile.
     * 
     * @param boardCol a board column to be converted to a scene x
     * @param boardRow a board row to be converted to a scene y
     * @return scene coordinates as length 2 array where index 0 is x
     */
    public double[] boardIdxToScenePos (int boardCol, int boardRow) {
        double sceneX = ((boardCol + MIDDLE_OFFSET) * 
                (mainScene.getWidth() - 1)) / getBoardWidth();
        double sceneY = ((boardRow + MIDDLE_OFFSET) * 
                (mainScene.getHeight() - 1)) / getBoardHeight();
        return new double[]{sceneX, sceneY};
    }

    /* TODO */
    public int getBoardWidth() {
        /* TODO
           Should return the width of the current level's board
           */
        return game.currentState.board[0].length; 
    }

    /* TODO */
    public int getBoardHeight() {
        /* TODO
           Should return the height of the current level's board
           */
        return game.currentState.board.length;
    }

    /* TODO 
     * @param none
     * @return double value for size of a single square on the board
     * */
    public double getSquareSize() {
        /* TODO
           Find a size for a single square of the board that will fit nicely
           in the current scene size.
           For example, given a scene size of 1000 by 600 and a board size
           of 5 by 6, we have room for each square to be 200x100. Since we
           want squares, return the minimum, which is 100 in this example.
           */
        double x = mainScene.getWidth()/getBoardWidth();
        double y = mainScene.getHeight()/getBoardHeight();

        if (x>y){
            return y;
        }
        else{
            return x; 
        }
    }

    /* TODO 
     * @param none
     * @return none
     * resets the Grid
     * */
    public void resetGrid() {
        /* TODO
           Destroy and recreate grid and all trail and obstacle shapes.
           Assumes the dimensions of the board may have changed.
Hints: Empty out levelGroup and recreate grid.
Also should make sure grid is the right size, in case the size of the
board changed.
*/
        levelGroup.getChildren().clear();
        grid = new Shape[getBoardHeight()][getBoardWidth()];

        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[0].length; j++){
                double[] shapePos = boardIdxToScenePos(j, i);
                if (game.currentState.board[i][j]==OBSTACLE_CHAR){
                    grid[i][j] = new RoundedSquare(shapePos[0], shapePos[1], 
                            getSquareSize()*SQUARE_FRACTION);
                    grid[i][j].setFill(OBSTACLE_COLOR);
                }
                else{
                    grid[i][j] = new Circle(shapePos[0], shapePos[1], 
                            getSquareSize()*TRAIL_RADIUS_FRACTION);
                    grid[i][j].setFill(Color.TRANSPARENT);
                }



                levelGroup.getChildren().add(grid[i][j]); 
            }
        }
    }

    /* TODO 
     * @param none
     * @return none
     * updates trail colors to reflect movement
     * */
    public void updateTrailColors() {
        /* TODO
           Sets the fill color of all trail Circles making them visible or not
           depending on if that board position equals TRAIL_CHAR.
           */
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[0].length; j++){
                if (game.currentState.board[i][j]==TRAIL_CHAR){
                    grid[i][j].setFill(TRAIL_COLOR);
                }
                if (game.currentState.board[i][j]== ' '){
                    grid[i][j].setFill(Color.TRANSPARENT);
                }


            }
        }
    }

    /* TODO 
     * @param int fromCol original column position of player 
     * @param int fromRow original row position of player
     * @param int toCol final column position of player after move
     * @param int toRow final row position of player after move
     * @return none
     * updates the Gui to reflect movement
     * */
    public void onPlayerMoved(int fromCol, int fromRow, int toCol, int toRow)
    {
        /* TODO
           Makes trail markers visible and changes player position.
           To be called when the user moved the player and the GUI needs to be 
           updated to show the new position.
           Parameters are the old position and new position
           */
        if (fromCol == toCol && fromRow == toRow) {
            return;
        }


        this.updateTrailColors();
        double[] playerPos = boardIdxToScenePos(
                toCol, toRow
                );
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);

    }

    /* TODO 
     * @param KeyCode keyCode of input
     * @return none
     * tells the Gui how to handle specific keyboard inputs
     * */
    void handleKeyCode(KeyCode keyCode)
    {
        /* TODO */
        int fromRow = game.currentState.playerCol;
        int fromCol = game.currentState.playerRow;
        switch (keyCode) {
            /* TODO 
               Write the other cases here (what are all the valid inputs?)
               */
            case UP:
                game.recordAndMove(Direction.UP);
                break;
            case DOWN:
                game.recordAndMove(Direction.DOWN);
                break;
            case LEFT:
                game.recordAndMove(Direction.LEFT);
                break;
            case RIGHT:
                game.recordAndMove(Direction.RIGHT);
                break;

            case U:
                game.undo();
                break;
            case O:
                game.saveToFile();
                break;

            case Q:
                System.exit(0);
                break;
            default:
                System.out.println("Possible commands:\n w - up\n " + 
                        "a - left\n s - down\n d - right\n u - undo\n " + 
                        "q - quit level");
                break;
        }

        /* TODO
           Call onPlayerMoved() to update the GUI to reflect the player's 
           movement (if any)
           */
        this.onPlayerMoved(fromCol, fromRow, game.currentState.playerCol,
                game.currentState.playerRow);
        if(game.currentState.levelPassed==true){
            this.onLevelFinished();
        }

    }

    /**
     * This nested class handles keyboard input and calls handleKeyCode()
     */
    class MyKeyHandler implements EventHandler<KeyEvent>
    {
        /* TODO 
         * @param KeyEvent e input from keyboard
         * @return none
         * handles keyboard input using handleKeyCode()
         * */
        public void handle(KeyEvent e)
        {
            /* TODO */
            handleKeyCode(e.getCode());
        }
    }

    /* TODO 
     * @param none
     * @return none
     * sets up the Gui when level is loaded
     * */
    public void onLevelLoaded()
    {
        resetGrid();

        double squareSize = getSquareSize() * SQUARE_FRACTION;

        // Update the player position
        double[] playerPos = boardIdxToScenePos(
                game.currentState.playerCol, game.currentState.playerRow
                );
        playerRect.setSize(squareSize);
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);

        /* TODO 
           What else should be updated in the UI when starting a new level?
           */
        double[] goalPos = boardIdxToScenePos(
                game.currentState.goalCol, game.currentState.goalRow
                );
        goalRect.setSize(squareSize);
        goalRect.setCenterX(goalPos[0]);
        goalRect.setCenterY(goalPos[1]);
    }

    /** 
     * @param none 
     * @return none
     * Called when the player reaches the goal. Shows the winning animation
     * and loads the next level if there is one.
     */
    public void onLevelFinished() {
        // Clone the goal rectangle and scale it up until it covers the screen

        // Clone the goal rectangle
        Rectangle animatedGoal = new Rectangle(
                goalRect.getX(),
                goalRect.getY(),
                goalRect.getWidth(),
                goalRect.getHeight()
                );
        animatedGoal.setFill(goalRect.getFill());


        // Scope for children
        {
            // Add the clone to the scene
            List<Node> children = rootGroup.getChildren();
            children.add(children.indexOf(goalRect), animatedGoal);
        }

        // Create the scale animation
        ScaleTransition st = new ScaleTransition(
                Duration.millis(SCALE_TIME), animatedGoal
                );
        st.setInterpolator(Interpolator.EASE_IN);

        // Scale enough to eventually cover the entire scene
        st.setByX(DOUBLE_MULTIPLIER * 
                mainScene.getWidth() / animatedGoal.getWidth());
        st.setByY(DOUBLE_MULTIPLIER * 
                mainScene.getHeight() / animatedGoal.getHeight());

        /*
         * This will be called after the scale animation finishes.
         * If there is no next level, quit. Otherwise switch to it and
         * fade out the animated cloned goal to reveal the new level.
         */
        st.setOnFinished(e1 -> {
                /* TODO
                   check if there is no next game and if so, quit 
                   update the instances variables game and nextGames 
                   to switch to the next level
                   */
                rootGroup.getChildren().remove(animatedGoal);
                if (nextGames.isEmpty()==true){
                System.exit(0);
                }
                this.game = this.nextGames.get(0);
                this.nextGames.remove(0);


                // DO NOT MODIFY ANYTHING BELOW THIS LINE IN THIS METHOD

                // Update UI to the next level, but it won't be visible yet
                // because it's covered by the animated cloned goal
                onLevelLoaded();

                Rectangle fadeRect = new Rectangle(0, 0, 
                    mainScene.getWidth(), mainScene.getHeight());
                fadeRect.setFill(goalRect.getFill());

                // Scope for children
                {
                    // Add the fading rectangle to the scene
                    List<Node> children = rootGroup.getChildren();
                    children.add(children.indexOf(goalRect), fadeRect);
                }

                FadeTransition ft = new FadeTransition(
                        Duration.millis(FADE_TIME), fadeRect
                        );
                ft.setFromValue(1);
                ft.setToValue(0);

                // Remove the cloned goal after it's finished fading out
                ft.setOnFinished(e2 -> {
                        rootGroup.getChildren().remove(fadeRect); 
                        });


                // Start the fade-out now
                ft.play();
        });

        // Start the scale animation
        st.play();
    }

    /** 
     * @param none
     * @return none
     * Performs file IO to populate game and nextGames using filenames from
     * command line arguments.
     */
    public void loadLevels() {
        game = null;
        nextGames = new ArrayList<Streamline>();

        List<String> args = getParameters().getRaw();
        if (args.size() == 0) {
            System.out.println("Starting a default-sized random game...");
            game = new Streamline();
            return;
        }

        // at this point args.length == 1

        File file = new File(args.get(0));
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting...", 
                    args.get(0));
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s...\n", 
                    args.get(0));
            game = new Streamline(args.get(0));
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        Arrays.sort(subfiles);
        for (int i=0; i<subfiles.length; i++) {
            File subfile = subfiles[i];

            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create a new game for each file, and add it to nextGames
            System.out.printf("Loading game %d/%d from file %s...\n",
                    i+1, subfiles.length, subfile.toString());
            nextGames.add(new Streamline(subfile.toString()));
        }

        // Switch to the first level
        game = nextGames.get(0);
        nextGames.remove(0);
    }

    /**
     * The main entry point for all JavaFX Applications
     * Initializes instance variables, creates the scene, and sets up the UI
     * 
     * @param  primaryStage The window for this application
     * @throws Exception    [description]
     */
    public void start(Stage primaryStage) throws Exception {
        // Populate game and nextGames
        loadLevels();

        // Initialize the scene and our groups
        rootGroup = new Group();
        mainScene = new Scene(rootGroup, MAX_SCENE_WIDTH, MAX_SCENE_HEIGHT, 
                Color.GAINSBORO);
        levelGroup = new Group();
        rootGroup.getChildren().add(levelGroup);

        /* TODO
           initialize goalRect and playerRect, add them to rootGroup,
           call onLevelLoaded(), and set up keyboard input handling
           */
        this.goalRect = new RoundedSquare();
        this.goalRect.setFill(GOAL_COLOR);
        this.playerRect = new Player();
        rootGroup.getChildren().add(goalRect);
        rootGroup.getChildren().add(playerRect);
        this.onLevelLoaded();
        myKeyHandler = new MyKeyHandler();
        mainScene.setOnKeyPressed(myKeyHandler);
        // Make the scene visible
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    /**
     * Execution begins here, but at this point we don't have a UI yet
     * The only thing to do is call launch() which will eventually result in
     * start() above being called.
     * 
     * @param args The arguments from the command line. Should be one 
     *             argument at most which would be the level/directory to load.
     */
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.print(USAGE);
            return;
        }

        launch(args);
    }
}
