package Components.Structs;

public class Map {

    public TileSet tileSet;
    public int w, h;

    public Map(TileSet tileSet, int w, int h) {
        this.tileSet = tileSet;

        this.w = w;
        this.h = h;
    }
}
