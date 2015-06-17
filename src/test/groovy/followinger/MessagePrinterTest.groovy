package followinger

import followinger.stream.TimingOutReader
import spock.lang.Specification

class MessagePrinterTest extends Specification {
    
    def input = new PipedInputStream()
    def reader = new BufferedReader(new TimingOutReader(new InputStreamReader(input)))
    def output = new PipedOutputStream(input)
    def messagePrinter = new MessagePrinter(output)
    
    def "messages are printed to the stream that the printer operates on"() {
        when:
        messagePrinter.printMessages(["first message", "second message"])
        output.close()
        
        then:
        reader.readLine() == "first message"
        reader.readLine() == "second message"
        reader.readLine() == null
    }
}
