package fortscale.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This generic will represent Tree data structure
 * Created by idanp on 2/11/2015.
 */
public class TreeNode<T> implements Iterable<TreeNode<T>> {

	private T data;
	private TreeNode<T> parent;
	private ArrayList<TreeNode<T>> childrens;




	//CTO
	public TreeNode(T data)
	{
		this.data = data;
		this.childrens = new ArrayList<>();


	}

	public void setParent(TreeNode<T> parent)
	{
		this.parent = parent;
	}


	public boolean setChaild(TreeNode<T> child)
	{
		return this.childrens.add(child);
	}

	public boolean isRoot()
	{
		return parent == null;
	}

	/**
	 * This method based on the equals comparator of the template T - Note that for your use case you need to consider it.
	 * @param dataToFind
	 * @return
	 */
	public TreeNode<T> peekFromTree(TreeNode<T> dataToFind)
	{
		TreeNode<T> result = null;


		if (dataToFind.getData().equals(this.data))
			return this;
		if (this.childrens.size() == 0)
			return null;
		for (TreeNode<T> child : this.childrens)
		{
			result = child.peekFromTree(dataToFind);
			if ( result != null)
				break;

		}
		return result;
	}

	/**
	 * This method will return the list of leaf of current tree
	 * @return
	 */
	public ArrayList<TreeNode<T>> getListOfLeaf ()
	{
		ArrayList<TreeNode<T>> listOfLeaf = new ArrayList<>();

		if (childrens.size() == 0)
			listOfLeaf.add(this);

		for (TreeNode<T> child : childrens)
		{
			if (child.getChildrens().size() == 0)
				listOfLeaf.add(child);
			else
				listOfLeaf.addAll(child.getListOfLeaf());


		}

		return listOfLeaf;

	}


	public Iterator<TreeNode<T>> iterator ()
	{
		Iterator<TreeNode<T>> iter = this.childrens.iterator();
		return iter;

	}

	public T getData()
	{
		return this.data;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public ArrayList<TreeNode<T>> getChildrens() {
		return childrens;
	}


}
