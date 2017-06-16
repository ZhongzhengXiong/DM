import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main {
    private static final String p1 = "^[A-Z]{1}_\\{[0-9]+\\}$";
    private static final String p2 = "^[A-Z]{1}$";

    public static void main(String args[]) throws IOException {

        System.out.print("Input the filename: ");
        Scanner input = new Scanner(System.in);
        String filename = input.nextLine();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        BufferedWriter wr = new BufferedWriter(new FileWriter("result.txt"));
        for (String expression = br.readLine(); expression != null; expression = br.readLine()) {
            if(expression.equals("")){
                continue;
            }
            wr.write("*****************************\r\n");
            expression = expression.replaceAll(" ", "");
            if (!isWelldefined(expression)) {
                wr.write("not well-defined\r\n");
                continue;
            }

            //construct cst
            PropositionNode root = new PropositionNode(expression, null, null, null, false);
            root = cst(root, wr);
            //counter example
            PropositionNode node = counterexample(root);
            if(node != null){
                wr.write("counterexample\r\n");
            }
            while(node != null){
                if ((Pattern.matches(p1, node.proposition) || Pattern.matches(p2, node.proposition)) && node.label) {
                    wr.write(node.proposition+" ");
                }
                node = node.father;
            }
            wr.write("\r\n");
        }
        wr.close();
        System.out.println("The result has been output into the \"result.txt\"");

    }

    //construct cst
    private static PropositionNode cst(PropositionNode root, BufferedWriter wr) throws IOException {
        LinkedList<PropositionNode> unreducedNodes = new LinkedList<>();
        unreducedNodes.add(root);
        while (!unreducedNodes.isEmpty()) {
            PropositionNode pNode = unreducedNodes.removeFirst();
            String proposition = pNode.proposition;
            //print nodes into the file
            if (pNode.label)
                wr.write("T " + proposition + "\r\n");
            else
                wr.write("F " + proposition + "\r\n");
            //judge whethe the node is conflicted
            if(!pNode.isConflicted){
                if ((Pattern.matches(p1, proposition) || Pattern.matches(p2, proposition)))
                    pNode.isReduced = true;
                if (!(pNode.isReduced)) {
                    PropositionNode node = pNode.copy();
                    reduce(pNode, node);
                }
                if(pNode.left != null)
                    unreducedNodes.add(pNode.left);
                if(pNode.right != null)
                    unreducedNodes.add(pNode.right);
            }
        }
        return root;
    }

    private static boolean isWelldefined(String expression) {
        if (Pattern.matches(p1, expression) || Pattern.matches(p2, expression))
            return true;
        int len = expression.length();
        if (expression.charAt(0) == '(' && expression.charAt(len - 1) == ')') {
            // not A
            if (len > 6 && expression.substring(1, 5).equals("\\not")) {
                return isWelldefined(expression.substring(5, len - 1));
            }
            // A ^ B
            int index = 0;
            String leftExpression;
            String rightExpression;
            if (expression.charAt(1) != '(') {
                index = getRightExpressionIndex(expression, index);
                if (index == -1)
                    return false;
                rightExpression = expression.substring(index, len - 1);
                switch (expression.charAt(index - 1)) {
                    case 'd': {
                        index = index - 4;
                        break;
                    }
                    case 'r': {
                        index = index - 3;
                        break;
                    }
                    case 'y': {
                        index = index - 6;
                        break;
                    }
                    case 'q': {
                        index = index - 3;
                        break;
                    }
                }
                leftExpression = expression.substring(1, index);
            } else {
                index = getLeftExpressionIndex(expression);
                if (index == -1)
                    return false;
                leftExpression = expression.substring(1, index + 1);
                index = getRightExpressionIndex(expression, index);
                if (index == -1)
                    return false;
                rightExpression = expression.substring(index, len - 1);
            }
            return isWelldefined(leftExpression) && isWelldefined(rightExpression);
        }
        return false;
    }

    private static int getLeftExpressionIndex(String expression) {
        int leftP = 0;
        int len = expression.length();
        int index;
        for (index = 2; index < len - 1; index++) {
            if (expression.charAt(index) == ')') {
                if (leftP == 0)
                    break;
                else
                    leftP--;
            } else if (expression.charAt(index) == '(') {
                leftP++;
            }
        }
        if (index == len - 1)
            return -1;
        return index;
    }

    private static int getRightExpressionIndex(String expression, int index) {
        int[] indexArr = new int[4];
        String[] strArr = {"\\and", "\\or", "\\imply", "\\eq"};
        int min = 0;
        int minIndex = 0;
        for(int i = 0; i < 4; i++){
            indexArr[i] = expression.indexOf(strArr[i], index);
            if(indexArr[i] > 0){
                min = indexArr[i];
                minIndex = i;
            }
        }
        if(min == 0)
            return -1;
        for(int i = 0; i < 4; i++){
            if(indexArr[i] > 0 && indexArr[i] < min){
                min = indexArr[i];
                minIndex = i;
            }
        }
        index = indexArr[minIndex] + strArr[minIndex].length();
        return index;
    }

    private static void reduce(PropositionNode root, PropositionNode node) {

        if(root.isConflicted)
            return;

        if (root.proposition.equals(node.proposition) && root.label == node.label) {
            if (root.isReduced)
                return;
            else {
                root.isReduced = true;
                node.isReduced = true;
            }
        }

        if (root.left == null && root.right == null && !root.isConflicted && node.isReduced) {
            node = atomicT(node);
            root.left = node;
            node.father = root;
            PropositionNode tNode = root;
            while (tNode != null) {
                if (node.proposition.equals(tNode.proposition) && node.label != tNode.label) {
                    node.isConflicted = true;
                    break;
                }
                if (node.left != null) {
                    if (!node.left.isConflicted && node.left.proposition.equals(tNode.proposition) && node.left.label != tNode.label)
                        node.left.isConflicted = true;
                    if (node.left.left != null && !node.left.left.isConflicted) {
                        node.left.left.isConflicted = node.left.isConflicted;
                        if (node.left.left.proposition.equals(tNode.proposition) && node.left.left.label != tNode.label)
                            node.left.left.isConflicted = true;
                    }
                }

                if (node.right != null) {
                    if (!node.right.isConflicted && node.right.proposition.equals(tNode.proposition) && node.right.label != tNode.label)
                        node.right.isConflicted = true;
                    if (node.right.left != null && !node.right.left.isConflicted) {
                        node.right.left.isConflicted = node.right.isConflicted;
                        if (node.right.left.proposition.equals(tNode.proposition) && node.right.left.label != tNode.label)
                            node.right.left.isConflicted = true;
                    }
                }
                tNode = tNode.father;
            }
            return;
        }
        if (root.left != null) {
            reduce(root.left, node.copy());
        }
        if (root.right != null) {
            reduce(root.right, node.copy());
        }
    }

    private static PropositionNode atomicT(PropositionNode node) {
        String proposition = node.proposition;
        int len = proposition.length();
        String leftProposition = "", rightProposition = "";
        int index = 0;
        String type = "";
        if (proposition.charAt(1) == '\\') {
            leftProposition = proposition.substring(5, len - 1);
            type = "not";
        }
        if (proposition.charAt(1) == '(') {
            index = getLeftExpressionIndex(proposition);
            leftProposition = proposition.substring(1, index + 1);
            switch (proposition.charAt(index + 2)) {
                case 'a': {
                    type = "and";
                    break;
                }
                case 'o': {
                    type = "or";
                    break;
                }
                case 'i': {
                    type = "imply";
                    break;
                }
                case 'e': {
                    type = "eq";
                }
            }
            index = getRightExpressionIndex(proposition, index);
            rightProposition = proposition.substring(index, len - 1);
        }
        if (Character.isUpperCase(proposition.charAt(1))) {
            index = getRightExpressionIndex(proposition, index);
            rightProposition = proposition.substring(index, len - 1);
            switch (proposition.charAt(index - 1)) {
                case 'd': {
                    type = "and";
                    index = index - 4;
                    break;
                }
                case 'r': {
                    type = "or";
                    index = index - 3;
                    break;
                }
                case 'y': {
                    type = "imply";
                    index = index - 6;
                    break;
                }
                case 'q': {
                    type = "eq";
                    index = index - 3;
                    break;
                }
            }
            leftProposition = proposition.substring(1, index);
        }
        if (!type.equals("eq") && !type.equals("not")) {
            if (node.label)
                type = type + "T";
            else
                type = type + "F";
        }

        switch (type) {
            case "implyT": {
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
            case "orF": {
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
                if (node.label) {
                    lNode1.left = rNode1;
                    lNode2.left = rNode2;
                    rNode1.father = lNode1;
                    rNode2.father = lNode2;
                } else {
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

    private static PropositionNode counterexample(PropositionNode root){
        if(root.isConflicted){
            return null;
        }
        if(root.left == null && root.right == null)
            return root;
        else if(root.right != null){
            PropositionNode rnode = counterexample(root.right);
            if(rnode == null)
                return counterexample(root.left);
            return rnode;
        }else{
            return counterexample(root.left);
        }
    }
}
