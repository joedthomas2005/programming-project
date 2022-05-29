package game.rendering.math;
public class Vector {
    float[] items = {};
    public Vector(float... items){
        this.items = items;
    }
    public Vector(int items){
        this.items = new float[items];
    }

    public Vector(float degrees, float magnitude){
        this.items = new float[]{ 
            (float)(magnitude * Math.cos(Math.toRadians(degrees))),
            (float)(magnitude * Math.sin(Math.toRadians(degrees)))
        };
    }

    @Override
    public String toString(){
        String string = "(";
        for(int i = 0; i < this.items.length; i++){
            string += this.items[i];
            if(i != this.items.length-1){
                string += ", ";
            }
        }
        string += ")";
        return string;
    }

    public Vector toVec4(){
        float[] components = new float[4];
        for(int i = 0; i < 4; i++){
            try{
                components[i] = items[i];
            }
            catch(ArrayIndexOutOfBoundsException e){
                components[i] = 0;
                if(i == 3){
                    components[i] = 1;
                }
            }
        }
        return new Vector(components);
    }

    public float getDirection2D(){
        return (float) Math.toDegrees(Math.atan(this.items[1]/this.items[0]));
    }

    public float getMagnitude(){
        if(this.items.length >= 3){
            return (float) Math.sqrt(Math.pow(items[0], 2) + Math.pow(items[1], 2) + Math.pow(items[2], 2));
        }
        else{
            return (float) Math.sqrt(Math.pow(items[0], 2) + Math.pow(items[1], 2));
        }
    }

    public Vector multiply(float n){
        float[] items = new float[this.items.length];
        for(int i = 0; i < this.items.length; i++){
            items[i] = this.items[i] * n;
        }
        return new Vector(items);
    }
    public float getX(){
        return items[0];
    }

    public float getY(){
        return items[1];
    }

    public float getZ(){
        return items[2];
    }

    public float getW(){
        return items[3];
    }

    public static Vector Vec3(float x, float y, float z){
        return new Vector(x, y, z, 1);
    }

    public static Vector Vec2(float x, float y){
        return new Vector(new float[]{x, y, 0, 1});
    }

}
