package game.engine.rendering;

import java.util.ArrayList;
public class Animation {
    private final int[] frames;
    private final double frameInterval;
    private boolean playing;
    private double startTime;
    private int currentFrame;
    private int pausedOn;

    public Animation(double interval, int... frames){
        this.frames = frames;
        this.frameInterval = interval;
        this.playing = false;
        this.currentFrame = 0;
        this.startTime = 0;
        this.pausedOn = 0;
    }
    public Animation(double interval, int start, int end){
        this.frames = new int[end-start];
        for(int i = start; i < end; i++){
            this.frames[i - start] = i;
        }
        this.frameInterval = interval;
        this.pausedOn = 0;
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
        this.currentFrame = (pausedOn + (int) Math.floor((time - startTime) / frameInterval)) % frames.length;
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

    public int getCurrentFrame(){
        return this.frames[currentFrame];
    }
}
