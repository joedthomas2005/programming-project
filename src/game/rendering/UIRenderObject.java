package game.rendering;

public class UIRenderObject extends RenderObject {
    public UIRenderObject(float x, float y, float rot, float width, float height, int texture, TextureAtlas atlas){
        super(x, y, rot, width, height, texture, atlas);
    }
    public UIRenderObject(float x, float y, float rot, float width, float height, float r, float g, float b){
        super(x, y, rot, width, height, r, g, b);
    }

    @Override
    public boolean isUI(){
        return true;
    }
}
