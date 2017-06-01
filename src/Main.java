/**
 * Created by XiongZZ on 2017/5/31.
 */

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.*;
import java.util.regex.*;


public class Main {
    static final String p1 = "^[A-Z]{1}_\\{[0-9]+\\}$";
    static final String p2 = "^[A-Z]{1}$";

    public static void main(String args[]) {
        String expression = " (((A \\imply B) \\or (C \\or D)) \\and (A \\or B))";
        expression = expression.replaceAll(" ", "");
        if(isWelldefined(expression)){
            PropositionNode root = new PropositionNode(expression, null, null, null, false);
            root = cst(root);
            System.out.println(root.proposition);
        }
        else
            System.out.println("wrong");
    }

    public static PropositionNode cst(PropositionNode root) {
        // ArrayDeque<PropositionNode> queue = new ArrayDeque<>();
        LinkedList<PropositionNode> unreducedNodes = new LinkedList<>();
        unreducedNodes.add(root);
        while (!unreducedNodes.isEmpty()) {
            PropositionNode pNode = unreducedNodes.removeFirst();
            PropositionNode node = pNode.copy();
            if (pNode.isReduced)
                continue;
            String proposition = pNode.proposition;
            int len = proposition.length();
            String leftProposition = "", rightProposition = "";
            int index = 0;
            String type = "";
            if (Pattern.matches(p1, proposition) || Pattern.matches(p2, proposition))
                continue;
            if (proposition.charAt(1) == '\\') {
                leftProposition = proposition.substring(1, len - 1);
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
                switch (proposition.charAt(index - 1)){
                    case 'd':{
                        type = "and";
                        index = index - 4;
                        break;
                    }
                    case 'r':{
                        type = "or";
                        index = index -3;
                        break;
                    }
                    case 'y':{
                        type = "imply";
                        index = index -6;
                        break;
                    }
                    case 'q':{
                        type = "eq";
                        index = index - 3;
                        break;
                    }
                }
                leftProposition = proposition.substring(1, index);
            }
            if (!type.equals("eq")  && !type.equals("not")) {
                if (node.label == true)
                    type = type + "T";
                else
                    type = type + "F";
            }
            pNode.type = type;
            node.type = type;
            node = atomicT(node, leftProposition, rightProposition);
            reduce(root, node, unreducedNodes);
        }
        return  root;
    }


    public static boolean isWelldefined(String expression) {
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
            String leftExpression = " ";
            String rightExpression = " ";
            if (expression.charAt(1) != '(') {
                index = getRightExpressionIndex(expression, index);
                if (index == -1)
                    return false;
                rightExpression = expression.substring(index, len - 1);
                switch (expression.charAt(index - 1)){
                    case 'd':{
                        index = index - 4;
                        break;
                    }
                    case 'r':{
                        index = index -3;
                        break;
                    }
                    case 'y':{
                        index = index -6;
                        break;
                    }
                    case 'q':{
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


    public static int getLeftExpressionIndex(String expression) {
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

    public static int getRightExpressionIndex(String expression, int index) {
        if ((index = expression.indexOf("\\and", index)) != -1) {
            index = index + 4;
        } else if ((index = expression.indexOf("\\or", index)) != -1) {
            index = index + 3;
        } else if ((index = expression.indexOf("\\imply", index)) != -1) {
            index = index + 6;
        } else if ((index = expression.indexOf("\\eq", index)) != -1) {
            index = index + 3;
        } else
            index = -1;
        return index;
    }

    public static void reduce(PropositionNode root, PropositionNode node, LinkedList<PropositionNode> unreducedNodes) {


        if (root.proposition.equals(node.proposition) && root.label == node.label) {
            if(root.isReduced)
                return;
            else{
                root.isReduced = true;
                node.isReduced = true;
            }
        }
        switch (node.type) {
            case "orT":
            case "implyT":
            case "andF": {
                if (node.left.proposition.equals(node.proposition) && node.left.label != root.label)
                    node.left.isConflicted = true;
                if (node.right.proposition.equals(node.proposition) && node.right.label != root.label)
                    node.right.isConflicted = true;
                break;
            }
            case "andT":
            case "orF":
            case "implyF": {
                if (node.left.proposition.equals(node.proposition) && node.left.label != root.label)
                    node.left.isConflicted = true;
                if (node.left.left.proposition.equals(node.proposition) && node.left.left.label != root.label)
                    node.left.left.isConflicted = true;
                break;
            }
            case "not": {
                if (node.left.proposition.equals(node.proposition) && node.left.label != root.label)
                    node.left.isConflicted = true;
                break;
            }
            case "eq": {
                if (node.left.proposition.equals(node.proposition) && node.left.label != root.label)
                    node.left.isConflicted = true;
                if (node.left.left.proposition.equals(node.proposition) && node.left.left.label != root.label)
                    node.left.left.isConflicted = true;
                if (node.right.proposition.equals(node.proposition) && node.right.label != root.label)
                    node.right.isConflicted = true;
                if (node.right.left.proposition.equals(node.proposition) && node.right.left.label != root.label)
                    node.right.left.isConflicted = true;
                break;
            }
        }
        if (root.left == null && root.right == null && !root.isConflicted) {
            //           PropositionNode copyNode = node.copy();
            root.left = node;
            node.father = root;
            switch (node.type) {
                case "orT":
                case "implyT":
                case "andF": {
                    if (!node.left.isConflicted)
                        unreducedNodes.add(node.left);
                    if (!node.right.isConflicted)
                        unreducedNodes.add(node.right);
                    break;
                }
                case "andT":
                case "orF":
                case "implyF": {
                    if (!node.left.isConflicted)
                        unreducedNodes.add(node.left);
                    if (!node.left.left.isConflicted)
                        unreducedNodes.add(node.left.left);
                    break;
                }
                case "not": {
                    if (!node.left.isConflicted)
                        unreducedNodes.add(node.left);
                    break;
                }
                case "eq": {
                    if (!node.left.isConflicted)
                        unreducedNodes.add(node.left);
                    if (!node.left.left.isConflicted)
                        unreducedNodes.add(node.left.left);
                    if (!node.right.isConflicted)
                        unreducedNodes.add(node.right);
                    if (!node.right.left.isConflicted)
                        unreducedNodes.add(node.right.left);
                    break;
                }
            }
            return;
        }
        if (root.left != null) {
            reduce(root.left, node.copyWithChildren(), unreducedNodes);
        }
        if (root.right != null) {
            reduce(root.right, node.copyWithChildren(), unreducedNodes);
        }
    }


    public static PropositionNode atomicT(PropositionNode node, String leftProposition, String rightProposition) {
        switch (node.type) {
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
                //  PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
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
                // PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", true);
                PropositionNode llNode = new PropositionNode(rightProposition, false, false, "", false);
                node.left = lNode;
                lNode.father = node;
                lNode.left = llNode;
                llNode.father = lNode;
                break;
            }
            case "not": {
                //    PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
                PropositionNode lNode = new PropositionNode(leftProposition, false, false, "", !node.label);
                node.left = lNode;
                lNode.father = node;
                break;
            }
            case "eq": {
                //   PropositionNode node = new PropositionNode(this.proposition, this.isConflicted, this.isReduced, this.type, this.label);
                PropositionNode lNode1 = new PropositionNode(leftProposition, false, false, "", true);
                PropositionNode lNode2 = new PropositionNode(leftProposition, false, false, "", false);
                PropositionNode rNode1 = new PropositionNode(rightProposition, false, false, "", true);
                PropositionNode rNode2 = new PropositionNode(rightProposition, false, false, "", false);
                node.left = lNode1;
                node.right = lNode2;
                lNode1.father = node;
                lNode2.father = node;
                if (node.label == true) {
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

    public static void printCst(PropositionNode root){


    }
}
