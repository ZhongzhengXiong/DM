import java.util.LinkedList;

/**
 * Created by XiongZZ on 2017/6/1.
 */
public class test {
    public static void main(String args[]){
//        LinkedList<PropositionNode> nodes = new LinkedList<>();
//        reduce(nodes, new PropositionNode("ada", null, null, null, false));
//        PropositionNode node = nodes.getFirst();
//        System.out.println(node.proposition);
        String type = "not";
        switch (type) {
            case "orT":
            case "implyT":
            case "andF": {
               System.out.print(1);
               break;
            }
            case "andT":
            case "orF":
            case "implyF": {
                System.out.print(2);
                break;
            }
            case "not": {
                System.out.print(3);
                break;
            }
            case "eq": {
                System.out.print(4);
                break;
            }
        }
    }

    //public static void  reduce(LinkedList<PropositionNode> nodes, PropositionNode node){
   //     nodes.add(node);
   // }
}
