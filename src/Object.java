

import java.util.List;

public class Object {

    public String name, realName;
    public int x, y, angle, w, h;
    public String parent;

    public void setName(String name) {

        this.realName = name;
        this.name = name;
        this.name = name.replace(" ", "-");

        if (nameExists(name)) {

            autoNameChangeProtocol();
        }
    }

    private void autoNameChangeProtocol() {

        int index = 1;
        while (true) {

            String tempName = this.name + String.valueOf(index);
            System.out.println(tempName);

            if (!nameExists(tempName)) {
                // System.out.println("Only once");
                this.name = tempName;
                break;
            }
            index++;
        }
    }

    private boolean nameExists(String name) {

        for (Object object : MapEditor.objects) {

            if (object.name.equals(name))
                return true;
        }

        return false;
    }

    public boolean matches(int x, int y) {

        return this.x == x && this.y == y;
    }

    public String infoDump() {

        String info = "Name: " + name + "\nPosition: " + x + "," + y + "\nAngle: " + angle + "deg\nScale: " + w + ","
                + h + "\nParent: " + parent;

        return info;
    }
}
