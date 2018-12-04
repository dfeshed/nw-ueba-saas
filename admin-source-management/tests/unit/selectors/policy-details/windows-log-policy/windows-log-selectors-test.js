import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import {
  focusedPolicy,
  selectedWindowsLogPolicy
} from 'admin-source-management/reducers/usm/policy-details/windows-log-policy/windows-log-selectors';

module('Unit | Selectors | Policy Details | Windows Log Policy | Windows Log Selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('selectedWindowsLogPolicy selector', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({
        id: 'policy_WL001',
        policyType: 'windowsLogPolicy',
        name: 'WL001',
        description: 'Windows Log Policy # WL001',
        dirty: true,
        defaultPolicy: false,
        createdBy: 'admin',
        createdOn: 1540318426092,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        lastPublishedOn: 0,
        lastPublishedCopy: null,
        enabled: true,
        protocol: 'TCP',
        sendTestLog: false,
        primaryDestination: '10.10.10.10',
        secondaryDestination: '10.10.10.12',
        channelFilters: [
          {
            eventId: '1234',
            channel: 'System',
            filterType: 'include'
          },
          {
            eventId: '5678',
            channel: 'Security',
            filterType: 'include'
          },
          {
            eventId: '7789',
            channel: 'Application',
            filterType: 'exclude'
          }
        ],
        associatedGroups: []
      })
      .build();
    assert.expect(10);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedWindowsLogPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 2, '2 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.windowsLogSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 5, 'first section has 5 properties');
    assert.equal(policyDetails[0].props[0].value, 'Enabled', 'enabled property has expected value');
    assert.equal(policyDetails[0].props[3].value, 'NWAPPLIANCE55555 - Log Server', 'primary destination has expected value');
    assert.equal(policyDetails[0].props[4].value, 'NWAPPLIANCE66666- Log Server', 'secondary destination has expected value');
    assert.equal(policyDetails[1].header, 'adminUsm.policies.detail.channelFilterSettings', 'second section  is as expected');
    assert.equal(policyDetails[1].channels.length, 3, 'second section has 3 channels');
    assert.equal(policyDetails[1].channels[0].name, 'System include', 'first channel name is as expected');
    assert.equal(policyDetails[1].channels[0].value, '1234', 'first channel value is as expected');
  });

  test('selectedWindowsLogPolicy ignore blank values', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({
        id: 'policy_WL001',
        policyType: 'windowsLogPolicy',
        name: 'WL001',
        description: 'Windows Log Policy # WL001',
        dirty: true,
        defaultPolicy: false,
        createdBy: 'admin',
        createdOn: 1540318426092,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        lastPublishedOn: 0,
        lastPublishedCopy: null,
        enabled: true,
        protocol: '',
        sendTestLog: false,
        primaryDestination: '',
        secondaryDestination: '',
        channelFilters: [
          {
            eventId: '1234',
            channel: 'System',
            filterType: 'include'
          },
          {
            eventId: '5678',
            channel: 'Security',
            filterType: 'include'
          },
          {
            eventId: '7789',
            channel: 'Application',
            filterType: 'exclude'
          }
        ],
        associatedGroups: []
      })
      .build();
    assert.expect(6);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedWindowsLogPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 2, '2 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.windowsLogSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 2, 'first section has 2 properties');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policies.detail.protocol',
      value: ''
    }), false, 'protocol is ignored since it does not have a value');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policies.detail.primaryDestination',
      value: ''
    }), false, 'primary dest is ignored since it does not have a value');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policies.detail.secondaryDestination',
      value: ''
    }), false, 'secondary dest is ignored since it does not have a value');
  });

  test('selectedWindowsLogPolicy no basic settings', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({
        id: 'policy_WL001',
        policyType: 'windowsLogPolicy',
        name: 'WL001',
        description: 'Windows Log Policy # WL001',
        dirty: true,
        defaultPolicy: false,
        createdBy: 'admin',
        createdOn: 1540318426092,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        lastPublishedOn: 0,
        lastPublishedCopy: null,
        channelFilters: [
          {
            eventId: '1234',
            channel: 'System',
            filterType: 'include'
          },
          {
            eventId: '5678',
            channel: 'Security',
            filterType: 'include'
          },
          {
            eventId: '7789',
            channel: 'Application',
            filterType: 'exclude'
          }
        ],
        associatedGroups: []
      })
      .build();
    assert.expect(4);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedWindowsLogPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policies.detail.windowsLogSettings',
      props: []
    }), false, 'No Log settings section as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.channelFilterSettings', 'second section  is as expected');
    assert.equal(policyDetails[0].channels.length, 3, 'second section has 3 channels');
  });

  test('selectedWindowsLogPolicy no channels section', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({
        id: 'policy_WL001',
        policyType: 'windowsLogPolicy',
        name: 'WL001',
        description: 'Windows Log Policy # WL001',
        dirty: true,
        defaultPolicy: false,
        createdBy: 'admin',
        createdOn: 1540318426092,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        lastPublishedOn: 0,
        lastPublishedCopy: null,
        enabled: true,
        protocol: 'TCP',
        sendTestLog: false,
        primaryDestination: 'LD_01',
        secondaryDestination: '2.2.2.2',
        associatedGroups: []
      })
      .build();
    assert.expect(5);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedWindowsLogPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.windowsLogSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props[3].value, 'LD_01', 'primary destination has expected value');
    assert.equal(policyDetails[0].props[4].value, '2.2.2.2', 'no display name found for secondary destination as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policies.detail.channelFilterSettings',
      props: []
    }), false, 'No Channel settings section as expected');
  });
});