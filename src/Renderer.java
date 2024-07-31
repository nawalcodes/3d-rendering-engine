import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the 3D rendering engine.
 */
public class Renderer {

    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Engine"); // Create a new JFrame
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout()); // Use BorderLayout for the container

        // Slider to control horizontal rotation
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // Slider to control vertical rotation
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        // Panel to display render results
        JPanel renderPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g); // Call superclass method to ensure proper painting
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK); // Set background color
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Create a list of triangles to render
                List<Triangle> tris = new ArrayList<>();
                tris.add(new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, 100, -100),
                        Color.WHITE));
                tris.add(new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, -100),
                        Color.RED));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(100, 100, 100),
                        Color.GREEN));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(-100, -100, 100),
                        Color.BLUE));

                // Calculate transformation matrices
                double heading = Math.toRadians(headingSlider.getValue());
                Matrix3 headingTransform = new Matrix3(new double[]{
                        Math.cos(heading), 0, Math.sin(heading),
                        0, 1, 0,
                        -Math.sin(heading), 0, Math.cos(heading)
                });
                double pitch = Math.toRadians(pitchSlider.getValue());
                Matrix3 pitchTransform = new Matrix3(new double[]{
                        1, 0, 0,
                        0, Math.cos(pitch), Math.sin(pitch),
                        0, -Math.sin(pitch), Math.cos(pitch)
                });
                Matrix3 transform = headingTransform.multiply(pitchTransform);

                // Initialize the image and z-buffer
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY; // Initialize z-buffer with very far values
                }

                // Render each triangle
                for (Triangle t : tris) {
                    // Transform vertices
                    Vertex v1 = transform.transform(t.v1);
                    Vertex v2 = transform.transform(t.v2);
                    Vertex v3 = transform.transform(t.v3);

                    // Manually translate vertices to screen coordinates
                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;

                    // Calculate normal vector for the triangle
                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    Vertex norm = new Vertex(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x
                    );
                    double normalLength =
                            Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    double angleCos = Math.abs(norm.z); // Cosine of the angle with the view direction

                    // Compute rectangular bounds for the triangle
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea =
                            (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    // Rasterize the triangle
                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            double b1 =
                                    ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 =
                                    ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 =
                                    ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    // Calculate shaded color based on normal angle
                                    Color shadedColor = getShade(t.color, angleCos);
                                    img.setRGB(x, y, shadedColor.getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }
                }

                // Draw the rendered image
                g2.drawImage(img, 0, 0, null);
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);

        // Add listeners to update rendering when sliders change
        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Adjusts the color shade based on the angle with the view direction.
     * 
     * @param color The original color
     * @param shade The shading factor (0 to 1)
     * @return The shaded color
     */
    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed() / 255.0, 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen() / 255.0, 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue() / 255.0, 2.4) * shade;

        int red = (int) (Math.pow(redLinear, 1 / 2.4) * 255);
        int green = (int) (Math.pow(greenLinear, 1 / 2.4) * 255);
        int blue = (int) (Math.pow(blueLinear, 1 / 2.4) * 255);

        return new Color(red, green, blue);
    }
}
