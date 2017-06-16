/**
 * Created by XiongZZ on 2017/5/31.
 */

public class PropositionNode {
    String proposition;
    PropositionNode left;
    PropositionNode right;
    PropositionNode father;
    boolean label;
    boolean isConflicted;
    boolean isReduced;
    String type;

    PropositionNode(String proposition, PropositionNode left, PropositionNode right, PropositionNode father, boolean label) {
        this.proposition = proposition;
        this.left = left;
        this.right = right;
        this.father = father;
        this.label = label;
    }

    PropositionNode(String proposition, boolean isConflicted, boolean isReduced, String type, boolean label){
        this.proposition = proposition;
        this.isConflicted = isConflicted;
        this.isReduced = isReduced;
        this.type = type;
        this.label = label;
    }

    public PropositionNode copy(){
        PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
        return node;
    }

}
