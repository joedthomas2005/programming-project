package game.engine.rendering;

public class Texture {
    public TextureAtlas atlas;
    public int id;
    public Texture(TextureAtlas atlas, int id){
        this.atlas = atlas;
        this.id = id;
    }
}
