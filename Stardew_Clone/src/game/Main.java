package game;

import game.engine.InputController;
import game.engine.Logger;
import game.engine.Window;
import game.engine.rendering.*;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

class Main{

    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}