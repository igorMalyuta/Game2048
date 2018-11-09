package task3513;

import java.util.*;

public class Model {

    int score;
    int maxTile;

    private Stack<Tile[][]> previousStates = new Stack();
    private Stack<Integer> previousScores = new Stack();
    private boolean isSaveNeeded = true;

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

    public Model() {
        resetGameTiles();
    }


    void autoMove() {
        PriorityQueue<MoveEfficiency> efficiencyQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        efficiencyQueue.offer(getMoveEfficiency(this::left));
        efficiencyQueue.offer(getMoveEfficiency(this::right));
        efficiencyQueue.offer(getMoveEfficiency(this::up));
        efficiencyQueue.offer(getMoveEfficiency(this::down));

        efficiencyQueue.poll().getMove().move();
    }

    MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency efficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);

        if(hasBoardChanged()) {
            rollback();
            return efficiency;
        }
        else {
            rollback();
            return new MoveEfficiency(-1,0, move);
        }
    }

    boolean hasBoardChanged() {
        int currentWeight = 0;
        int savedWeight = 0;

        for (Tile[] tiles : gameTiles)
            for (Tile tile : tiles)
                currentWeight += tile.value;

        for (Tile[] tiles : previousStates.peek())
            for (Tile tile : tiles)
                savedWeight += tile.value;

        return currentWeight != savedWeight;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] state = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i < state.length; i++)
            for (int j = 0; j < state[i].length; j++) {
                int value = tiles[i][j].value;
                state[i][j] = new Tile(value);
            }

        previousStates.push(state);
        previousScores.push(score);

        isSaveNeeded = false;
    }

    public void rollback() {
        if(previousScores.empty() || previousStates.empty())
            return;

        gameTiles = previousStates.pop();
        score = previousScores.pop();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 1; j < gameTiles[i].length; j++) {
                //there is an empty cell
                if(gameTiles[i][j].isEmpty())
                    return true;

                //current cell is equal to the cell to the right
                if(gameTiles[i][j].value == gameTiles[i][j-1].value)
                    return true;

                //the current cell is equal to the cell above
                if(i > 0 && gameTiles[i][j].value == gameTiles[i-1][j].value)
                    return true;
            }
        }

        return false;
    }

    void randomMove() {
        int choose = ((int) (Math.random() * 100)) % 4;
        switch (choose) {
            case 0: left();
                    break;

            case 1: right();
                break;

            case 2: up();
                break;

            case 3: down();
        }
    }

    protected void up() {
        saveState(gameTiles);

        copy(clockwiseRotation());
        copy(clockwiseRotation());
        copy(clockwiseRotation());

        left();

        copy(clockwiseRotation());
    }

    protected void down() {
        saveState(gameTiles);

        copy(clockwiseRotation());

        left();

        copy(clockwiseRotation());
        copy(clockwiseRotation());
        copy(clockwiseRotation());
    }

    protected void right() {
        saveState(gameTiles);

        copy(clockwiseRotation());
        copy(clockwiseRotation());

        left();

        copy(clockwiseRotation());
        copy(clockwiseRotation());
    }

    /**
     * Moved tiles left
     */
    protected void left() {
        if(isSaveNeeded)
            saveState(gameTiles);

        boolean changed = false;

        for (Tile[] tiles : gameTiles)
            if(compressTiles(tiles) | mergeTiles(tiles))
                changed = true;

        if(changed)
            addTile();

        isSaveNeeded = true;
    }

    private void copy(Tile[][] source) {
        for (int i = 0; i < gameTiles.length; i++)
            for (int j = 0; j < gameTiles[i].length; j++)
                gameTiles[i][j] = source[i][j];
    }

    private Tile[][] clockwiseRotation() {
        int m = gameTiles.length;
        int n = gameTiles[0].length;
        Tile[][] rotated = new Tile[n][m];

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                rotated[j][m - 1 - i] = gameTiles[i][j];

        return rotated;
    }

    private void addTile() {
        List<Tile> tileList = getEmptyTiles();
        if(tileList.isEmpty())
            return;

        Tile tile = tileList.get((int)(tileList.size() * Math.random()));
        tile.value = (Math.random() < 0.9 ? 2 : 4);
    }

    private boolean compressTiles(Tile[] tiles) {
        Tile[] toCompare = Arrays.copyOf(tiles, tiles.length);

        Arrays.sort(tiles, (t1, t2) -> {
            int v1 = t1.value;
            int v2 = t2.value;

            if(v1 == 0)
                return 1;

            if(v2 == 0)
                return -1;

            return 0;
        });

        return !Arrays.deepEquals(tiles, toCompare);
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean changed = false;

        int value = tiles[0].value;
        for (int i = 1; i < tiles.length; i++) {
            int next = tiles[i].value;
            if(value == next && value != 0) {
                tiles[i - 1].value *= 2;
                changed = true;

                int weight = tiles[i-1].value;
                score += weight;

                if(maxTile < weight)
                    maxTile = weight;

                tiles[i].value = 0;
                compressTiles(tiles);
                value = tiles[i].value;
            }
            else
                value = next;
        }

        return changed;
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> freeTiles = new ArrayList<>();

        for (Tile[] gameTile : gameTiles)
            for (Tile aGameTile : gameTile)
                if (aGameTile.isEmpty())
                    freeTiles.add(aGameTile);

        return freeTiles;
    }

    void resetGameTiles() {
        score = 0;
        maxTile = 0;

        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                gameTiles[i][j] = new Tile();

        addTile();
        addTile();
    }
}
