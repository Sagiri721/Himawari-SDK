
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public enum Components {

    Transform("Transform"),
    ImageRenderer("ImageRenderer"),
    Animator("Animator"),
    RectCollider("RectCollider"),
    Body("Body"),
    Camera("Camera");

    private String name;

    private Components(String value) {

        name = value;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {

        return "Details";
    }

    public static String[] fetchAll() {

        String[] result = new String[Components.values().length];

        int i = 0;
        for (Components c : Components.values()) {

            result[i] = c.name;
            i++;
        }

        return result;
    }

    public static Components getComponentbyString(String name) {

        for (Components c : Components.values()) {

            // System.out.println(name + " | " + c.getName() + " | " +
            // c.getName().equals(name));
            if (c.getName().equals(name))
                return c;
        }

        return null;
    }

    public static String[] getSnippet(int ind) {

        String snippet = "";
        String[] ret = new String[2];

        switch (ind) {

            case 0:
                // Transform
                int x = Integer.parseInt((JOptionPane.showInputDialog(null, "X Position?"))),
                        y = Integer.parseInt((JOptionPane.showInputDialog(null, "Y Position?")));

                snippet = "Transform transform = new Transform(new Vec2(" + x + ", " + y
                        + "), 0, new Vec2(1, 1));\naddComponent(transform);";

                ret[0] = snippet;
                ret[1] = "X: " + x + " | Y: " + y;
                return ret;
            case 1:
                // ImageRenderer
                JFileChooser fc = new JFileChooser(Project.engineFiles + "/Sprites");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int opt = fc.showDialog(null, "Choose");
                if (opt == JFileChooser.APPROVE_OPTION) {

                    snippet = "Sprite image = new Sprite(\"" + fc.getSelectedFile().getName()
                            + "\");\nImageRenderer renderer = new ImageRenderer(image);\naddComponent(renderer);";
                }

                ret[0] = snippet;
                ret[1] = "Image: " + fc.getSelectedFile().getName();
                return ret;
            case 2:
                // Animator

                break;
            case 3:
                // RectCollider
                int width = Integer.parseInt((JOptionPane.showInputDialog(null, "X Scale?"))),
                        height = Integer.parseInt((JOptionPane.showInputDialog(null, "Y Scale?")));

                snippet = "RectCollider collider = new RectCollider(transform, new Vec2(" + width + ", " + height
                        + "));\naddComponent(collider);";

                ret[0] = snippet;
                ret[1] = "Width: " + width + " | Height: " + height;
                return ret;
            case 4:
                // Body
                int mass = Integer.parseInt((JOptionPane.showInputDialog(null, "Mass?")));

                snippet = "Body body = new Body(transform, collider, " + mass + ");\naddComponent(body);";

                ret[0] = snippet;
                ret[1] = "Mass: " + mass;
                return ret;
            case 5:
                // Camera
                break;
        }

        return ret;
    }
}
