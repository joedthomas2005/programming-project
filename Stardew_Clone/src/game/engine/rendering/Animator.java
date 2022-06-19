package game.engine.rendering;

import java.util.HashMap;

public class Animator {
    private final HashMap<RenderObject, HashMap<String, Animation>> animations = new HashMap<>();
    private double time;
    public Animator(){
        this.time = 0;
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
        Animation anim = new Animation(object, interval, start, end);
        if(animations.containsKey(object)){
            animations.get(object).put(name, anim);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, anim);
        }
    }

    public void add(RenderObject object, String name, float interval, int... frames){
        Animation anim = new Animation(object, interval, frames);
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
        for (HashMap<String, Animation> entry : animations.values()) {
            for (Animation anim : entry.values()) {
                anim.update(time);
            }
        }
    }

    public void start(RenderObject object, String name){
        for(Animation anim : animations.get(object).values()){
            if(anim.isPlaying()){
                anim.stop();
                break;
            }
        }

        this.animations
                .get(object)
                .get(name)
                .start(time);
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
                anim.stop();
            }
        }
        this.animations
                .get(object)
                .get(name)
                .resume(time);
    }
}
