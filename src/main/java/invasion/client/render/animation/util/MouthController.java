package invasion.client.render.animation.util;

import invasion.client.render.animation.AnimationAction;
import invasion.client.render.animation.AnimationState;
import invasion.entity.EntityIMLiving;
import invasion.entity.monster.EntityIMBird;


public class MouthController {
    private final EntityIMLiving theEntity;
    private final AnimationState mouthState;
    private int mouthOpenTime;

    public MouthController(EntityIMBird entity, AnimationState stateObject) {
        this.theEntity = entity;
        this.mouthState = stateObject;
        this.mouthOpenTime = 0;
    }

    public void update() {
        if (this.mouthOpenTime > 0) {
            this.mouthOpenTime -= 1;
            this.ensureAnimation(this.mouthState, AnimationAction.MOUTH_OPEN, 1.0F, true);
        } else {
            this.ensureAnimation(this.mouthState, AnimationAction.MOUTH_CLOSE, 1.0F, true);
        }
        this.mouthState.update();
    }

    public void setMouthState(int timeOpen) {
        this.mouthOpenTime = timeOpen;
    }

    private void ensureAnimation(AnimationState state, AnimationAction action, float animationSpeed, boolean pauseAfterAction) {
        if (state.getNextSetAction() != action) {
            state.setNewAction(action, animationSpeed, pauseAfterAction);
        } else {
            state.setAnimationSpeed(animationSpeed);
            state.setPauseAfterSetAction(pauseAfterAction);
            state.setPaused(false);
        }
    }
}