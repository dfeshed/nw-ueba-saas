import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, find, findAll, render } from '@ember/test-helpers';
import Service from '@ember/service';
import rsvp from 'rsvp';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as ACTION_TYPES from 'context/actions/types';

const entityType = 'IP';
const entityId = '10.20.30.40';
const metaMap = {
  IM: {
    code: 0,
    data: {
      [entityType]: ['foo', 'bar', 'baz']
    }
  },
  CORE: {
    code: 0,
    data: {
      [entityType]: ['foo', 'bar', 'baz']
    }
  }
};

const contextService = Service.extend({
  metas(endpointId) {
    return rsvp.resolve(metaMap[endpointId]);
  }
});

module('Integration | Component | context tooltip actions', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'context', 'service:context');
    this.owner.register('service:context', contextService);
    initialize(this.owner);
  });

  test('it renders an actionsList based on the entityType', async function(assert) {
    assert.expect(6);

    const hideAction = () => {
      assert.ok(true, 'hideAction was invoked');
    };
    const addToListAction = (entity = {}) => {
      assert.ok(true, 'addToListAction was invoked');
      assert.equal(entity.id, entityId, 'Expected addToListAction to receive entity object.');
      assert.equal(entity.type, entityType, 'Expected addToListAction to receive entity object.');
    };

    this.setProperties({
      entityType,
      entityId,
      hideAction,
      addToListAction
    });

    await render(hbs`{{context-tooltip/actions
    entityType=entityType
    entityId=entityId
    hideAction=hideAction
    addToListAction=addToListAction}}`);


    assert.equal(findAll('.rsa-context-tooltip-actions').length, 1, 'Expected to find root DOM node');
    assert.equal(findAll('.action').length, 4, 'Expected to find 4 action menu options');
      // Click the first option, that's the Add To List option
    await click(findAll('.action')[0]);
  });

  test('it only shows the Pivot to Endpoint link for IPs, HOSTs & MAC addresses', async function(assert) {
    this.setProperties({
      entityType: 'IP',
      entityId: '10.20.30.40'
    });

    await render(hbs`{{context-tooltip/actions
    entityType=entityType
    entityId=entityId}}`);

    assert.ok(findAll('.js-test-endpoint-link').length, 'Expected to find endpoint link for IP');

    this.setProperties({
      entityType: 'HOST',
      entityId: 'MACHINE1'
    });
    assert.ok(findAll('.js-test-endpoint-link').length, 'Expected to find endpoint link for HOST');

    this.setProperties({
      entityType: 'MAC_ADDRESS',
      entityId: 'aa:bb:cc:dd'
    });
    assert.ok(findAll('.js-test-endpoint-link').length, 'Expected to find endpoint link for MAC address');

    this.setProperties({
      entityType: 'USER',
      entityId: 'username1'
    });
    assert.notOk(findAll('.js-test-endpoint-link').length, 'Expected to NOT find endpoint link for user');

    this.setProperties({
      entityType: 'IP',
      entityId: null
    });
    assert.notOk(findAll('.js-test-endpoint-link').length, 'Expected to NOT find endpoint link for empty IP');
  });
  test('it only shows the Pivot to Investigate link for IPs, HOSTs, MACs, DOMAINs, USERs & FILE_NAMEs', async function(assert) {

    this.setProperties({
      entityType: 'IP',
      entityId: '10.20.30.40'
    });

    await render(hbs`{{context-tooltip/actions
      entityType=entityType
      entityId=entityId}}`);

    assert.ok(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to find investigate link for IP');
    this.setProperties({
      entityType: 'HOST',
      entityId: 'MACHINE1'
    });

    assert.ok(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to find investigate link for HOST');
    this.setProperties({
      entityType: 'MAC_ADDRESS',
      entityId: 'aa:bb:cc:dd:ee:ff'
    });

    assert.ok(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to find investigate link for MAC with 6-pairs');
    this.setProperties({
      entityType: 'MAC_ADDRESS',
      entityId: 'aa:bb:cc:dd:ee:ff:11:22'
    });

    assert.notOk(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to NOT find investigate link for MAC with 8-pairs');
    this.setProperties({
      entityType: 'DOMAIN',
      entityId: 'www.g00gle.com'
    });

    assert.ok(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to find investigate link for DOMAIN');
    this.setProperties({
      entityType: 'USER',
      entityId: 'username1'
    });

    assert.ok(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to find investigate link for USER');
    this.setProperties({
      entityType: 'FILE_NAME',
      entityId: 'foo.pdf'
    });

    assert.ok(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to find investigate link for FILE_NAME');
    this.setProperties({
      entityType: 'IP',
      entityId: null
    });

    assert.notOk(findAll('.js-test-pivot-to-investigate-link').length, 'Expected to NOT find investigate link for empty IP');
  });

  test('it only shows the Pivot to Archer link for IPs, HOSTs', async function(assert) {
    this.setProperties({
      entityType: 'IP',
      entityId: '10.20.30.40'
    });

    await render(hbs`{{context-tooltip/actions
      entityType=entityType
      entityId=entityId}}`);

    assert.ok(findAll('.js-test-pivot-to-archer-link').length, 'Expected to find archer link for IP');
    this.setProperties({
      entityType: 'HOST',
      entityId: 'MACHINE1'
    });

    assert.ok(findAll('.js-test-pivot-to-archer-link').length, 'Expected to find archer link for HOST');
    this.setProperties({
      entityType: 'MAC_ADDRESS',
      entityId: 'aa:bb:cc:dd:ee:ff'
    });

    assert.notOk(findAll('.js-test-pivot-to-archer-link').length, 'Expected to NOT find archer link for MAC');
    this.setProperties({
      entityType: 'DOMAIN',
      entityId: 'www.g00gle.com'
    });

    assert.notOk(findAll('.js-test-pivot-to-archer-link').length, 'Expected to NOT find archer link for DOMAIN');
    this.setProperties({
      entityType: 'USER',
      entityId: 'username1'
    });

    assert.notOk(findAll('.js-test-pivot-to-archer-link').length, 'Expected to NOT find archer link for USER');
    this.setProperties({
      entityType: 'FILE_NAME',
      entityId: 'foo.pdf'
    });

    assert.notOk(findAll('.js-test-pivot-to-archer-link').length, 'Expected to NOT find archer link for FILE_NAME');
    this.setProperties({
      entityType: 'IP',
      entityId: null
    });

    assert.notOk(findAll('.js-test-pivot-to-archer-link').length, 'Expected to NOT find archer link for empty IP');
    this.setProperties({
      entityType: 'HOST',
      entityId: null
    });

    assert.notOk(findAll('.js-test-pivot-to-archer-link').length, 'Expected to NOT find archer link for empty HOST');
  });

  test('the query in the Pivot to Investigate link includes the meta keys from the meta map', async function(assert) {
    const entityId = '192.168.101.66';
    const entityType = 'IP';

    this.setProperties({
      entityType,
      entityId
    });

    await render(hbs`{{context-tooltip/actions
      entityType=entityType
      entityId=entityId
    }}`);

    const url = find('a[href]').getAttribute('href');
    assert.ok(!!url.match(/query/), 'Expected to find a link to an investigation query');
    Object.keys(metaMap).forEach((key) => {
      assert.ok(!!url.indexOf(key) > -1, `Expected to find meta key ${key} in query URL`);
    });
  });

  test('Enable/Disable Pivot to Archer link based on summary data', async function(assert) {
    const entityId = '192.168.101.66';
    const entityType = 'IP';

    // Test Model Summary to be dispatched to context-tooltip actions to enable pivot to archer link in hover over
    let modelSummary = { name: 'Archer', count: null, url: 'www.google.com', criticality: 'Low', riskRating: 'Medium' };

    const redux = this.owner.lookup('service:redux');

    redux.dispatch({
      type: ACTION_TYPES.GET_SUMMARY_DATA,
      payload: [modelSummary]
    });

    this.setProperties({
      entityType,
      entityId
    });

    await render(hbs`{{context-tooltip/actions
      entityType=entityType
      entityId=entityId
    }}`);

    assert.notOk(findAll('a.disabled').length, 'Expected not to find Pivot To Archer link disabled');
    assert.equal(findAll('span')[1].title, '', 'Expected not to find tooltip for Pivot To Archer link');

    // Test Model Summary to be dispatched to context-tooltip actions to disable pivot to archer link in hover over
    modelSummary = { name: 'Archer', count: null, url: null };

    redux.dispatch({
      type: ACTION_TYPES.GET_SUMMARY_DATA,
      payload: [modelSummary]
    });

    this.setProperties({
      entityType,
      entityId
    });

    assert.ok(findAll('a.disabled').length, 'Expected to find Pivot To Archer link disabled');
    assert.equal(findAll('span')[1].title, 'Add or enable Archer or Data is not available.', 'Expected to find tooltip for Pivot To Archer link');
  });
});
