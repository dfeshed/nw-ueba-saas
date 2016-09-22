import Tree from 'sa/utils/tree/tree';
import TreeNode from 'sa/utils/tree/node';
import { module, test } from 'qunit';

module('Unit | Utility | tree/tree');

test('it can be created and then assigned a root', function(assert) {
  assert.expect(2);

  let tree = Tree.create();
  assert.ok(tree);

  let node = TreeNode.create({ value: 'rootNode' });
  tree.add(node);
  assert.equal(tree.get('root'), node, 'root was updated successfully');
});

test('it can find a node by using native triple equals', function(assert) {
  let tree = Tree.create();
  let node1 = TreeNode.create({ value: 'foo' });
  let node2 = TreeNode.create({ value: 'bar' });

  tree.add(node1).add(node2);
  assert.equal(tree.find('bar'), node2, 'node was found successfully without a compare function');
});

test('it can find a node by using the node value\'s \'isEqual\' method', function(assert) {
  const isEqual = function(value) {
    return this.raw === value;
  };

  let tree = Tree.create();
  let node1 = TreeNode.create({
    value: {
      raw: 'foo',
      isEqual
    }
  });
  let node2 = TreeNode.create({
    value: {
      raw: 'bar',
      isEqual
    }
  });

  tree.add(node1).add(node2);
  assert.equal(tree.find('bar'), node2, 'node was found successfully without a compare function');
});

test('it can find a nested node by using the node value\'s \'isEqual\' method', function(assert) {
  const isEqual = function(value) {
    return this.raw === value;
  };

  const tree = Tree.create();
  const node1 = TreeNode.create({
    value: {
      raw: 'foo',
      isEqual
    }
  });
  const node2 = TreeNode.create({
    value: {
      raw: 'bar',
      isEqual
    }
  });
  const node3 = TreeNode.create({
    value: {
      raw: 'baz',
      isEqual
    }
  });

  tree.add(node1).add(node2, node1).add(node3, node2);
  assert.ok(tree.find('baz') === node3, 'nested node was found successfully');
});
