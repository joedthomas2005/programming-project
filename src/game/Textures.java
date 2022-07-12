package game;

import game.engine.Logger;
import game.engine.rendering.TextureAtlas;

public final class Textures {
    private Textures(){}
    public void load(String filename){
        if(filename.split("\\.")[0].equals(".texmap")){

        }
        else{
            Logger.getInstance().error(filename = " not a texture file (.texmap)");
        }
    }
}
