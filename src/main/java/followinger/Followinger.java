package followinger;

import followinger.message.MessagePrinter;
import followinger.message.MessageStore;

import java.io.*;
import java.time.Clock;

public class Followinger {

    private final BufferedReader in;
    private final PrintWriter out;
    private final CommandProcessor commandProcessor;

    public Followinger(InputStream input, OutputStream output, CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
        in = new BufferedReader(new InputStreamReader(input));
        out = new PrintWriter(new OutputStreamWriter(output), true);
    }
    
    public void processInput() throws IOException {
        String command;
        while ((command = readCommand()) != null) {
            commandProcessor.processCommand(command);
        }
    }
    
    private String readCommand() throws IOException {
        out.printf("> ");
        return in.readLine();
    }

    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        MessagePrinter messagePrinter = new MessagePrinter(System.out, clock);
        MessageStore messageStore = new MessageStore(clock);
        CommandProcessor commandProcessor = new CommandProcessor(messageStore, messagePrinter);
        Followinger followinger = new Followinger(System.in, System.out, commandProcessor);
        try {
            followinger.processInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
