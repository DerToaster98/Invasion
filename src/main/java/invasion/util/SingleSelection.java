package invasion.util;

public class SingleSelection<T>
	implements ISelect<T>
{
	private T object;

	public SingleSelection(T object)
	{
		this.object = object;
	}

	@Override
	public T selectNext()
	{
		return this.object;
	}

	@Override
	public void reset()
	{
	}

	@Override
	public String toString()
	{
		return this.object.toString();
	}
}