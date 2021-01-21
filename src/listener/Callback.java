package listener;

public interface Callback<T> {
    void call(T t);
}
