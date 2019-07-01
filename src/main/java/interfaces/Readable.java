package interfaces;

import java.io.IOException;

public interface Readable extends ResourceClean{
    String read() throws IOException;
}
