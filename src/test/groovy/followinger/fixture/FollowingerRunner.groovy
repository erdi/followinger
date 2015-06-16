package followinger.fixture

import java.util.concurrent.*

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
        assert reader.readLine() == message
    }

    private static class TimingOutReader extends Reader {
        
        private final Reader reader
        private final ExecutorService executor = Executors.newSingleThreadExecutor()
        
        TimingOutReader(Reader reader) {
            this.reader = reader
        }
        
        @Override
        int read(char[] cbuf, int off, int len) throws IOException {
            Future<Integer> readFuture = executor.submit({
                reader.read(cbuf, off, len)
            } as Callable<Integer>)
            try {
                readFuture.get(2, TimeUnit.SECONDS)
            } catch (TimeoutException e) {
                throw new AssertionError("Expected new data to be available but none has been received")
            }
        }
    
        @Override
        void close() throws IOException {
            executor.shutdownNow()
            reader.close()
        }
    }
}
