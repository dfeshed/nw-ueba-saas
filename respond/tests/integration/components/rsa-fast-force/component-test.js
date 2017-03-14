import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-fast-force', 'Integration | Component | Fast Force Layout', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const user = { id: 'user1' };
const host = { id: 'host1' };
const ip1 = { id: '10.20.30.40' };
const ip2 = { id: '200.50.60.70' };
const domain = { id: 'g00gle.com' };

const nodes = [ user, host, ip1, ip2, domain ];

const links = [
  {
    id: `${user.id}-${host.id}`,
    source: user,
    target: host
  }, {
    id: `${host.id}-${ip1.id}`,
    source: host,
    target: ip1
  }, {
    id: `${ip1.id}-${ip2.id}`,
    source: ip1,
    target: ip2
  }, {
    id: `${ip2.id}-${domain.id}`,
    source: ip2,
    target: domain
  }];

const data = { nodes, links };

const filter = {
  nodeIds: [ nodes[0].id, nodes[1].id ],
  linkIds: [ links[2].id, links[3].id ]
};

test('it renders the correct number of nodes and links in DOM', function(assert) {

  this.set('data', data);
  this.render(hbs`{{rsa-fast-force data=data}}`);

  assert.equal(this.$('.rsa-force-layout').length, 1, 'Unable to find the root component DOM element.');
  assert.equal(this.$('.rsa-force-layout-link').length, links.length, 'Unable to find the link DOM elements.');
  assert.equal(this.$('.rsa-force-layout-node').length, nodes.length, 'Unable to find the node DOM elements.');
});

test('it applies a given filter to the data and DOM of every node & link', function(assert) {
  this.setProperties({ data, filter: null });
  this.render(hbs`{{rsa-fast-force data=data filter=filter}}`);
  const $el = this.$('.rsa-force-layout');

  assert.notOk(nodes[0].isHidden, 'Expected all nodes to not be hidden.');
  assert.notOk(nodes[1].isHidden, 'Expected all nodes to not be hidden.');
  assert.notOk(nodes[2].isHidden, 'Expected all nodes to not be hidden.');
  assert.notOk(nodes[3].isHidden, 'Expected all nodes to not be hidden.');
  assert.notOk(nodes[4].isHidden, 'Expected all nodes to not be hidden.');

  assert.notOk(links[0].isHidden, 'Expected all links to not be hidden.');
  assert.notOk(links[1].isHidden, 'Expected all links to not be hidden.');
  assert.notOk(links[2].isHidden, 'Expected all links to not be hidden.');
  assert.notOk(links[3].isHidden, 'Expected all links to not be hidden.');

  assert.notOk($el.find('.is-hidden').length, 'Expected not to find is-hidden class on any nodes or links.');

  this.set('filter', filter);
  return wait().then(() => {
    assert.notOk(nodes[0].isHidden);
    assert.notOk(nodes[1].isHidden);
    assert.ok(nodes[2].isHidden);
    assert.ok(nodes[3].isHidden);
    assert.ok(nodes[4].isHidden);

    assert.ok(links[0].isHidden);
    assert.ok(links[1].isHidden);
    assert.notOk(links[2].isHidden);
    assert.notOk(links[3].isHidden);

    const $hiddenLinks = $el.find('.rsa-force-layout-link.is-hidden');
    const $hiddenNodes = $el.find('.rsa-force-layout-node.is-hidden');
    assert.equal($hiddenLinks.length, links.length - filter.linkIds.length, 'Expected to find is-hidden class on all hidden links.');
    assert.equal($hiddenNodes.length, nodes.length - filter.nodeIds.length, 'Expected to find is-hidden class on all hidden nodes');
  });
});
