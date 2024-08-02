import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaftMain {
    public static void main(String[] args) {
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            nodes.add(new RaftNode(i, nodes));
        }

        new Thread(() -> {
            while (true) {
                for (RaftNode node : nodes) {
                    if (node.getState() == NodeState.LEADER) {
                        System.out.println("Leader: Node " + node.nodeId);
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}