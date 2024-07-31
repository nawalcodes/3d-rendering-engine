import java.awt.Color;

/**
 * Represents a 3D triangle defined by three vertices and a color.
 */
public class Triangle {
    Vertex v1;  // First vertex of the triangle
    Vertex v2;  // Second vertex of the triangle
    Vertex v3;  // Third vertex of the triangle
    Color color; // Color of the triangle

    /**
     * Constructs a new Triangle with the specified vertices and color.
     * 
     * @param v1 The first vertex
     * @param v2 The second vertex
     * @param v3 The third vertex
     * @param color The color of the triangle
     */
    public Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
}
