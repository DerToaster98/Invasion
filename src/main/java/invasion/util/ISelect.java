package invasion.util;

public interface ISelect<T> {
    T selectNext();

    void reset();
}