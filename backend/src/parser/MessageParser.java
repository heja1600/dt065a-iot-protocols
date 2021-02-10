package parser;

public interface MessageParser<T> {
    T decode(byte[] buffer);
    byte[] encode(T message);
}
