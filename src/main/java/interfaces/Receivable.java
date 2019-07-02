package interfaces;

public interface Receivable<T, O> {
    O receive(T from);
}
