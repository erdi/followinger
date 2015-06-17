package followinger.stream

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class TimingOutReader extends Reader {

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
