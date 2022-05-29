package game.rendering;
import game.GameObject;

import java.util.ArrayList;
public class Animation {
    private final GameObject object;
    private final ArrayList<Integer> frames = new ArrayList<>();
    private final double frameInterval;
    private boolean playing;
    private double startTime;
    private int currentFrame;
    private int pausedOn;

    public Animation(GameObject object, double interval, int... frames){
        for(int frame : frames) this.frames.add(frame);
        this.frameInterval = interval;
        this.object = object;
        this.playing = false;
        this.currentFrame = 0;
        this.startTime = 0;
        this.pausedOn = 0;
    }
    public Animation(GameObject object, double interval, int start, int end){
        for(int i = start; i < end; i++) this.frames.add(i);
        this.frameInterval = interval;
        this.pausedOn = 0;
        this.object = object;
        this.playing = false;
        this.currentFrame = 0;
        this.startTime = 0;
    }

    public void start(double time){
        this.playing = true;
        this.startTime = time;
        this.pausedOn = 0;
    }

    public void update(double time){
        if(playing) {
            currentFrame = (pausedOn + (int) Math.floor((time - startTime) / frameInterval)) % frames.size();
            this.object.setTexture(frames.get(currentFrame));
        }
    }

    public void stop(){
        this.playing = false;
    }

    public void pause(){
        this.playing = false;
        this.pausedOn = currentFrame;
    }

    public void resume(double time){
        this.playing = true;
        this.startTime = time;
    }
    public boolean isPlaying(){
        return this.playing;
    }
    public GameObject getObject(){
        return this.object;
    }
}
