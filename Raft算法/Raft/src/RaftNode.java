import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RaftNode {
    private int currentTerm;
    private int votedFor;
    private List<LogEntry> log;
    private NodeState state;
    private int[] nextIndex;
    private int[] matchIndex;

    final int nodeId;
    private final List<RaftNode> nodes;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RaftNode(int nodeId, List<RaftNode> nodes) {
        this.nodeId = nodeId;
        this.nodes = nodes;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.log = new ArrayList<>();
        this.state = NodeState.FOLLOWER;
        this.nextIndex = new int[nodes.size()];
        this.matchIndex = new int[nodes.size()];

        // 设置选举超时时间
        scheduler.schedule(this::startElection, getRandomElectionTimeout(), TimeUnit.MILLISECONDS);
    }

    private int getRandomElectionTimeout() {
        return random.nextInt(3000) + 2000; // 2-5 seconds
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public NodeState getState() {
        return state;
    }

    public synchronized void startElection() {
        if (state == NodeState.LEADER) return;

        state = NodeState.CANDIDATE;
        currentTerm++;
        votedFor = nodeId;
        int votes = 1;

        for (RaftNode node : nodes) {
            //不是自己并且发送请求投票
            if (node != this && node.requestVote(currentTerm, nodeId, log.size())) {
                votes++;
            }
        }

        if (votes > nodes.size()/2) {
            becomeLeader();
        } else {
            state = NodeState.FOLLOWER;
            scheduler.schedule(this::startElection, getRandomElectionTimeout(), TimeUnit.MILLISECONDS);
        }
    }


    public synchronized boolean requestVote(int term, int candidateId, int lastLogIndex) {
        if (term > currentTerm) {
            currentTerm = term;
            votedFor = -1;
            state = NodeState.FOLLOWER;
        }

        //如果尚未投票或投票给了请求中的候选人，并且候选人的日志至少与自己一样新，则投票给候选人。
        if ((votedFor == -1 || votedFor == candidateId) && lastLogIndex >= log.size() - 1) {
            votedFor = candidateId;
            return true;
        }

        return false;
    }

    private void becomeLeader() {
        state = NodeState.LEADER;
        System.out.println("Node " + nodeId + " became leader for term： " + currentTerm);
        for (int i = 0; i < nextIndex.length; i++) {
            nextIndex[i] = log.size();
            matchIndex[i] = 0;
        }
        sendHeartbeat();
    }

    private void sendHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state == NodeState.LEADER) {
                appendEntries();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void appendEntries() {
        for (int i = 0; i < nodes.size(); i++) {
            if (i == nodeId) continue;

            RaftNode node = nodes.get(i);
            List<LogEntry> entriesToSend = log.subList(nextIndex[i], log.size());
            if (node.appendEntry(currentTerm, nodeId, nextIndex[i] - 1, entriesToSend)) {
                nextIndex[i] = log.size();
                matchIndex[i] = log.size() - 1;
            } else {
                nextIndex[i]--;
            }
        }
    }

    public synchronized boolean appendEntry(int term, int leaderId, int prevLogIndex, List<LogEntry> entries) {
        if (term < currentTerm) return false;

        currentTerm = term;
        state = NodeState.FOLLOWER;
        //检查前一个日志条目的索引和任期号是否匹配，如果不匹配，则返回 false。
        if (prevLogIndex >= log.size() || (prevLogIndex >= 0 && log.get(prevLogIndex).term != currentTerm)) {
            return false;
        }

        int index = prevLogIndex + 1;
        for (LogEntry entry : entries) {
            //当前index 处是否有日志条目，并且该条目是否与接收到的条目任期不同。要与leader日志相同
            if (log.size() > index && log.get(index).term != entry.term) {
                log = log.subList(0, index);//截断本地日志，从 index 位置开始的所有条目都被丢弃。这是因为新的条目与现有条目冲突，所以要删除现有的冲突条目。
            }
            if (log.size() == index) {
                log.add(entry);//超出了日志的末尾,添加新日志
            } else {
                log.set(index, entry);
            }
            index++;
        }

        System.out.println("Node " + leaderId + " appended entries: " + entries);
        return true;
    }
}