/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameGUI;

import Entities.Entity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class MapGUI extends JPanel {

    private static final Color GOBLIN_COLOR = new Color(153, 0, 0); //GREEN
    private static final Color HEALER_COLOR = new Color(153, 255, 153); //PALE GREEN
    private static final Color TREASURE_COLOR = new Color(255, 230, 0); //GOLD
    private static final Color TRAP_COLOR = new Color(153, 56, 0); //BROWN
    private static final Color PLAYER_COLOR = new Color(0, 128, 255); //BLUE
    private static final Color TERRAIN_COLOR = new Color(255, 204, 102);

    private HashMap<Integer, Color> colorPalette;

    private final int NUM_ROWS;
    private final int NUM_COLUMNS;
    private static final int PREFERRED_GRID_SIZE_PIXELS = 50;

    private Color[][] tileMap;

    public MapGUI(int num_rows, int num_cols) {
        this.NUM_ROWS = num_rows;
        this.NUM_COLUMNS = num_cols;
        setupColorPalette();
        tileMap = new Color[NUM_ROWS][NUM_COLUMNS];
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                this.tileMap[i][j] = Color.BLACK;
            }
        }
        int preferredWidth = NUM_COLUMNS * PREFERRED_GRID_SIZE_PIXELS;
        int preferredHeight = NUM_ROWS * PREFERRED_GRID_SIZE_PIXELS;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));

    }

    private void setupColorPalette() {
        this.colorPalette = new HashMap<>();
        colorPalette.put(Entity.GOBLIN_TRACKS, GOBLIN_COLOR);
        colorPalette.put(Entity.HEALER_TRACKS, HEALER_COLOR);
        colorPalette.put(Entity.GLITTER, TREASURE_COLOR);
        colorPalette.put(Entity.TRAP_TRACKS, TRAP_COLOR);
        colorPalette.put(Entity.PLAYER_TRACKS, PLAYER_COLOR);
        colorPalette.put(Entity.NO_TRACKS, Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        // Important to call super class method
        super.paintComponent(g);
        // Clear the board
        g.clearRect(0, 0, getWidth(), getHeight());
        // Draw the grid
        int rectWidth = getWidth() / NUM_COLUMNS;
        int rectHeight = getHeight() / NUM_ROWS;

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                // Upper left corner of this terrain rect
                int x = i * rectWidth;
                int y = j * rectHeight;
                Color terrainColor = tileMap[i][j];
                g.setColor(terrainColor);
//                g.fillRect(x, y, rectWidth, rectHeight);
                g.fillRect(x, getHeight() - y - rectHeight, rectWidth, rectHeight);
            }
        }
    }

    public void updateMapGUI(Entity[][] mapMatrix) {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                this.tileMap[i][j] = this.colorPalette.get(mapMatrix[i][j].getType());
            }
        }
        this.repaint();
    }

}
