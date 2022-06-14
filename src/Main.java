import game.*;
import game.rendering.*;
import org.lwjgl.system.linux.UIO;
import org.w3c.dom.Text;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glGetError;

class Main{
    public static void main(String[] args) {
        Window window = new Window(1920, 1080, "The test", true, 1, 0, 0, 0);
        window.create();
        Input.enable(window);
        BatchedRenderer.InitializeRectRenderer();

        OrthoCamera2D camera = new OrthoCamera2D(0, 0, window.getWidth(), window.getHeight());
        Animator animator = new Animator();
        TextureAtlas martin = new TextureAtlas("res/elf.png", 3, 1, true);
        TextureAtlas chidi = new TextureAtlas("res/chidi.png", 1, 1, true);

        UIObject chidiFace = new UIObject(50, window.getHeight() - 50, 0, 100, 100, 0, chidi);
        System.out.println(chidiFace.isUI());
        System.out.println(chidiFace instanceof UIObject);
        GameObject elf = new GameObject(window.getWidth() / 2.0f, window.getHeight() / 2.0f, 0, 160, 280, 0, martin);
        System.out.println(elf.isUI());
//        System.out.println(elf i)
        GameObject redSquare = new GameObject(500, 500, 0, 100, 100, 1, 0, 0);

        animator.add(elf, "walk", 0.3f, 1, 3);
        animator.add(elf, "idle", 0, 0);
        double time = glfwGetTime();
        double lastTime;
        double delta;

        animator.start(elf, "idle");

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

            if(Input.keyIsDown(GLFW_KEY_RIGHT)){
                camera.move((float)delta * 300, 0);
            }
            if(Input.keyIsDown(GLFW_KEY_UP)){
                camera.move(0, (float)delta * 300);
            }
            if(Input.keyIsDown(GLFW_KEY_LEFT)){
                camera.move((float)delta * -300, 0);
            }
            if(Input.keyIsDown(GLFW_KEY_DOWN)){
                camera.move(0, (float)delta * -300);
            }

            if(Input.keyPressedDown(GLFW_KEY_W) || Input.keyPressedDown(GLFW_KEY_S) ||
            Input.keyPressedDown(GLFW_KEY_A) || Input.keyPressedDown(GLFW_KEY_D)){
                animator.start(elf, "walk");
            } else if (!Input.keyIsDown(GLFW_KEY_W) && !Input.keyIsDown(GLFW_KEY_S) &&
                !Input.keyIsDown(GLFW_KEY_A) && !Input.keyIsDown(GLFW_KEY_D)){
                animator.start(elf, "idle");
            }
            camera.update();
            BatchedRenderer.draw();
            window.update();
        }
        window.end();
    }
}