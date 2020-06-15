package invasion.entity;

import invasion.entity.ai.navigator.Path;


public abstract interface IPathResult
{
	public abstract void pathCompleted(Path paramPath);
}