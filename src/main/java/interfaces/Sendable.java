package interfaces;

public interface Sendable<O, T> {
    void send(O object, T to);
}
