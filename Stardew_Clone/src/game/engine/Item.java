package game.engine;

import game.engine.rendering.TextureAtlas;
import game.engine.rendering.UIRenderObject;
import org.w3c.dom.Text;

public class Item {
    private String name;
    private TextureAtlas atlas;
    private int texture;
    public Item(String name, int texture, TextureAtlas atlas){
        this.name = name;
        this.texture = texture;
        this.atlas = atlas;
    }

    public UIRenderObject createIcon(float x, float y, float width, float height){
        return new UIRenderObject(x, y, 0, width, height, texture, atlas);
    }
}
