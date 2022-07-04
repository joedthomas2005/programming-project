package game.engine.rendering;

import java.util.HashMap;

public final class Animator {
    private static final HashMap<RenderObject, HashMap<String, Animation>> animations = new HashMap<>();
    private static double time;
    private Animator(){}

    public static void add(RenderObject object, String name, Animation animation){
        if(animations.containsKey(object)){
            animations.get(object).put(name, animation);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, animation);
        }
    }

    public static void add(RenderObject object, String name, float interval, int start, int end){
        Animation anim = new Animation(interval, start, end);
        if(animations.containsKey(object)){
            animations.get(object).put(name, anim);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, anim);
        }
    }

    public static void add(RenderObject object, String name, float interval, int... frames){
        Animation anim = new Animation(interval, frames);
        if(animations.containsKey(object)){
            animations.get(object).put(name, anim);
        }
        else{
            animations.put(object, new HashMap<>());
            animations.get(object).put(name, anim);
        }
    }

    public static void animate(double time) {
        Animator.time = time;
        animations.forEach((RenderObject object, HashMap<String, Animation> animation) ->{
            animation.forEach((String name, Animation anim) -> {
                if(anim.isPlaying()){
                    anim.update(time);
                    object.setTexture(anim.getCurrentFrame());
                }
            });
        });
    }

    public static void start(RenderObject object, String name){
        for(Animation anim : animations.get(object).values()){
            if(anim.isPlaying()){
                anim.pause();
                break;
            }
        }
        animations.get(object).get(name).start(time);
    }

    public static boolean isPlaying(RenderObject object, String name) {
        return animations.get(object).get(name).isPlaying();
    }
    public static void stop(RenderObject object, String name){
        animations.get(object).get(name).stop();
    }

    public static void pause(RenderObject object, String name){
        animations.get(object).get(name).pause();
    }

    public static void resume(RenderObject object, String name){
        for(Animation anim : animations.get(object).values()){
            if(anim.isPlaying()){
                anim.pause();
            }
        }
        animations.get(object).get(name).resume(time);
    }
}
