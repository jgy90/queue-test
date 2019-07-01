import domain.Word;
import interfaces.Sendable;

import java.util.Queue;

public class QueueSender implements Sendable {
    private Queue<Word> partition;

    @Override
    public void send(Object object, Object to) {
        Word word = (Word) object;
        partition = (Queue<Word>) to;
        partition.offer(word);
    }
}
