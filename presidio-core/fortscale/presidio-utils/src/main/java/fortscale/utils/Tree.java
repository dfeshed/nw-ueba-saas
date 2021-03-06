package fortscale.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.map.HashedMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@JsonAutoDetect(
		creatorVisibility = JsonAutoDetect.Visibility.ANY,
		fieldVisibility = JsonAutoDetect.Visibility.NONE,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class Tree<T> {

	private String name;
	private TreeNode<T> root;

	@JsonCreator
	public Tree(@JsonProperty("name") String name,
				@JsonProperty("root") TreeNode<T> root) {
		this.name = name;
		this.root = root;
		fillTreeInTreeNodes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public TreeNode<T> getRoot() {
		return root;
	}

	public void setRoot(TreeNode<T> root) {
		this.root = root;
	}

	/**
	 * fill tree of each treeNode
	 */
	public void fillTreeInTreeNodes() {
		root.getDescendantStreamIncludingCurrentNode().forEach(node -> {
			node.setTree(this);
		});
	}

}
