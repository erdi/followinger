package followinger

import com.google.common.io.ByteStreams
import spock.lang.Specification

class FollowingerSpec extends Specification {
    
    def input = new PipedInputStream()
    def out = new PrintWriter(new OutputStreamWriter(new PipedOutputStream(input)), true)
    def commandProcessor = Mock(CommandProcessor)
    def followinger = new Followinger(input, ByteStreams.nullOutputStream(), commandProcessor)
    
    def "commands sent to input stream are passed on to the command processor"() {
        when:
        out.println("first command")
        out.println("second command")
        out.close()
        
        and:
        followinger.processInput()
        
        then:
        1 * commandProcessor.processCommand("first command")
        1 * commandProcessor.processCommand("second command")
    }
}
