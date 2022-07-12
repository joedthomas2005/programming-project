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
    public Player(Animation walk, Animation idle, TextureAtlas atlas, float x, float y){
        super(x, y, 0, PLAYER_WIDTH, PLAYER_HEIGHT, 0, atlas);

        Animator.add(this, "walk", walk);
        Animator.add(this, "idle", idle);
        Animator.start(this, "idle");
        this.inventoryFrame = null;
    }

//    public void showInventory(Window window){
//        if(this.inventoryFrame == null) {
//            this.inventoryFrame = new UIRenderObject(1920/2.0f, 1080/2.0f, 0, 500, 500, )
//        }
//    }
    private void addItem(Item item){
        inventory.add(item);
    }

    private ArrayList<Item> getInventory(){
        return inventory;
    }
    public void update(double delta){
        if(InputController.keyIsDown(KEY_UP) || InputController.keyIsDown(SEC_KEY_UP)){
            move(0, (float) (delta) * 500);
        }
        if(InputController.keyIsDown(KEY_DOWN) || InputController.keyIsDown(SEC_KEY_DOWN)){
            move(0, (float) (-500 * delta));
        }
        if(InputController.keyIsDown(KEY_RIGHT) || InputController.keyIsDown(SEC_KEY_RIGHT)){
            move((float) delta * 500, 0);
        }
        if(InputController.keyIsDown(KEY_LEFT) || InputController.keyIsDown(SEC_KEY_LEFT)){
            move((float) (-500 * delta), 0);
        }
        if(InputController.keyPressedDown(KEY_UP) || InputController.keyPressedDown(KEY_DOWN) ||
                InputController.keyPressedDown(KEY_LEFT) || InputController.keyPressedDown(KEY_RIGHT) ||
                InputController.keyPressedDown(SEC_KEY_UP) || InputController.keyPressedDown(SEC_KEY_DOWN) ||
                InputController.keyPressedDown(SEC_KEY_LEFT) || InputController.keyPressedDown(SEC_KEY_RIGHT)){
            Animator.resume(this, "walk");
        } else if (!(InputController.keyIsDown(KEY_UP) || InputController.keyIsDown(KEY_DOWN) ||
                InputController.keyIsDown(KEY_LEFT) || InputController.keyIsDown(KEY_RIGHT) ||
                InputController.keyIsDown(SEC_KEY_UP) || InputController.keyIsDown(SEC_KEY_DOWN) ||
                InputController.keyIsDown(SEC_KEY_LEFT) || InputController.keyIsDown(SEC_KEY_RIGHT)) &&
                Animator.isPlaying(this, "walk")){
                    Animator.pause(this, "walk");
                    Animator.start(this, "idle");
        }
    }
}
