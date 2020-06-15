package invasion.util;


public class PolarAngle implements IPolarAngle {
    private int angle;

    public PolarAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }


}