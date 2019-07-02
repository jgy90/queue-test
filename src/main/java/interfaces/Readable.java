package interfaces;

import java.io.IOException;

public interface Readable<T> extends ResourceClean{
    T read() throws IOException;
}
