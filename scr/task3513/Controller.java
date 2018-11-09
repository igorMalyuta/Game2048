package task3513;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {

    private static final int WINNING_TILE = 2048;
    private boolean isGameWon = false;
    private boolean isGameLost = false;

    private Model model;
    private View view;

    public Controller(Model model) {
        this.model = model;
        view = new View(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(!model.canMove()) {
            isGameLost = true;
            view.showLoseMess();
            resetGame();
        }
        else
            if(model.maxTile == WINNING_TILE) {
                isGameWon = true;
                view.showWinMess();
                resetGame();
        }

        if(!isGameLost && !isGameWon) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    resetGame();
                    break;

                case KeyEvent.VK_A:
                    model.autoMove();
                    break;

                case KeyEvent.VK_R:
                    model.randomMove();
                    break;

                case KeyEvent.VK_Z:
                    model.rollback();
                    break;

                case KeyEvent.VK_LEFT:
                    model.left();
                    break;

                case KeyEvent.VK_RIGHT:
                    model.right();
                    break;

                case KeyEvent.VK_UP:
                    model.up();
                    break;

                case KeyEvent.VK_DOWN:
                    model.down();
            }
        }

        view.repaint();
    }

    public void resetGame() {
        isGameLost = false;
        isGameWon = false;

        model.resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore() {
        return model.score;
    }

    public View getView() {
        return view;
    }
}
