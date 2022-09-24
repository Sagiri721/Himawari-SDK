import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TileSet {

    public int size;

    public BufferedImage tileSet;
    public BufferedImage[] sprites;

    int sizeY = 0;

    public TileSet(String path, int size) {

        this.size = size;

        try {
            tileSet = ImageIO.read(new File(path));
        } catch (IOException e) {

            System.out.println("Couldn't find your file");
            e.printStackTrace();
        }

        int sizeX = tileSet.getWidth() / size;
        sizeY = tileSet.getHeight() / size;

        sprites = new BufferedImage[sizeX * sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                sprites[j + (i * sizeY)] = tileSet.getSubimage(i * size, j * size, size, size);
            }
        }
    }

    public BufferedImage getTileOf(int x, int y) {

        return sprites[x + (y * sizeY)];
    }

    public BufferedImage getTileOf(int y) {

        return sprites[y];
    }
}
