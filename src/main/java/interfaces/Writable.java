package interfaces;

public interface Writable<T> extends ResourceClean{
    void write(T object);
}
