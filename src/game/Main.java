package game;

import game.engine.Input;
import game.engine.Window;
import game.engine.rendering.*;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

class Main{
    public static void main(String[] args) {
        Window window = new Window(3840, 2400, "Stardew Valley", true, 1, 0, 0, 0);
        Input.enable(window);
        OrthographicCamera2D camera = new OrthographicCamera2D(-1920/2.0f, -1080/2.0f, 1920, 1080);
        BatchedRenderer.initializeRectRenderer(camera);
        Animator animator = new Animator();
        TextureAtlas martin = new TextureAtlas("res/elf.png", 3, 1, true);
        TextureAtlas dirt = new TextureAtlas("res/StardewDirt.png", 12, 12, true);

        UIRenderObject dirtSquare = new UIRenderObject(50, 1030, 0, 100, 100, 120, dirt);
        RenderObject elf = new RenderObject(0, 0, 0, 80, 140, 0, martin);

        Random r = new Random(System.nanoTime());
        for(int i = -100; i < 100; i++){
            for(int j = -100; j < 100; j++){
                new RenderObject(i * 100.0f, j * 100.0f, 0, 100, 100, r.nextInt(0, 144), dirt);
            }
        }

        animator.add(elf, "walk", 0.3f, 1, 3);
        animator.add(elf, "idle", 0, 0);
        animator.add(dirtSquare, "textureScroll", 0.1f, 0, 144);
        double time = glfwGetTime();
        double lastTime;
        double delta;

        animator.start(elf, "idle");
        animator.start(dirtSquare, "textureScroll");
        while(!window.shouldClose()){
            lastTime = time;
            time = glfwGetTime();
            delta = time - lastTime;
            animator.animate(time);
            if(Input.keyIsDown(GLFW_KEY_W)){
                elf.move(0, (float) (delta) * 500);
            }
            if(Input.keyIsDown(GLFW_KEY_S)){
                elf.move(0, (float) (-500 * delta));
            }
            if(Input.keyIsDown(GLFW_KEY_D)){
                elf.move((float) delta * 500, 0);
            }
            if(Input.keyIsDown(GLFW_KEY_A)){
                elf.move((float) (-500 * delta), 0);
            }

            if(Input.keyPressedDown(GLFW_KEY_W) || Input.keyPressedDown(GLFW_KEY_S) ||
            Input.keyPressedDown(GLFW_KEY_A) || Input.keyPressedDown(GLFW_KEY_D)){
                animator.resume(elf, "walk");
            } else if (!Input.keyIsDown(GLFW_KEY_W) && !Input.keyIsDown(GLFW_KEY_S) &&
                !Input.keyIsDown(GLFW_KEY_A) && !Input.keyIsDown(GLFW_KEY_D)){
                animator.pause(elf, "walk");
                animator.start(elf, "idle");
            }
            camera.setX(elf.getX() - 1920 / 2.0f);
            camera.setY(elf.getY() - 1080 / 2.0f);
            if(Input.keyPressedDown(GLFW_KEY_ESCAPE)){
                window.close();
            }
            window.update();
        }
        window.end();
    }
}