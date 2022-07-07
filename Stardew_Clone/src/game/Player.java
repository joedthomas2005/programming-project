package game;

import game.engine.Input;
import game.engine.Item;
import game.engine.Window;
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
        if(Input.keyIsDown(KEY_UP) || Input.keyIsDown(SEC_KEY_UP)){
            move(0, (float) (delta) * 500);
        }
        if(Input.keyIsDown(KEY_DOWN) || Input.keyIsDown(SEC_KEY_DOWN)){
            move(0, (float) (-500 * delta));
        }
        if(Input.keyIsDown(KEY_RIGHT) || Input.keyIsDown(SEC_KEY_RIGHT)){
            move((float) delta * 500, 0);
        }
        if(Input.keyIsDown(KEY_LEFT) || Input.keyIsDown(SEC_KEY_LEFT)){
            move((float) (-500 * delta), 0);
        }
        if(Input.keyPressedDown(KEY_UP) || Input.keyPressedDown(KEY_DOWN) ||
                Input.keyPressedDown(KEY_LEFT) || Input.keyPressedDown(KEY_RIGHT) ||
                Input.keyPressedDown(SEC_KEY_UP) || Input.keyPressedDown(SEC_KEY_DOWN) ||
                Input.keyPressedDown(SEC_KEY_LEFT) || Input.keyPressedDown(SEC_KEY_RIGHT)){
            Animator.resume(this, "walk");
        } else if (!(Input.keyIsDown(KEY_UP) || Input.keyIsDown(KEY_DOWN) ||
                Input.keyIsDown(KEY_LEFT) || Input.keyIsDown(KEY_RIGHT) ||
                Input.keyIsDown(SEC_KEY_UP) || Input.keyIsDown(SEC_KEY_DOWN) ||
                Input.keyIsDown(SEC_KEY_LEFT) || Input.keyIsDown(SEC_KEY_RIGHT)) &&
                Animator.isPlaying(this, "walk")){
                    Animator.pause(this, "walk");
                    Animator.start(this, "idle");
        }
    }
}
