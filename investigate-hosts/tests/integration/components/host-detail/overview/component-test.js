import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render, click, waitUntil, settled } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import { lookupData } from '../../../../integration/components/state/visual.lookupData';
import { linux } from '../../../../integration/components/state/overview.hostdetails';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import policyData from '../../../../data/subscriptions/policy/data';

let initState;

module('Integration | Component | host-detail/overwiew', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });


  test('  The risk panel populating with alerts and incidents', async function(assert) {
    new ReduxDataHelper(initState)
      .lookupData(lookupData)
      .activeHostPropertyTab('ALERT')
      .policy(policyData)
      .host(linux.overview.hostDetails).build();
    await render(hbs`{{host-detail/overview domIsReady=true}}`);
    assert.equal(findAll('.host-properties-box .host-title-bar .rsa-nav-tab:nth-child(2) div.label')[0].textContent.trim(), 'Alerts (1)', 'Alerts tab is appearing in overview page');
    assert.equal(findAll('.host-properties-box .host-title-bar .rsa-nav-tab:nth-child(3) div.label')[0].textContent.trim(), 'Incidents (2)', 'Incidents tab is appearing in overview page');
    assert.equal(findAll('.host-properties-box .risk-properties-panel__content').length, 1, 'Risk panel populated with one available alert');
  });

  skip('Toggling the alerts/incidents tabs', async function(assert) {
    new ReduxDataHelper(initState)
      .lookupData(lookupData)
      .activeHostPropertyTab('ALERT')
      .host(linux.overview.hostDetails).build();
    await render(hbs`{{host-detail/overview domIsReady=true}}`);
    waitUntil(() => find('.host-properties-box .host-title-bar'));
    await click('.host-properties-box .host-title-bar .rsa-nav-tab:nth-child(3)');
    return settled().then(() => {
      assert.equal(findAll('.host-properties-box .host-title-bar .rsa-nav-tab:nth-child(3).is-active').length, 1, 'toggling of alerts and incidents tabs achieved.');
      assert.equal(findAll('.host-properties-box .risk-properties-panel__content .rsa-content-accordion').length, 2, 'Risk panel populated with two available Incidents');
    });
  });

  test('error message when context server is not reachable', async function(assert) {
    new ReduxDataHelper(initState)
      .lookupData(lookupData)
      .activeHostPropertyTab('ALERT')
      .policy(policyData)
      .host(linux.overview.hostDetails)
      .setContextError('investigateHosts.context.error.context.service.timeout')
      .build();
    await render(hbs`{{host-detail/overview domIsReady=true}}`);
    waitUntil(() => find('.rsa-panel-message'));
    assert.equal(findAll('.message')[0].textContent.trim(), 'Context Hub server is not running or is inaccessible. Check with your Administrator to resolve this issue.', 'error message displayed.');
  });
});
