import game.*;
import game.rendering.Animator;
import game.rendering.BatchedRenderer;
import game.rendering.OrthoCamera2D;
import game.rendering.ShaderProgram;

class Main{
    public static void main(String[] args) {
        Window window = new Window(1920, 1080, "The test", true, 1, 0, 0, 0);
        window.create();

        ShaderProgram shader = new ShaderProgram("Batch");
        shader.create();
        shader.use();

        BatchedRenderer renderer = BatchedRenderer.SquareRenderer();
        OrthoCamera2D camera = new OrthoCamera2D(0, 0, 1920, 1080, shader.getID());
        camera.uploadProjectionUniform();

        Animator animator = new Animator();


    }
}