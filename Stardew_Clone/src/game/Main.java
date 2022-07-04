package game;

import game.engine.Input;
import game.engine.Window;
import game.engine.rendering.*;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

class Main{
    public static void main(String[] args) {
        Window window = new Window(1920, 1080, "Stardew Valley", true, 1, 0, 0, 0);
        Input.enable(window);
        OrthographicCamera2D camera = new OrthographicCamera2D(-1920/2.0f, -1080/2.0f, 1920, 1080);
        Renderer.initializeRectRenderer(camera);
        TextureAtlas martin = new TextureAtlas("Stardew_Clone/res/elf.png", 3, 1, true);
        TextureAtlas dirt = new TextureAtlas("Stardew_Clone/res/StardewDirt.png", 12, 12, true);

        UIRenderObject dirtSquare = new UIRenderObject(50, 1030, 0, 100, 100, 120, dirt);

        Random r = new Random(System.nanoTime());
        for(int i = -200; i < 200; i++){
            for(int j = -200; j < 200; j++){
                new RenderObject(i * 100.0f, j * 100.0f, 0, 100, 100, r.nextInt(0, 144), dirt);
            }
        }
        Player player = new Player(
                new Animation(0.3f, 1, 3),
                new Animation(0, 0),
                martin,
                -100, -100
        );
        Renderer.setAmbientLight(1, 1, 1, 0.8f);
        PointLight playerLight = new PointLight(0, 0, 1, 1, 1, 1);

        Animator.add(dirtSquare, "textureScroll", 0.1f, 0, 144);
        double time = glfwGetTime();
        double lastTime;
        double delta;

        Animator.start(dirtSquare, "textureScroll");
        while(!window.shouldClose()){
            lastTime = time;
            time = glfwGetTime();
            delta = time - lastTime;
            player.update(delta);
            Animator.animate(time);

            if(Input.keyPressedDown(GLFW_KEY_SPACE)){
                if(player.getHidden()){
                    player.show();
                }
                else {
                    player.hide();
                }
            }
            camera.setX(player.getX() - 1920 / 2.0f);
            camera.setY(player.getY() - 1080 / 2.0f);
            playerLight.setX(player.getX());
            playerLight.setY(player.getY());

            if(Input.keyPressedDown(GLFW_KEY_ESCAPE)){
                window.close();
            }
            window.update();
        }
        window.end();
    }
}