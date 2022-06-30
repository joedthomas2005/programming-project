package game;

import game.engine.rendering.Animation;
import game.engine.rendering.RenderObject;
import game.engine.rendering.TextureAtlas;

public class Player extends RenderObject {
    private static final float PLAYER_WIDTH = 80f;
    private static final float PLAYER_HEIGHT = 140f;
    private final Animation walkAnimation;
    private final Animation idleAnimation;
    private final TextureAtlas atlas;
    public Player(Animation walk, Animation idle, TextureAtlas atlas, float x, float y){
        super(x, y, 0, PLAYER_WIDTH, PLAYER_HEIGHT, 0, atlas);
        this.walkAnimation = walk;
        this.idleAnimation = idle;
        this.atlas = atlas;
        this.setX(x);
        this.setY(y);
    }
}
