package Components.Structs;

public class Object {

    public String name;
    public int x, y, angle, w, h;

    public boolean matches(int x, int y) {

        return this.x == x && this.y == y;
    }

    public String infoDump() {

        String info = "Name: " + name + "\nPosition: " + x + "," + y + "\nAngle: " + angle + "deg\nScale: " + w + ","
                + h;

        return info;
    }
}