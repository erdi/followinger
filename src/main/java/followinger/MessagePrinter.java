package followinger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class MessagePrinter {

    private final PrintWriter out;

    public MessagePrinter(OutputStream stream) {
        this.out = new PrintWriter(new OutputStreamWriter(stream), true);
    }

    public void printMessages(List<String> messages) {
        messages.stream().forEach(out::println);
    }
}
