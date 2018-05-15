import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import { findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

let setState;

const entityTypes = ['host', 'ip', 'mac_address', 'user', 'domain', 'file_name', 'file_hash'];
const user = { id: 'user1', type: 'user' };
const host = { id: 'host1', type: 'host' };
const ip1 = { id: '10.20.30.40', type: 'ip' };
const ip2 = { id: '200.50.60.70', type: 'ip' };
const domain = { id: 'g00gle.com', type: 'domain' };

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

module('Integration | Component | Incident Entities Force Layout', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      const fullState = { respond: { incident: state } };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  test('it includes the source and target classes on the lines/links', async function(assert) {
    setState({});
    this.set('data', data);
    await render(hbs`{{rsa-incident/entities-force class="rsa-incident-entities" data=data}}`);
    assert.expect(links.length * 2); // two assertions per link
    findAll('.rsa-force-layout-link').forEach((el, index) => {
      assert.ok(el.classList.contains(`source-${links[index].source.type}`), 'The link contains a source class');
      assert.ok(el.classList.contains(`target-${links[index].target.type}`), 'The link contains a target class');
    });
  });

  test('it includes the type as a class name on the node', async function(assert) {
    assert.expect(nodes.length);
    setState({
      visibleEntityTypes: entityTypes
    });
    this.set('data', data);
    await render(hbs`{{rsa-incident/entities-force class="rsa-incident-entities" data=data}}`);
    findAll('.rsa-force-layout-node').forEach((el) => {
      assert.ok(_.intersection(el.classList, entityTypes).length === 1, 'One of the entity types appears as a class name on the component');
    });
  });

  test('it shows no hidden entities as class names when they are all included in visibleEntitytTypes', async function(assert) {
    assert.expect(entityTypes.length);
    setState({
      visibleEntityTypes: entityTypes
    });
    this.set('data', data);
    await render(hbs`{{rsa-incident/entities-force class="rsa-incident-entities" data=data}}`);
    entityTypes.forEach((type) => {
      assert.equal(findAll(`.rsa-incident-entities.${type}`).length, 0, `There is no ${type}-hidden class`);
    });
  });

  test('it shows all entities as hidden class names when none are included in visibleEntitytTypes', async function(assert) {
    assert.expect(entityTypes.length);
    setState({
      visibleEntityTypes: []
    });
    this.set('data', data);
    await render(hbs`{{rsa-incident/entities-force class="rsa-incident-entities" data=data}}`);
    entityTypes.forEach((type) => {
      assert.equal(findAll(`.rsa-incident-entities.${type}-hidden`).length, 1, `There is a ${type}-hidden class`);
    });
  });
});

