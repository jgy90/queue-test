import domain.Word;
import interfaces.Receivable;

import java.util.Queue;

public class QueueReceiver implements Receivable<Queue<Word>, Word> {
    @Override
    public Word receive(Queue<Word> from) {
        return from.poll();
    }
}
