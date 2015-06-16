package followinger;

import java.io.*;

public class Followinger {
    
    static String readCommand(BufferedReader in, PrintWriter out) throws IOException {
        out.printf("> ");
        return in.readLine();
    }

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out), true);
        try {
            if (readCommand(in, out).equals("Alice -> I love the weather today") &&
                    readCommand(in, out).equals("Alice")) {
                out.println("I love the weather today");
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
