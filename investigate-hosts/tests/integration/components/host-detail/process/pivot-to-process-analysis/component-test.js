import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled, triggerEvent, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const callback = () => {};
const e = {
  clientX: 5,
  clientY: 2,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
let initState;
const wormhole = 'wormhole-context-menu';
module('Integration | Component | host-detail/process/process-tree/process-name', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  hooks.afterEach(function() {
    document.removeEventListener('contextmenu', callback);
  });

  test('it renders the process analysis item on right click', async function(assert) {
    assert.expect(2);

    this.set('serviceList', []);
    this.set('agentId', '1aaa');
    this.set('osType', 'windows');
    this.set('hostName', 'test-machine');
    this.set('contextSelection', 'test-machine');
    this.set('item', { name: 'test.exe', checksumSha256: 'aaa1', vpid: 123123 });
    await render(hbs`
    {{#host-detail/process/pivot-to-process-analysis
            serviceList=serviceList
            agentId=agentId
            osType=osType
            hostName=hostName
            contextSelection=item}}
            Process Analysis
     {{/host-detail/process/pivot-to-process-analysis}}
     {{context-menu}}
    `);
    triggerEvent('.content-context-menu', 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 1);
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(1) .context-menu__item__label`).textContent.trim(), 'Process Analysis');

    });
  });

  test('context menu not pressent for linux os type', async function(assert) {
    assert.expect(1);

    this.set('serviceList', []);
    this.set('agentId', '1aaa');
    this.set('osType', 'linux');
    this.set('hostName', 'test-machine');
    this.set('contextSelection', 'test-machine');
    this.set('item', { name: 'test.exe', checksumSha256: 'aaa1', vpid: 123123 });
    await render(hbs`
    {{#host-detail/process/pivot-to-process-analysis
            serviceList=serviceList
            agentId=agentId
            osType=osType
            hostName=hostName
            contextSelection=item}}
            Process Analysis
     {{/host-detail/process/pivot-to-process-analysis}}
     {{context-menu}}
    `);
    assert.equal(findAll('.content-context-menu').length, 0);
  });

  test('it navigates to process analysis page', async function(assert) {
    assert.expect(4);
    this.set('serviceId', '12345');
    this.set('serviceList', []);
    this.set('agentId', '1aaa');
    this.set('osType', 'windows');
    this.set('hostName', 'test-machine');
    this.set('contextSelection', 'test-machine');
    this.set('item', { name: 'test.exe', checksumSha256: 'aaa1', vpid: 123123 });

    new ReduxDataHelper(initState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .build();

    await render(hbs`
    {{#host-detail/process/pivot-to-process-analysis
            serviceList=serviceList
            agentId=agentId
            osType=osType
            hostName=hostName
            contextSelection=item}}
            Process Analysis
     {{/host-detail/process/pivot-to-process-analysis}}
     {{context-menu}}
    `);
    triggerEvent('.content-context-menu', 'contextmenu', e);
    return settled().then(() => {
      const actionSpy = sinon.spy(window, 'open');
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 1);
      click('.context-menu__item:nth-of-type(1)');
      return settled().then(() => {
        assert.ok(actionSpy.calledOnce);
        assert.ok(actionSpy.args[0][0].includes('vid=123123'));
        assert.ok(actionSpy.args[0][0].includes('sid=123456'));
        actionSpy.reset();
        actionSpy.restore();
      });
    });
  });

  test('it opens the service list modal window if service id is -1', async function(assert) {
    assert.expect(2);
    this.set('serviceId', '12345');
    this.set('serviceList', []);
    this.set('agentId', '1aaa');
    this.set('osType', 'windows');
    this.set('hostName', 'test-machine');
    this.set('contextSelection', 'test-machine');
    this.set('item', { name: 'test.exe', checksumSha256: 'aaa1', vpid: 123123 });
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    new ReduxDataHelper(initState)
      .serviceId('-1')
      .timeRange({ value: 7, unit: 'days' })
      .build();

    await render(hbs`
    {{#host-detail/process/pivot-to-process-analysis
            serviceList=serviceList
            agentId=agentId
            osType=osType
            hostName=hostName
            contextSelection=item}}
            Process Analysis
     {{/host-detail/process/pivot-to-process-analysis}}
     {{context-menu}}
    `);
    triggerEvent('.content-context-menu', 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 1);
      click('.context-menu__item:nth-of-type(1)');
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
      });
    });
  });
});
