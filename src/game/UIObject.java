package game;

import game.rendering.TextureAtlas;

public class UIObject extends GameObject{
    public UIObject(float x, float y, float rot, float width, float height, int texture, TextureAtlas atlas){
        super(x, y, rot, width, height, texture, atlas);
        this.isUI = true;
    }
    public UIObject(float x, float y, float rot, float width, float height, float r, float g, float b){
        super(x, y, rot, width, height, r, g, b);
        this.isUI = true;
    }
}
