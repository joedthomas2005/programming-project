import game.*;
import game.rendering.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glGetError;

class Main{
    public static void main(String[] args) {
        Window window = new Window(1920, 1080, "The test", true, 1, 0, 0, 0);
        window.create();
        Input.enable(window);

        ShaderProgram shader = new ShaderProgram("Batch");
        shader.use();

        BatchedRenderer renderer = BatchedRenderer.SquareRenderer();
        OrthoCamera2D camera = new OrthoCamera2D(0, 0, window.getWidth(), window.getHeight(), shader.getID());
        camera.uploadProjectionUniform();

        Animator animator = new Animator();

        TextureAtlas martin = new TextureAtlas("res/elf.png", 3, 1, true);
        martin.load(shader.getID());

        GameObject elf = renderer.create(window.getWidth() / 2.0f, window.getHeight() / 2.0f, 0, 160, 280, 0, martin);
        Animation walk = new Animation(elf, 0.2f, 1, 3);
        Animation idle = new Animation(elf, 0.0f, 0);
        animator.addAnimation("elfWalk", walk);
        animator.addAnimation("elfIdle", idle);

        double time = glfwGetTime();
        double lastTime;
        double delta;
        animator.startAnimation("elfIdle");
        while(!window.shouldClose()){
            lastTime = time;
            time = glfwGetTime();
            delta = time - lastTime;

            animator.animate(time);

            if(Input.keyIsDown(GLFW_KEY_W)){
                elf.move((float) 0, (float) (delta) * 300);
            }
            if(Input.keyIsDown(GLFW_KEY_S)){
                elf.move((float) 0, (float) (-300 * delta));
            }
            if(Input.keyIsDown(GLFW_KEY_D)){
                elf.move((float) delta * 300, 0);
            }
            if(Input.keyIsDown(GLFW_KEY_A)){
                elf.move((float) (-300 * delta), 0);
            }

            if(Input.keyPressedDown(GLFW_KEY_W) || Input.keyPressedDown(GLFW_KEY_S) ||
            Input.keyPressedDown(GLFW_KEY_A) || Input.keyPressedDown(GLFW_KEY_D)){
                animator.startAnimation("elfWalk");
            } else if (!Input.keyIsDown(GLFW_KEY_W) && !Input.keyIsDown(GLFW_KEY_S) &&
                !Input.keyIsDown(GLFW_KEY_A) && !Input.keyIsDown(GLFW_KEY_D)){
                animator.startAnimation("elfIdle");
            }
            camera.uploadViewUniform();
            renderer.drawAll(false);
            window.update();
        }
        window.end();
    }
}