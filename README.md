# 3D Graphics Engine with Java

## Description

Below is a simple 3D graphics engine implemented in Java. It uses basic rendering techniques, such as orthographic projection, simple triangle rasterization, z-buffering and flat shading, to display a 3D object on a 2D canvas, with controls for horizontal and vertical rotation and zooming in and out to examine the object more closely. The engine demonstrates the use of basic three-dimensional transformations and shading techniques.

## Features

- **3D Rendering**: Renders a 3D cube with basic flat shading.
- **Rotation Controls**: Adjust horizontal and vertical rotation using sliders.
- **Shading**: Applies basic shading based on the angle of the normal.
- **Magnification**: Zooms in and out of the object using buttons. 

## Project Structure

- `Renderer.java`: The main class that sets up the GUI, handles rendering, and controls the application.
- `Vertex.java`: Defines a vertex with 3D coordinates.
- `Square.java`: Defines a square using four vertices and a specified color.
- `Matrix3.java`: Provides methods for 3x3 matrix transformations and multiplication.

## Getting Started

### Prerequisites

- JDK 8 or higher
- Any Java IDE (e.g., IntelliJ IDEA, Eclipse) or a text editor with command line tools

### Building and Running

1. **Clone the repository:**

    ```bash
    git clone https://github.com/yourusername/your-repository.git
    ```

2. **Navigate to the project directory:**

    ```bash
    cd your-repository
    ```

3. **Compile the Java files:**

    ```bash
    javac -d bin src/*.java
    ```

4. **Run the application:**

    ```bash
    java -cp bin Renderer
    ```

## Usage

- **Rotation Controls**: Use the horizontal slider to adjust the heading (yaw) and the vertical slider to adjust the pitch.
- **Rendering**: The rendered 3D shapes will be displayed in the central panel.

## Customization

- **Background Color**: Modify the `paintComponent` method in `Renderer.java` to change the background color of the frame.
- **Triangle Data**: Update the `square` list in `Renderer.java` to render different sets of squares.

## Contributing

Feel free to submit issues or pull requests. Please follow the project's coding standards and guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [Java Swing Documentation](https://docs.oracle.com/javase/8/docs/api/javax/swing/package-summary.html) for GUI components.
- [Java 2D API Documentation](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics2D.html) for rendering and graphics operations.
- [This blog post](http://blog.rogach.org/2015/08/how-to-create-your-own-simple-3d-render.html) for explaining the basics of 3D rendering techniques. 
