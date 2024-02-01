package project1.part2;

public class RequestResult {
    private final int statusCode;
    private final long latency; // in milliseconds

    public RequestResult(int statusCode, long latency) {
        this.statusCode = statusCode;
        this.latency = latency;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getLatency() {
        return latency;
    }
}
