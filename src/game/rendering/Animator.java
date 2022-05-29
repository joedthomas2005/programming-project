package game.rendering;

import game.GameObject;

import java.util.HashMap;

public class Animator {
    private final HashMap<String, Animation> animations = new HashMap<>();
    private double time;
    public Animator(){
        this.time = 0;
    }

    public void addAnimation(String name, Animation animation){
        this.animations.put(name, animation);
    }

    public void addAnimation(String name, GameObject object, float interval, int start, int end){
        Animation anim = new Animation(object, interval, start, end);
        this.animations.put(name, anim);
    }

    public void startAnimation(String name){
        for(Animation anim : animations.values()){
            if(anim.isPlaying() && anim.getObject() == animations.get(name).getObject()){
//                System.out.println("WARNING: ANIMATION ALREADY PLAYING. INTERRUPTING.");
                anim.stop();
                break;
            }
        }
        this.animations.get(name).start(time);
    }

    public void animate(double time){
        this.time = time;
        for(Animation anim : animations.values()){
            anim.update(time);
        }
    }

    public void pauseAnimation(String name){
        Animation anim = animations.get(name);
        if(anim.isPlaying()){
            anim.pause();
        }
    }

    public void resumeAnimation(String name){
        Animation anim = animations.get(name);
        if(!anim.isPlaying()){
            anim.resume(time);
        }
    }

    public void pauseAnimations(){
        for(Animation anim : animations.values()){
            if(anim.isPlaying()){
                anim.pause();
            }
        }
    }

    public void resumeAnimations(){
        for(Animation anim : animations.values()){
            if(!anim.isPlaying()) {
                anim.resume(time);
            }
        }
    }

    public void pauseAnimations(String... names){
        for(String name : names) {
            Animation anim = animations.get(name);
            if(anim.isPlaying()){
                anim.pause();
            }
        }
    }

    public void resumeAnimations(String... names){
        for(String name : names){
            Animation anim = animations.get(name);
            if(!anim.isPlaying()){
                anim.resume(time);
            }
        }
    }
    public void stopAnimations(){
        for(Animation anim : animations.values()){
            if(anim.isPlaying()) {
                anim.stop();
            }
        }
    }

    public void startAnimations(){
        for(Animation anim : animations.values()){
            if(!anim.isPlaying()) {
                anim.start(time);
            }
        }
    }

    public void stopAnimations(String... names){
        for(String name : names){
            Animation anim = animations.get(name);
            if(anim.isPlaying()){
                anim.stop();
            }
        }
    }

    public void startAnimations(String... names){
        for(String name : names){
            Animation anim = animations.get(name);
            if(!anim.isPlaying()){
                anim.start(time);
            }
        }
    }
    public void stopAnimation(String name){
        Animation anim = animations.get(name);
        if(anim.isPlaying()){
            anim.stop();
        }
    }

    public boolean isPlaying(String name){
        return this.animations.get(name).isPlaying();
    }
}
