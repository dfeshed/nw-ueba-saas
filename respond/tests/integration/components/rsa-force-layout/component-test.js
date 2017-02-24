import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('rsa-force-layout', 'Integration | Component | Force Layout', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('it renders the correct number of nodes and links in DOM', function(assert) {

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

  this.set('data', data);
  this.render(hbs`{{rsa-force-layout data=data}}`);

  assert.equal(this.$('.rsa-force-layout').length, 1, 'Unable to find the root component DOM element.');
  assert.equal(this.$('.rsa-force-layout-link').length, links.length, 'Unable to find the link DOM elements.');
  assert.equal(this.$('.rsa-force-layout-node').length, nodes.length, 'Unable to find the node DOM elements.');
});
