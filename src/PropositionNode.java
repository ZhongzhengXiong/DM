import java.util.Stack;

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



    public PropositionNode atomicT(PropositionNode node, String leftProposition, String rightProposition){
        switch (node.type) {
            case "implyT":{
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", false);
                PropositionNode rNode = new PropositionNode(rightProposition, false, false, "", true);
                node.left = lNode;
                node.right = rNode;
                lNode.father = node;
                rNode.father = node;
                break;
            }
            case "orT":
            case "andF": {
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", node.label);
                PropositionNode rNode = new PropositionNode(rightProposition, false, false, "", node.label);
                node.left = lNode;
                node.right = rNode;
                lNode.father = node;
                rNode.father = node;
                break;
            }
            case "andT":
            case "orF":{
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", node.label);
                PropositionNode llNode = new PropositionNode(rightProposition, false, false, "", node.label);
                node.left = lNode;
                lNode.father = node;
                lNode.left = llNode;
                llNode.father = lNode;
                break;
            }
            case "implyF": {
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", true);
                PropositionNode llNode = new PropositionNode(rightProposition, false, false, "", false);
                node.left = lNode;
                lNode.father = node;
                lNode.left = llNode;
                llNode.father = lNode;
                break;
            }
            case "not": {
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", !node.label);
                node.left = lNode;
                lNode.father = node;
                break;
            }
            case "eq": {
                PropositionNode lNode1 = new PropositionNode(leftProposition, false, false, "", true);
                PropositionNode lNode2 = new PropositionNode(leftProposition, false, false, "", false);
                PropositionNode rNode1 = new PropositionNode(rightProposition, false, false, "", true);
                PropositionNode rNode2 = new PropositionNode(rightProposition, false, false, "", false);
                node.left = lNode1;
                node.right = lNode2;
                lNode1.father = node;
                lNode2.father = node;
                if(node.label == true){
                    lNode1.left = rNode1;
                    lNode2.left = rNode2;
                    rNode1.father = lNode1;
                    rNode2.father = lNode2;
                }else{
                    lNode1.left = rNode2;
                    lNode2.left = rNode1;
                    rNode1.father = lNode2;
                    rNode2.father = lNode1;
                }
                break;
            }
        }
        return node;
    }

    public PropositionNode copy(){
        PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
        return node;
    }

    public PropositionNode copyWithChildren() {
        PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
        switch (type) {
            case "orT":
            case "implyT":
            case "andF": {
                PropositionNode lNode = new PropositionNode(this.left.proposition, this.left.isConflicted, this.left.isReduced, this.left.type, this.left.label);
                PropositionNode rNode = new PropositionNode(this.right.proposition, this.right.isConflicted, this.right.isReduced, this.right.type, this.right.label);
                node.left = lNode;
                node.right = rNode;
                lNode.father = node;
                rNode.father = node;
                break;
            }
            case "andT":
            case "orF":
            case "implyF": {
                PropositionNode lNode = new PropositionNode(this.left.proposition, this.left.isConflicted, this.left.isReduced, this.left.type, this.left.label);
                PropositionNode llNode = new PropositionNode(this.left.left.proposition, this.left.left.isConflicted, this.left.left.isReduced, this.left.left.type, this.left.left.label);
                node.left = lNode;
                lNode.father = node;
                lNode.left = llNode;
                llNode.father = lNode;
                break;
            }
            case "not": {
                PropositionNode lNode = new PropositionNode(this.left.proposition, this.left.isConflicted, this.left.isReduced, this.left.type, this.left.label);
                node.left = lNode;
                lNode.father = node;
                break;
            }
            case "eq": {
                PropositionNode lNode = new PropositionNode(this.left.proposition, this.left.isConflicted, this.left.isReduced, this.left.type, this.left.label);
                PropositionNode rNode = new PropositionNode(this.right.proposition, this.right.isConflicted, this.right.isReduced, this.right.type, this.right.label);
                PropositionNode llNode = new PropositionNode(this.left.left.proposition, this.left.left.isConflicted, this.left.left.isReduced, this.left.left.type, this.left.left.label);
                PropositionNode rrNode = new PropositionNode(this.right.left.proposition, this.right.left.isConflicted, this.right.left.isReduced, this.right.left.type, this.right.left.label);
                node.left = lNode;
                node.right = rNode;
                lNode.father = node;
                rNode.father = node;
                lNode.left = llNode;
                rNode.left = rrNode;
                llNode.father = lNode;
                rrNode.father = rNode;
                break;
            }
        }
        return node;
    }
}
