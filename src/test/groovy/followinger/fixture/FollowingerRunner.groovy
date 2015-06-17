package followinger.fixture

import followinger.stream.TimingOutReader

import java.util.regex.Pattern

class FollowingerRunner {

    private final Process process
    private final PrintWriter writer
    private final BufferedReader reader

    private String getApplicationPath() {
        def testConfigProperties = new Properties()
        testConfigProperties.load(getClass().getResourceAsStream("/test-config.properties"))
        testConfigProperties.getProperty("application.path")
    }

    FollowingerRunner() {
        process = new ProcessBuilder(applicationPath).start()
        writer = new PrintWriter(new OutputStreamWriter(process.outputStream), true)
        reader = new BufferedReader(new TimingOutReader(new InputStreamReader(process.inputStream)))
    }
    
    void close() {
        process.destroy()
        try { reader.close() } catch (Exception e) {}
        try { writer.close() } catch (Exception e) {}
    }
    
    private void sendCommand(String command) {
        readCommandPrompt()
        writer.println(command)
    }

    private String readCharacters(int count) {
        def builder = new StringBuilder()
        def character
        while (builder.size() < count && ((character = reader.read()) != -1)) {
            builder.append((char) character);
        }
        builder.toString()
    }
    
    private void readCommandPrompt() {
        def output = readCharacters(2)
        if (output != "> ") {
            throw new AssertionError("Expected command prompt to be printed but got '$output' instead")
        }
    }
    
    void postMessage(String author, String message) {
        sendCommand("$author -> $message")
    }
    
    void showMessages(String author) {
        sendCommand(author)
    }

    void messagePrinted(String message) {
        def printed = reader.readLine()
        def pattern = /${Pattern.quote(message)} \((just now|\d+ \w+ ago)\)/
        assert printed =~ pattern
    } 
}
