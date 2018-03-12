package fortscale.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<TreeNode<T>> features;

	@JsonCreator
	public Tree(@JsonProperty("name") String name,
				@JsonProperty("root") TreeNode<T> root) {
		this.name = name;
		this.root = root;
		this.features = new HashSet<>();
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

	public Set<TreeNode<T>> getFeatures() {
		return features;
	}

	public void setFeatures(Set<TreeNode<T>> features) {
		this.features = features;
	}
}
