package textures;

public class TextureModel {

    private int textureID;

    private float shineDampener = 1;
    private float reflectivity = 0;
    private boolean hasTransparency = false;
    private boolean fakeLightning = false;

    public boolean isHasTransparency() {
        return hasTransparency;
    }

    public boolean isFakeLightning() {
        return fakeLightning;
    }

    public void setFakeLightning(boolean fakeLightning) {
        this.fakeLightning = fakeLightning;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public TextureModel(int textureID) {
        this.textureID = textureID;
    }

    public float getShineDampener() {
        return shineDampener;
    }

    public void setShineDampener(float shineDampener) {
        this.shineDampener = shineDampener;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public int getTextureID() {
        return textureID;
    }
}
