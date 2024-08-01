import java.awt.Color;

/**
 * Represents a 3D square defined by three vertices and a color.
 */
public class Square {
    Vertex v1;  // First vertex of the square
    Vertex v2;  // Second vertex of the square
    Vertex v3;  // Third vertex of the square
    Vertex v4; // Fourth vertex of the square
    Color color; // Color of the square

    /**
     * Constructs a new square with the specified vertices and color.
     * 
     * @param v1 The first vertex
     * @param v2 The second vertex
     * @param v3 The third vertex
     * @param color The color of the square
     */
    public Square(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4; 
        this.color = color;
    }
}
