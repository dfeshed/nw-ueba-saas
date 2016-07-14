import TreeNode from 'sa/utils/tree/node';
import { module, test } from 'qunit';

module('Unit | Utility | tree/node');

test('it can be instantiated as an orphan without parent and children', function(assert) {
  assert.expect(3);
  let node = TreeNode.create({
    value: 'myValue'
  });
  assert.equal(node.get('value'), 'myValue', 'its value is applied correctly');
  assert.equal(node.get('children.length'), 0, 'its children are initially empty');
  assert.equal(node.get('parent'), null, 'its parent is initially null');
});

test('children can be added and removed from it', function(assert) {
  assert.expect(5);

  let parent = TreeNode.create({ value: 0 }),
    child1 = TreeNode.create({ value: 1 }),
    child2 = TreeNode.create({ value: 2 });

  parent.addChild(child1).addChild(child2);

  assert.equal(parent.get('children.firstObject'), child1, 'its child is inserted into the children list correctly');
  assert.equal(parent.get('children.lastObject'), child2, 'its child is inserted into the children list correctly');
  assert.equal(child1.get('parent'), parent, 'the child parent is set correctly');

  parent.removeChild(child1);
  assert.equal(parent.get('children.length'), 1, 'its child is removed from the children list correctly');
  assert.equal(child1.get('parent'), null, 'the child parent is cleared correctly');
});
