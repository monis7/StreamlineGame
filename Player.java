/* TODO 
 * Author: Mohammed Abdoul
 * Login: cs8bsp19aa
 * Email: mabdoul@ucsd.edu
 * References: CSE 8B website
 * This file contains the class Player that defines 
 * the player on the Gui game board
 * */
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/* TODO 
 * This class contains methods that define the 
 * player on the Gui game board
 * */
public class Player extends RoundedSquare {
    final static double STROKE_FRACTION = 0.1;

    /* TODO 
     * @param none
     * @return none
     * Player constructor that creates a shape object 
     * representing the player on the board
     * */
    public Player() {
        /* TODO
           set a fill color, a stroke color, and set the stroke type to
           centered
           */
        this.setFill(Color.GAINSBORO);
        this.setStroke(Color.BLACK);
        this.setStrokeType(StrokeType.CENTERED);
    }

    /* TODO 
     * @param double size size of the Player rectangle
     * @return none
     * sets the size of the player rectangle to the given input
     * */
    @Override
        public void setSize(double size) {
            /* TODO
               1. update the stroke width based on the size and 
               STROKE_FRACTION
               2. call super setSize(), bearing in mind that the size
               parameter we are passed here includes stroke but the
               superclass's setSize() does not include the stroke
               */
            this.setStrokeWidth(size*STROKE_FRACTION);
            super.setSize(size);
        }
}
