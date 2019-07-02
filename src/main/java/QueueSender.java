import domain.Word;
import interfaces.Sendable;

import java.util.Queue;

public class QueueSender implements Sendable<Word, Queue<Word>> {

    @Override
    public void send(Word word, Queue<Word> to) {
        to.offer(word);
    }
}
