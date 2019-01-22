import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { render, waitUntil, findAll, click, triggerEvent, settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { snapShot } from '../../../../../data/data';
import sinon from 'sinon';

let setState;

const callback = () => {};
const e = {
  clientX: 10,
  clientY: 10,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const wormhole = 'wormhole-context-menu';


module('Integration | Component | host detail header titlebar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };

    // Right click setup
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);

    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
  });
  test('Should render the hostname properly', async function(assert) {
    new ReduxDataHelper(setState)
      .hostName('XYZ')
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    assert.equal(this.$('.host-name').text(), 'XYZ', 'Rendered the hostname properly');
  });
  test('Should call action when the tab is clicked', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    assert.expect(1);
    new ReduxDataHelper(setState)
      .hostName('XYZ')
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    this.$('.rsa-nav-tab')[3].click();
    await waitUntil(() => {
      return redux.getState().endpoint.visuals.activeHostDetailTab === 'FILES';
    }, { timeout: 6000 });
    assert.equal(this.$('.rsa-nav-tab.is-active').text().trim().toUpperCase(), 'FILES', 'Rendered the tab name that is passed');
  });
  test('Show right panel button is present when Details tab is selected', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .hostName('XYZ')
      .isDetailRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    assert.equal(findAll('.open-properties').length, 1, 'Show/Hide right panel button is present');
  });

  test('Right panel button is hidden when Process tab is selected', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .hostName('XYZ')
      .isDetailRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    await click(findAll('.rsa-nav-tab')[1]);
    assert.equal(findAll('.open-properties').length, 0, 'Right panel button is hidden');
  });

  test('it redirects to investigate on doing the Analyze events', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    new ReduxDataHelper(setState)
      .hostName('XYZ')
      .build();
    await render(hbs`{{host-detail/header/titlebar}}{{context-menu}}`);
    assert.equal(this.$('.host-name').text(), 'XYZ', 'Rendered the hostname properly');
    triggerEvent(findAll('.host-name')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      assert.equal(menuItems.length, 1, '1 Context menu options rendered');
      click(`#${menuItems[0].id}`);
      return settled().then(() => {
        assert.ok(actionSpy.calledOnce);
        // make sure that is navigating to investigate navigate
        assert.ok(actionSpy.args[0][0].includes('/navigate/query'));
        actionSpy.resetHistory();
        actionSpy.restore();
      });
    });
  });
});
