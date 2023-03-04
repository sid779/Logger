package Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

interface LogClient {
    /**
     * When a process starts, it calls 'start' with processId.
     */
    void start(String processId, long timestamp);

    /**
     * When the same process ends, it calls 'end' with processId.
     */
    void end(String processId);

    /**
     * Polls the first log entry of a completed process sorted by the start time of processes in the below format
     * {processId} started at {startTime} and ended at {endTime}
     * <p>
     * process id = 1 --> 12, 15
     * process id = 2 --> 8, 12
     * process id = 3 --> 7, 19
     * <p>
     * {3} started at {7} and ended at {19}
     * {2} started at {8} and ended at {12}
     * {1} started at {12} and ended at {15}
     */
    String poll();
}

class Process{
    private final String id;
    private final long startTime;

    public String getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    protected long endTime;

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    Process(String id,long startTime){
        this.id = id;
        this.startTime = startTime;
        endTime = -1;
    }
}

class ProcessComparator implements Comparator<Process>{
    @Override
    public int compare(Process p1, Process p2) {
        return Long.compare(p1.getStartTime(),p2.getStartTime());
    }
}

class LoggerImplementation implements LogClient {

    private final Map<String, Process> processes;
    private final TreeMap<Long,Process> queue;

    LoggerImplementation(){
        this.processes = new HashMap<>();
        this.queue = new TreeMap<>();
    }

    @Override
    public void start(String processId, long timestamp) {
        Process process = new Process(processId,timestamp);
        processes.put(processId,process);
        queue.put(timestamp,process);
    }

    @Override
    public void end(String processId) {
        processes.get(processId).setEndTime(System.currentTimeMillis());
    }

    @Override
    public String poll() {
        if(queue.isEmpty()){System.out.println("No processes"); return null;}
        if(queue.firstEntry().getValue().getEndTime()!=-1){
            Process process = queue.pollFirstEntry().getValue();
            System.out.println(process.getId() + " started at " + process.getStartTime() + " and ended at " + process.getEndTime());
            processes.remove(process.getId());
        }
        else{System.out.println("None completed. Processes Running: " + queue.keySet());}
        return null;
    }
}

public class LoggerMain {
    public static void main(String[] args) {
        final LogClient logger = new LoggerImplementation();
        logger.start("1", 1);
        logger.poll();
        logger.start("3", 2);
        logger.poll();
        logger.end("1");
        logger.poll();
        logger.start("2", 3);
        logger.poll();
        logger.end("2");
        logger.poll();
        logger.end("3");
        logger.poll();
        logger.poll();
        logger.poll();
        //1
        //3
        //2
    }
}
