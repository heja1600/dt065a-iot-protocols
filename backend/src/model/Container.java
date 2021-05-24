package src.model;

public class Container <T>{
    T value;
    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
