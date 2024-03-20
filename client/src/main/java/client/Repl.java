package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;
public class Repl{
    private final ChessClient client;
    public Repl(String url){
        client = new ChessClient(url);
    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to your chess client. Register or log in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
