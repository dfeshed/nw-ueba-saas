import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';

let setState;

module('Integration | Component | host-detail/overview', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    setState = (visuals) => {
      const overview = {
        hostDetails: {
          machine: {
            machineAgentId: 'A8F19AA5-A48D-D17E-2930-DF5F1A75A711',
            machineName: 'INENDHUPAAL1C',
            machineOsType: 'windows'
          }
        }
      };
      const state = Immutable.from({ endpoint: { overview, visuals } });
      patchReducer(this, state);
      this.owner.inject('component', 'i18n', 'service:i18n');
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('renders all sections', async function(assert) {
    setState();
    await render(hbs`{{host-detail/overview}}`);

    // assert.equal(findAll('.trends-chart').length, 1, 'Chart is rendered');
    // assert.equal(findAll('.host-detail-box .risk-properties').length, 1, 'alert tab is rendered');
    assert.equal(findAll('.host-properties-box .rsa-loader').length, 1,
        'By default loader is rendered in properties box');
  });

  test('host properties is open/close on click', async function(assert) {
    setState();

    await render(hbs`{{host-detail/overview}}`);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'Host properties is open');

    await click('.right-zone .close-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0,
        'right panel is not visible after close');

    await click('.center-zone .open-properties');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1,
        'right panel is visible on external open action');
  });

  test('Host detail Alerts box is available', async function(assert) {
    setState();
    await render(hbs`{{host-detail/overview domIsReady=true}}`);
    assert.equal(findAll('.host-detail-box.scroll-box').length, 1, 'Host detail box is present');
  });

  test('renders host details properties when tab is host_details', async function(assert) {
    setState({ activePropertyPanelTab: 'HOST_DETAILS' });
    await render(hbs`{{host-detail/overview domIsReady=true}}`);
    assert.equal(find('.right-zone .rsa-nav-tab.is-active .label').textContent.trim(), 'Host Details', 'host details tab selected');
    assert.equal(findAll('.host-properties-box .rsa-loader').length, 0, 'Loader is not present');
    assert.equal(findAll('.host-properties-box .host-property-panel').length, 1, 'Properties panel is rendered');
  });

  test('renders policies properties when tab is policies', async function(assert) {
    setState({ activePropertyPanelTab: 'POLICIES' });

    await render(hbs`{{host-detail/overview domIsReady=true }}`);

    assert.equal(find('.right-zone .rsa-nav-tab.is-active .label').textContent.trim(), 'Policies', 'policies tab selected');
    assert.equal(findAll('.host-properties-box .rsa-loader').length, 0, 'Loader is not present');
    assert.equal(findAll('.host-properties-box .host-property-panel').length, 1, 'Properties panel is rendered');
  });
});