public class LogEntry {
    int term;
    String command;

    public LogEntry(int term, String command) {
        this.term = term;
        this.command = command;
    }

    @Override
    public String toString() {
        return "LogEntry{" + "term=" + term + ", command='" + command + '\'' + '}';
    }
}
