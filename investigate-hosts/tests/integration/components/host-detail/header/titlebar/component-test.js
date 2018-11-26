import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { render, waitUntil, findAll, click } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { snapShot } from '../../../../../data/data';

let setState;

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
    });
    assert.equal(this.$('.rsa-nav-tab.is-active').text().trim().toUpperCase(), 'FILES', 'Rendered the tab name that is passed');
  });
  test('Show right panel button is present when Details tab is selected', async function(assert) {
    this.set('showRightPanelButton', true);
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .hostName('XYZ')
      .isRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail/header/titlebar showRightPanelButton=showRightPanelButton}}`);
    assert.equal(findAll('.open-properties').length, 1, 'Show/Hide right panel button is present');
  });

  test('Right panel button is hidden when Process tab is selected', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .hostName('XYZ')
      .isRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    await click(findAll('.rsa-nav-tab')[1]);
    assert.equal(findAll('.open-properties').length, 0, 'Right panel button is hidden');
  });
});