package interfaces;

import java.io.IOException;

public interface ResourceCleaner {
    void close() throws IOException;
}
