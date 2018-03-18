package fortscale.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ParentIterator<T> implements Iterator<TreeNode<T>> {

	private TreeNode<T> treeNode;
	private Queue<TreeNode<T>> queue;

	public ParentIterator(TreeNode<T> treeNode){
		this.treeNode = treeNode;
		this.queue = new LinkedList<>();
		addParentToQueue(treeNode);
	}

	@Override
	public boolean hasNext() {
		return !this.queue.isEmpty();
	}

	@Override
	public TreeNode<T> next() {

		TreeNode<T> parent = queue.remove();
		addParentToQueue(parent);
		return parent;
	}

	public void addParentToQueue(TreeNode<T> treeNode){
		TreeNode<T> parent = treeNode.getParent();
		if(parent != null) {
			this.queue.add(treeNode.getParent());
		}

	}
}
