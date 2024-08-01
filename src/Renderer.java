import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private static double zoomFactor = 1.0; // Initial zoom factor

    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Engine");
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // Slider to control horizontal rotation
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // Slider to control vertical rotation
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        // Buttons for zooming in and out
        JPanel buttonPanel = new JPanel();
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        pane.add(buttonPanel, BorderLayout.NORTH);

        // Panel to display render results
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                List<Square> squares = createCube();

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

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }

                for (Square s : squares) {
                    Vertex v1 = transform.transform(s.v1);
                    Vertex v2 = transform.transform(s.v2);
                    Vertex v3 = transform.transform(s.v3);
                    Vertex v4 = transform.transform(s.v4);

                    // Apply zoom factor
                    v1.x *= zoomFactor;
                    v1.y *= zoomFactor;
                    v2.x *= zoomFactor;
                    v2.y *= zoomFactor;
                    v3.x *= zoomFactor;
                    v3.y *= zoomFactor;
                    v4.x *= zoomFactor;
                    v4.y *= zoomFactor;

                    // Manually translate vertices
                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;
                    v4.x += getWidth() / 2;
                    v4.y += getHeight() / 2;

                    // Draw each square as two triangles
                    drawSquareAsTriangles(v1, v2, v3, v4, s.color, img, zBuffer);
                }

                g2.drawImage(img, 0, 0, null);
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());

        // Add action listeners for zoom buttons
        zoomInButton.addActionListener(e -> {
            zoomFactor *= 1.1; // Increase zoom factor
            renderPanel.repaint();
        });
        zoomOutButton.addActionListener(e -> {
            zoomFactor /= 1.1; // Decrease zoom factor
            renderPanel.repaint();
        });

        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static List<Square> createCube() {
        List<Square> squares = new ArrayList<>();
        // Define vertices for the cube
        Vertex[] vertices = {
            new Vertex(100, 100, 100),
            new Vertex(-100, 100, 100),
            new Vertex(-100, -100, 100),
            new Vertex(100, -100, 100),
            new Vertex(100, 100, -100),
            new Vertex(-100, 100, -100),
            new Vertex(-100, -100, -100),
            new Vertex(100, -100, -100)
        };

        // Define the squares of the cube
        squares.add(new Square(vertices[0], vertices[1], vertices[2], vertices[3], Color.WHITE));
        squares.add(new Square(vertices[4], vertices[5], vertices[6], vertices[7], Color.RED));
        squares.add(new Square(vertices[0], vertices[1], vertices[5], vertices[4], Color.GREEN));
        squares.add(new Square(vertices[2], vertices[3], vertices[7], vertices[6], Color.BLUE));
        squares.add(new Square(vertices[0], vertices[3], vertices[7], vertices[4], Color.YELLOW));
        squares.add(new Square(vertices[1], vertices[2], vertices[6], vertices[5], Color.CYAN));

        return squares;
    }

    private static void drawSquareAsTriangles(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Color color, BufferedImage img, double[] zBuffer) {
        // Draw the square as two triangles
        drawTriangle(v1, v2, v3, color, img, zBuffer);
        drawTriangle(v1, v3, v4, color, img, zBuffer);
    }

    private static void drawTriangle(Vertex v1, Vertex v2, Vertex v3, Color color, BufferedImage img, double[] zBuffer) {
        // Calculate normal vector and depth shading
        Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
        Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
        Vertex norm = new Vertex(
                ab.y * ac.z - ab.z * ac.y,
                ab.z * ac.x - ab.x * ac.z,
                ab.x * ac.y - ab.y * ac.x
        );
        double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
        norm.x /= normalLength;
        norm.y /= normalLength;
        norm.z /= normalLength;
        double angleCos = Math.abs(norm.z);

        // Compute rectangular bounds for triangle
        int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
        int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
        int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
        int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

        double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                    double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                    int zIndex = y * img.getWidth() + x;
                    if (zBuffer[zIndex] < depth) {
                        Color shadedColor = getShade(color, angleCos);
                        img.setRGB(x, y, shadedColor.getRGB());
                        zBuffer[zIndex] = depth;
                    }
                }
            }
        }
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
        return new Color(
            (int) (255.0 * Math.pow(redLinear, 1.0 / 2.4)),
            (int) (255.0 * Math.pow(greenLinear, 1.0 / 2.4)),
            (int) (255.0 * Math.pow(blueLinear, 1.0 / 2.4))
        );
    }
}
