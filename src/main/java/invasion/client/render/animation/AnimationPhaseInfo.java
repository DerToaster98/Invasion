package invasion.client.render.animation;

import java.util.HashMap;
import java.util.Map;


public class AnimationPhaseInfo {
    private final AnimationAction action;
    private final float timeBegin;
    private final float timeEnd;
    private final Map<AnimationAction, Transition> transitions;
    private final Transition defaultTransition;

    public AnimationPhaseInfo(AnimationAction action, float timeBegin, float timeEnd, Transition defaultTransition) {
        this(action, timeBegin, timeEnd, defaultTransition, new HashMap(1));
        this.transitions.put(defaultTransition.getNewAction(), defaultTransition);
    }

    public AnimationPhaseInfo(AnimationAction action, float timeBegin, float timeEnd, Transition defaultTransition, Map<AnimationAction, Transition> transitions) {
        this.action = action;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.defaultTransition = defaultTransition;
        this.transitions = transitions;
    }

    public AnimationAction getAction() {
        return this.action;
    }

    public float getTimeBegin() {
        return this.timeBegin;
    }

    public float getTimeEnd() {
        return this.timeEnd;
    }

    public boolean hasTransition(AnimationAction newAction) {
        return this.transitions.containsKey(newAction);
    }

    public Transition getTransition(AnimationAction newAction) {
        return this.transitions.get(newAction);
    }

    public Transition getDefaultTransition() {
        return this.defaultTransition;
    }
}