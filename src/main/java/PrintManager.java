public class PrintManager {
    private static boolean instantiated = false;
    private boolean debug = true;
    private boolean error = true;

    public PrintManager() {
        if (instantiated) {
            System.err.println("PrintManager should only be initialized once.");
        }
        instantiated = true;
    }

    public void printDebug(String message) {
        if (debug) {
            System.out.print(message);
        }
    }

    public void printlnDebug(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

    public void printError(String message) {
        if (error) {
            System.out.print(message);
        }
    }

    public void printlnError(String message) {
        if (error) {
            System.out.println(message);
        }
    }
}
