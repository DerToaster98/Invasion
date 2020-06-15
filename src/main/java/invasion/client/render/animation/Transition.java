package invasion.client.render.animation;

public class Transition {
    private final AnimationAction newAction;
    private final float sourceTime;
    private final float destTime;

    public Transition(AnimationAction newAction, float sourceTime, float destTime) {
        this.newAction = newAction;
        this.sourceTime = sourceTime;
        this.destTime = destTime;
    }

    public AnimationAction getNewAction() {
        return this.newAction;
    }

    public float getSourceTime() {
        return this.sourceTime;
    }

    public float getDestTime() {
        return this.destTime;
    }
}