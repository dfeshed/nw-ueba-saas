import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('rsa-tree', 'Integration | Component | rsa tree', {
  integration: true,
  resolver: engineResolverFor('live-content')
});

const states = [
  { id: 'MA', name: 'Massachusetts', children: [ { id: 'bos', name: 'Boston' } ] },
  { id: 'TX', name: 'Texas', children: [ { id: 'aus', name: 'Austin' } ] },
  { id: 'CA', name: 'California', children: [ { id: 'oak', name: 'Oakland' } ] }
];

const categories = [
  {
    'val': 'threat',
    'title': 'THREAT',
    'subCategories': [
      {
        'val': 'attack phase',
        'title': 'Attack Phase',
        'subCategories': [
          {
            'val': 'reconaissance',
            'title': 'Reconaissance',
            'subCategories': null
          },
          {
            'val': 'command control',
            'title': 'Command and Control',
            'subCategories': null
          },
          {
            'val': 'action objectives',
            'title': 'Action on Objectives',
            'subCategories': [
              {
                'val': 'data sabotage',
                'title': 'Data Sabotage',
                'subCategories': null
              },
              {
                'val': 'denial of service',
                'title': 'Denial of Service',
                'subCategories': null
              }
            ]
          }
        ],
        'order': 0
      },
      {
        'val': 'malware',
        'title': 'Malware',
        'subCategories': [
          {
            'val': 'trojans',
            'title': 'Remote Access Trojans',
            'subCategories': null
          },
          {
            'val': 'crimeware',
            'title': 'Crimeware',
            'subCategories': null
          }
        ],
        'order': 1
      }
    ],
    'order': 0
  }
];

test('The Tree renders', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-tree}}`);
  assert.equal(this.$('.rsa-tree.root').length, 1, 'The RSA Tree component has been rendered');
});

test('The Tree contains a root node for each top-level object in the data array', function(assert) {
  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states}}`);
  assert.equal(this.$('.rsa-tree.root > .rsa-tree-node').length, 3, 'There are three root nodes in the RSA Tree');
  assert.equal(this.$('.rsa-tree .rsa-tree-node').length, 6, 'There are six total nodes in the RSA Tree');
});

test('The Tree displays a name for each node', function(assert) {
  assert.expect(7);
  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states}}`);

  const names = this.$('.rsa-tree-node .node-name');

  assert.equal(names.length, 6, 'The tree has six node names');
  assert.equal(this.$(names[0]).text(), 'Massachusetts', 'The first node has the name Massachusetts');
  assert.equal(this.$(names[1]).text(), 'Boston', 'The second node has the name Boston');
  assert.equal(this.$(names[2]).text(), 'Texas', 'The third node has the name Texas');
  assert.equal(this.$(names[3]).text(), 'Austin', 'The fourth node has the name Austin');
  assert.equal(this.$(names[4]).text(), 'California', 'The fifth node has the name California');
  assert.equal(this.$(names[5]).text(), 'Oakland', 'The sixth node has the name Oakland');
});

test('The Tree uses the "children" property by default to create nested nodes', function(assert) {
  assert.expect(2);
  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states}}`);

  const nodesWithChildren = this.$('.rsa-tree-node.has-children');
  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  assert.equal(nodesWithChildren.length, 3, 'There are three nodes with child nodes');
  assert.equal(subTreeNodes.length, 3, 'The tree has a total of three nested / child nodes');
});

test('The Tree ignores child node arrays if they are under a different property name than "children" (by default)', function(assert) {
  assert.expect(2);
  this.categories = categories;
  this.render(hbs`{{rsa-tree nodes=categories}}`);

  const nodesWithChildren = this.$('.rsa-tree-node.has-children');
  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  assert.equal(nodesWithChildren.length, 0, 'There are no nodes with child nodes');
  assert.equal(subTreeNodes.length, 0, 'The tree has zero nested / child nodes');
});

test('The Tree does not display child nodes by default (i.e., branches are collapsed)', function(assert) {
  assert.expect(4);

  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states}}`);

  const expandedNodes = this.$('.rsa-tree-node.expanded');
  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  assert.equal(expandedNodes.length, 0, 'None of the nodes are expanded');

  subTreeNodes.each((index, node) => {
    assert.equal(this.$(node).css('height'), '0px', 'The nested node is not visible');
  });
});

test('The Tree displays a fully expanded tree when the \"expandedAll\" option is used', function(assert) {
  assert.expect(5);
  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states expandAll=true}}`);

  const expandedNodes = this.$('.rsa-tree-node.expanded');
  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  assert.equal(expandedNodes.length, 3, 'All three top level nodes are expanded');
  assert.equal(subTreeNodes.length, 3, 'The tree has three nested nodes');
  subTreeNodes.each((index, node) => {
    assert.equal(this.$(node).is(':visible'), true, 'The nested node is visible');
  });
});

test('The Tree does not evaluate children nodes when the child array property name is not "children"', function(assert) {
  assert.expect(1);
  this.categories = categories;
  this.render(hbs`{{rsa-tree nodes=categories nodeChildrenProperty="subCategories"}}`);

  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  assert.equal(subTreeNodes.length, 9, 'The Tree has no nested nodes');
});

test('The Tree looks up and uses the \"nodeChildrenProperty\" for creating nested/sub nodes', function(assert) {
  assert.expect(1);
  this.categories = categories;
  this.render(hbs`{{rsa-tree nodes=categories nodeChildrenProperty="subCategories"}}`);

  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  assert.equal(subTreeNodes.length, 9, 'The Tree has nine nested nodes');
});

test('The Tree does not display node names when the data has no "name" property', function(assert) {
  assert.expect(10);
  this.categories = categories;
  this.render(hbs`{{rsa-tree nodes=categories nodeChildrenProperty="subCategories"}}`);

  const names = this.$('.rsa-tree-node .node-name');
  names.each((index, node) => {
    assert.notOk(this.$(node).text().trim(), 'The node has no name');
  });
});

test('The Tree looks up and uses the display name via the \"nodeDisplayNameProperty\" option', function(assert) {
  assert.expect(10);
  this.categories = categories;
  this.render(hbs`{{rsa-tree 
    nodes=categories 
    nodeChildrenProperty="subCategories"
    nodeDisplayNameProperty="title"}}`);

  const names = this.$('.rsa-tree-node .node-name');
  names.each((index, node) => {
    assert.ok(this.$(node).text().trim(), 'The node has a name');
  });
});

test('Clicking on node\'s toggle control will toggle expand and collapse of the sub nodes', function(assert) {
  assert.expect(10);
  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states}}`);

  const nodesWithChildren = this.$('.rsa-tree-node.has-children');
  const expandedNodes = this.$('.rsa-tree-node.expanded');
  const subTreeNodes = this.$('.rsa-tree .rsa-tree-node .rsa-tree .rsa-tree-node');

  // establish baseline that there are no expanded nodes
  assert.equal(expandedNodes.length, 0, 'None of the nodes are expanded');

  subTreeNodes.each((index, node) => {
    // all sub nodes are not visible
    assert.equal(this.$(node).css('height'), '0px', 'The nested nodes are not visible');
  });

  nodesWithChildren.each((index, node) => {
    this.$(node).find('.toggle').click(); // click the toggle "button" for all has-children nodes
  });

  subTreeNodes.each((index, node) => { // check to make sure all sub nodes are now visible
    assert.equal(this.$(node).is(':visible'), true, 'The nested nodes are all visible');
  });

  nodesWithChildren.each((index, node) => {
    this.$(node).find('.toggle').click(); // click the toggle "button" for all has-children nodes
  });

  subTreeNodes.each((index, node) => { // check to make sure all sub nodes are now invisible again
    assert.equal(this.$(node).css('height'), '0px', 'The nested nodes are not visible');
  });
});

test('Clicking on a node selects it; clicking again deselects it', function(assert) {
  assert.expect(3);
  this.set('states', states);
  this.selectedNode = null;

  this.render(hbs`{{rsa-tree nodes=states selectedNode=selectedNode}}`);

  const node = this.$('.rsa-tree-node > .node-name').first();

  assert.strictEqual(this.$('.rsa-tree-node.selected').length, 0, 'There is no selected node');
  node.click();
  assert.strictEqual(this.$('.rsa-tree-node.selected').length, 1, 'There is one selected node');
  node.click();
  assert.strictEqual(this.$('.rsa-tree-node.selected').length, 0, 'There is no selected node');
});

test('Clicking a non-selected node deselects any other selected node', function(assert) {
  assert.expect(4);
  this.set('states', states);
  this.render(hbs`{{rsa-tree nodes=states expandAll=true}}`);

  const nodes = this.$('.rsa-tree-node > .node-name');

  assert.strictEqual(this.$('.rsa-tree-node.selected').length, 0, 'There is no selected node');
  nodes.first().click();
  assert.strictEqual(nodes.first().closest('.rsa-tree-node').hasClass('selected'), true, 'First node is selected');
  nodes.last().click();
  assert.strictEqual(nodes.first().closest('.rsa-tree-node').hasClass('selected'), false, 'First node is not selected');
  assert.strictEqual(nodes.last().closest('.rsa-tree-node').hasClass('selected'), true, 'Last node is selected');
});

test('Setting the selectedNode on the root tree with a value property selects the appropriate leaf node', function(assert) {
  assert.expect(1);
  this.set('states', states);
  this.selectedNode = 'aus';

  this.render(hbs`{{rsa-tree nodes=states selectedNode=selectedNode}}`);

  assert.strictEqual(this.$('.rsa-tree-node.selected > .node-name').text().trim(), 'Austin', 'The "Austin" node is selected');

});

