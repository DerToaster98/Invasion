package invasion;

import invasion.entity.ai.navigator.PathNode;
import invasion.entity.ai.navigator.PathfinderIM;


public interface IPathfindable {
    float getBlockPathCost(PathNode prevNode, PathNode node);

    void getPathOptionsFromNode(PathNode paramPathNode, PathfinderIM paramPathfinderIM);
}