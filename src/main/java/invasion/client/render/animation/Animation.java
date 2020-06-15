package invasion.client.render.animation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;


public class Animation<T extends Enum<T>> {
    private final float animationPeriod;
    private final float baseSpeed;
    private final Class<T> skeletonType;
    private final EnumMap<T, List<KeyFrame>> allKeyFrames;
    private final List<AnimationPhaseInfo> animationPhases;

    public Animation(Class<T> skeletonType, float animationPeriod, float baseTime, EnumMap<T, List<KeyFrame>> allKeyFrames, List<AnimationPhaseInfo> animationPhases) {
        this.animationPeriod = animationPeriod;
        this.baseSpeed = baseTime;
        this.skeletonType = skeletonType;
        this.allKeyFrames = allKeyFrames;
        this.animationPhases = animationPhases;
    }

    public float getAnimationPeriod() {
        return this.animationPeriod;
    }

    public float getBaseSpeed() {
        return this.baseSpeed;
    }

    public List<AnimationPhaseInfo> getAnimationPhases() {
        return Collections.unmodifiableList(this.animationPhases);
    }

    public Class<T> getSkeletonType() {
        return this.skeletonType;
    }

    public List<KeyFrame> getKeyFramesFor(T skeletonPart) {
        if (this.allKeyFrames.containsKey(skeletonPart)) {
            return Collections.unmodifiableList(this.allKeyFrames.get(skeletonPart));
        }
        return null;
    }
}