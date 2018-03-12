//package fortscale.utils;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//import static junit.framework.Assert.assertTrue;
//
//public class TreeNodeTest {
//
//	@Test
//	public void test_getListOfLeaf()
//	{
//		TreeNode<Integer> root = new TreeNode<>(new Integer(0));
//		TreeNode<Integer> node1 = new TreeNode<>(new Integer(1));
//		TreeNode<Integer> node2 =  new TreeNode<>(new Integer(2));
//		TreeNode<Integer> node3 = new TreeNode<>(new Integer(3));
//		TreeNode<Integer> node4 =  new TreeNode<>(new Integer(4));
//		TreeNode<Integer> node5 = new TreeNode<>(new Integer(5));
//		TreeNode<Integer> node6 =  new TreeNode<>(new Integer(6));
//		TreeNode<Integer> node7 = new TreeNode<>(new Integer(7));
//		TreeNode<Integer> node8 =  new TreeNode<>(new Integer(8));
//
//		root.setChaild(node1);
//		root.setChaild(node2);
//		node1.setParent(root);
//		node2.setParent(root);
//
//		node1.setChaild(node3);
//		node1.setChaild(node4);
//		node2.setChaild(node5);
//
//		node3.setParent(node1);
//		node4.setParent(node1);
//
//		node5.setParent(node2);
//
//		node4.setChaild(node6);
//		node4.setChaild(node7);
//
//		node6.setParent(node4);
//		node7.setParent(node4);
//
//
//		ArrayList<Node<Integer>> actualListofleaf =root.getListOfLeaf();
//		ArrayList<Node<Integer>> expextedListofleaf =new ArrayList<>();
//		expextedListofleaf.add(node5);
//		expextedListofleaf.add(node3);
//		expextedListofleaf.add(node6);
//		expextedListofleaf.add(node7);
//
//
//		boolean assertFlag = true;
//		for (Node<Integer> expectedLeaf : expextedListofleaf)
//		{
//			boolean innerFlag = false;
//			for (Node<Integer> actualLeaf : actualListofleaf)
//			{
//				innerFlag = expectedLeaf.getData().equals(actualLeaf.getData());
//				if(innerFlag) break;
//			}
//			assertFlag = innerFlag;
//
//			if (!assertFlag) break;
//
//		}
//
//		assertTrue(assertFlag);
//
//
//	}
//
//}
