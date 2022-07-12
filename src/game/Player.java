package game;

import game.engine.InputController;
import game.engine.Item;
import game.engine.rendering.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends RenderObject {
    private static final int KEY_UP = GLFW_KEY_UP;
    private static final int KEY_DOWN = GLFW_KEY_DOWN;
    private static final int KEY_LEFT = GLFW_KEY_LEFT;
    private static final int KEY_RIGHT = GLFW_KEY_RIGHT;
    private static final int SEC_KEY_UP = GLFW_KEY_W;
    private static final int SEC_KEY_DOWN = GLFW_KEY_S;
    private static final int SEC_KEY_LEFT = GLFW_KEY_A;
    private static final int SEC_KEY_RIGHT = GLFW_KEY_D;
    private static final float PLAYER_WIDTH = 80f;
    private static final float PLAYER_HEIGHT = 140f;
    private final ArrayList<Item> inventory = new ArrayList<>();
    private UIRenderObject inventoryFrame;
    private final Animator animator;
    private final Animation walk;
    private final Animation idle;

    public Player(Animator animator, Animation walk, Animation idle, TextureAtlas atlas, float x, float y) {
        super(x, y, 0, PLAYER_WIDTH, PLAYER_HEIGHT, 0, atlas);
        this.walk = walk;
        this.idle = idle;
        this.animator = animator;
        this.inventoryFrame = null;
    }

    public void loadAnimations() {
        animator.add(this, "walk", walk);
        animator.add(this, "idle", idle);
    }

    //    public void showInventory(Window window){
//        if(this.inventoryFrame == null) {
//            this.inventoryFrame = new UIRenderObject(1920/2.0f, 1080/2.0f, 0, 500, 500, )
//        }
//    }
    private void addItem(Item item) {
        inventory.add(item);
    }

    private ArrayList<Item> getInventory() {
        return inventory;
    }

    public void pollInput(double delta) {
        InputController input = InputController.getInstance();
        if (input.keyIsDown(KEY_UP) || input.keyIsDown(SEC_KEY_UP)) {
            move(0, (float) (delta) * 500);
        }
        if (input.keyIsDown(KEY_DOWN) || input.keyIsDown(SEC_KEY_DOWN)) {
            move(0, (float) (-500 * delta));
        }
        if (input.keyIsDown(KEY_RIGHT) || input.keyIsDown(SEC_KEY_RIGHT)) {
            move((float) delta * 500, 0);
        }
        if (input.keyIsDown(KEY_LEFT) || input.keyIsDown(SEC_KEY_LEFT)) {
            move((float) (-500 * delta), 0);
        }
        if (input.keyPressedDown(KEY_UP) || input.keyPressedDown(KEY_DOWN) ||
                input.keyPressedDown(KEY_LEFT) || input.keyPressedDown(KEY_RIGHT) ||
                input.keyPressedDown(SEC_KEY_UP) || input.keyPressedDown(SEC_KEY_DOWN) ||
                input.keyPressedDown(SEC_KEY_LEFT) || input.keyPressedDown(SEC_KEY_RIGHT)) {
            animator.resume(this, "walk");
        } else if (!(input.keyIsDown(KEY_UP) || input.keyIsDown(KEY_DOWN) ||
                input.keyIsDown(KEY_LEFT) || input.keyIsDown(KEY_RIGHT) ||
                input.keyIsDown(SEC_KEY_UP) || input.keyIsDown(SEC_KEY_DOWN) ||
                input.keyIsDown(SEC_KEY_LEFT) || input.keyIsDown(SEC_KEY_RIGHT)) &&
                animator.isPlaying(this, "walk")) {
            animator.pause(this, "walk");
            animator.start(this, "idle");
        }
    }
}
