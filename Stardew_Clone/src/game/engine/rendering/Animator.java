package game.engine.rendering;

import java.util.HashMap;

public class Animator {
    private final HashMap<RenderObject, HashMap<String, Animation>> animations;
    private double time;
    public Animator(){
        animations = new HashMap<>();
        time = 0;
    }

    public void add(RenderObject object, String name, Animation animation){
        if(animations.containsKey(object)){
            animations.get(object).put(name, animation);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, animation);
        }
    }

    public void add(RenderObject object, String name, float interval, int start, int end){
        Animation anim = new Animation(interval, start, end);
        if(animations.containsKey(object)){
            animations.get(object).put(name, anim);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, anim);
        }
    }

    public void add(RenderObject object, String name, float interval, int... frames){
        Animation anim = new Animation(interval, frames);
        if(animations.containsKey(object)){
            animations.get(object).put(name, anim);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, anim);
        }
    }

    public void animate(double time) {
        this.time = time;
        animations.forEach((RenderObject object, HashMap<String, Animation> animation) ->{
            animation.forEach((String name, Animation anim) -> {
                if(anim.isPlaying()){
                    anim.update(this.time);
                    object.setTexture(anim.getCurrentFrame());
                }
            });
        });
    }

    public void start(RenderObject object, String name){
        for(Animation anim : animations.get(object).values()){
            if(anim.isPlaying()){
                anim.pause();
                break;
            }
        }
        animations.get(object).get(name).start(time);
    }

    public boolean isPlaying(RenderObject object, String name) {
        return animations.get(object).get(name).isPlaying();
    }
    public void stop(RenderObject object, String name){
        animations.get(object).get(name).stop();
    }

    public void pause(RenderObject object, String name){
        animations.get(object).get(name).pause();
    }

    public void resume(RenderObject object, String name){
        for(Animation anim : animations.get(object).values()){
            if(anim.isPlaying()){
                anim.pause();
            }
        }
        animations.get(object).get(name).resume(time);
    }
}
