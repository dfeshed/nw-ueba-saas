import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';

let setState;

const risk = {
  isRiskScoreReset: true,
  activeRiskSeverityTab: 'critical',
  currentEntityId: '224425af5a52d9bc67517221cb08e649c7e7822b05a6733996c97384d1ce5c9d',
  riskScoreContext: {
    id: '224425af5a52d9bc67517221cb08e649c7e7822b05a6733996c97384d1ce5c9d',
    distinctAlertCount: {
      critical: 1,
      high: 0,
      medium: 0,
      low: 0
    },
    categorizedAlerts: {
      Critical: {
        'Blacklisted File': {
          alertCount: 1,
          eventCount: 1
        }
      }
    }
  },
  riskScoreContextError: null,
  eventContext: [
    {
      id: '5c503e734ef1b73d23bc9f84',
      sourceId: 'c01561d7-527b-45d8-a2a4-e9c3c2853b7e',
      source: 'Respond'
    }
  ],
  eventContextError: null,
  eventsData: [
    {
      agent_id: '',
      data: [
        {
          filename: 'vlc-cache-gen.exe',
          size: 41,
          hash: '224425af5a52d9bc67517221cb08e649c7e7822b05a6733996c97384d1ce5c9d'
        }
      ],
      description: '',
      domain_src: '',
      device_type: 'nwendpoint',
      event_source: '10.40.14.101:50005',
      type: 'Endpoint',
      analysis_file: 'blacklisted file',
      file: 'vlc-cache-gen.exe',
      detected_by: '-nwendpoint',
      process_vid: '',
      host_src: '',
      action: '',
      operating_system: '',
      alias_ip: '',
      from: '',
      timestamp: '2019-01-29T11:52:14.000+0000',
      event_source_id: '10811357',
      category: 'File',
      indicatorId: '5c503e734ef1b73d23bc9f84',
      eventIndex: 0,
      id: '5c503e734ef1b73d23bc9f84:0'
    }
  ],
  eventsLoadingStatus: 'completed',
  alertsError: null,
  selectedAlert: 'Blacklisted File',
  expandedEventId: null,
  isRespondServerOffline: false,
  alertsLoadingStatus: 'completed'
};

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
      const state = Immutable.from({ endpoint: { overview, visuals, risk } });
      patchReducer(this, state);
      this.owner.inject('component', 'i18n', 'service:i18n');
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('host properties is open/close on click', async function(assert) {
    setState();

    await render(hbs`{{host-detail/overview}}`);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'Host properties is open');

    await click('.right-zone .close-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0, 'right panel is not visible after close');
  });

  test('Host detail Alerts box is available', async function(assert) {
    setState();
    await render(hbs`{{host-detail/overview domIsReady=true}}`);
    assert.equal(findAll('.host-detail-box').length, 1, 'Host detail box is present');
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

    assert.equal(find('.right-zone .rsa-nav-tab.is-active .label').textContent.trim(), 'Policy Details', 'policyDetails tab selected');
    assert.equal(findAll('.host-properties-box .rsa-loader').length, 0, 'Loader is not present');
    assert.equal(findAll('.host-properties-box .host-property-panel').length, 1, 'Properties panel is rendered');
  });

  test('Detail Right panel is visible', async function(assert) {
    setState({ activePropertyPanelTab: 'HOST_DETAILS' });
    this.set('isDetailRightPanelVisible', true);
    await render(hbs`{{host-detail/overview domIsReady=true }}`);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'Right panel- host properties is open');
  });

  test('host details closes on expanding events', async function(assert) {
    setState();
    this.set('isDetailRightPanelVisible', true);
    await render(hbs`{{host-detail/overview domIsReady=true }}`);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'Right panel- host properties is open');
    await click('.events-list-endpoint-header');
    assert.equal(findAll('.right-zone').length, 0, 'On event expansion, close the right panel');
  });

  test('renders policy unavailable message', async function(assert) {
    setState({ activePropertyPanelTab: 'POLICIES' });

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasPolicyReadPermission', true);

    await render(hbs`{{host-detail/overview domIsReady=true }}`);

    assert.equal(find('.host-properties-box .host-property-panel .message').textContent.trim(), 'Policy unavailable');
  });

  test('renders policy read permission message', async function(assert) {
    setState({ activePropertyPanelTab: 'POLICIES' });

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasPolicyReadPermission', false);

    await render(hbs`{{host-detail/overview domIsReady=true }}`);

    assert.equal(find('.host-properties-box .host-property-panel .message').textContent.trim(), 'Permission required to view policy');
  });
});