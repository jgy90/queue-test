import domain.Word;
import interfaces.Receivable;

import java.util.Queue;

public class QueueReceiver implements Receivable {
    @Override
    public Object receive(Object from) {
        Queue<Word> partition = (Queue<Word>) from;
        return partition.poll();
    }
}
