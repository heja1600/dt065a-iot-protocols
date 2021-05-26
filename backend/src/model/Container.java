package src.model;

public class Container <T>{
    T value;
    public Container<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public T getValue() {
        return value;
    }
}
