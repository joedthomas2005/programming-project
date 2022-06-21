package game.engine.rendering;

public class PointLight implements Ens{
    private static final float DISTANCE_COEFFICIENT = 0.01f;
    private static final float INSIGNIFICANCE_LEVEL = 0.1f;
    private float x;
    private float y;
    private float r;
    private float g;
    private float b;
    private float intensity;
    private boolean updated;


    public PointLight(float x, float y, float r, float g, float b, float intensity){
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
        this.intensity = intensity;
        this.updated = true;
        BatchedRenderer.add(this);
    }

    public float getX(){
        return this.x;
    }

    public boolean getUpdated(){
        return updated;
    }

    public void setUpdated(boolean updated){
        this.updated = updated;
    }
    public void setX(float x){
        this.x = x;
        this.updated = true;
    }

    public void setY(float y){
        this.y = y;
        this.updated = true;
    }
    public float getY(){
        return this.y;
    }

    public float getR(){
        return r;
    }

    public float getG(){
        return g;
    }

    public float getB(){
        return b;
    }

    public float getIntensity(){
        return intensity;
    }
    public float getWidth(){
        // intensity/(ad + 1) < b
        // intensity < abd + b
        // abd > intensity - b
        // d > (intensity - b)/ab
        return 2 * (intensity - INSIGNIFICANCE_LEVEL) / (DISTANCE_COEFFICIENT * INSIGNIFICANCE_LEVEL);
    }

    public float getHeight(){
        return 2 * (intensity - INSIGNIFICANCE_LEVEL) / (DISTANCE_COEFFICIENT * INSIGNIFICANCE_LEVEL);
    }
}
