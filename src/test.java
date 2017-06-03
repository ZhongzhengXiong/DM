
public class test {
    public static void main(String args[]) {
        String expression = " (((A \\imply B) \\imply A) \\imply A)";
        expression = expression.replaceAll(" ", "");
        int index1 = expression.indexOf("\\imply", 18);
        int index2 = expression.indexOf("\\imply", 19);
        System.out.println(index1);
        System.out.println(index2);
//        LinkedList<PropositionNode> nodes = new LinkedList<>();
//        reduce(nodes, new PropositionNode("ada", null, null, null, false));
//        PropositionNode node = nodes.getFirst();
//        System.out.println(node.proposition);
//        String type = "not";
//        switch (type) {
//            case "orT":
//            case "implyT":
//            case "andF": {
//               System.out.print(1);
//               break;
//            }
//            case "andT":
//            case "orF":
//            case "implyF": {
//                System.out.print(2);
//                break;
//            }
//            case "not": {
//                System.out.print(3);
//                break;
//            }
//            case "eq": {
//                System.out.print(4);
//                break;
//            }
//        }
//    }


        //public static void  reduce(LinkedList<PropositionNode> nodes, PropositionNode node){
        //     nodes.add(node);
        // }
    }
}
