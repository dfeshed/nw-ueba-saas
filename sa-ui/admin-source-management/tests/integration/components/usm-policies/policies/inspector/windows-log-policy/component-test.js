import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, triggerEvent } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

const testPolicy = {
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
  secondaryDestination: 'LD_02',
  channelFilters: [],
  customConfig: '{a:"test"}',
  associatedGroups: []
};

module('Integration | Component | Policy Inspector | Windows Log Policy', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-policies/policies/inspector/windows-log-policy}}`);
    assert.equal(findAll('.usm-policies-inspector-windows-log').length, 1, 'The component appears in the DOM');
  });

  test('It shows the correct sections for properties', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy(testPolicy)
      .setPolicyChannels([
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
      ])
      .build();

    await render(hbs`{{usm-policies/policies/inspector/windows-log-policy}}`);
    assert.equal(findAll('.heading').length, 3, '3 headings are shown');
    assert.equal(findAll('.heading')[0].innerText, 'Connection Settings', 'first heading is as expected');
    assert.equal(findAll('.heading')[1].innerText, 'Channel Filter Settings', 'second heading is as expected');
    assert.equal(findAll('.heading')[2].innerText, 'Advanced Configuration', 'third heading is as expected');
    assert.equal(findAll('.title').length, 9, '9 property names are shown');
    assert.equal(findAll('.value').length, 9, '9 value elements are shown');
    assert.equal(findAll('.title')[5].innerText, 'System include', 'channel prop name is as expected');
    assert.equal(findAll('.value')[5].innerText, '1234', 'channel value is as expected');
    assert.equal(findAll('.title')[6].innerText, 'Security include', 'channel prop name is as expected');
    assert.equal(findAll('.value')[6].innerText, '5678', 'channel value is as expected');
    assert.equal(findAll('.title')[7].innerText, 'Application exclude', 'channel prop name is as expected');
    assert.equal(findAll('.value')[7].innerText, '7789', 'channel value is as expected');
    assert.equal(findAll('.title')[8].innerText, 'Advanced Setting', 'adv setting label is as expected');
    assert.equal(findAll('.value')[8].innerText, '{a:\"test\"}', 'advanced setting value is as expected'); // eslint-disable-line no-useless-escape
  });

  test('When no channels it shows only two other sections basic and advanced', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy(testPolicy)
      .setPolicyChannels([])
      .build();

    await render(hbs`{{usm-policies/policies/inspector/windows-log-policy}}`);
    assert.equal(findAll('.heading').length, 2, '2 heading are shown');
    assert.equal(findAll('.heading')[0].innerText, 'Connection Settings', 'first heading is as expected');
    assert.equal(findAll('.heading')[1].innerText, 'Advanced Configuration', 'second heading is as expected');
    assert.equal(findAll('.title').length, 6, '6 property names are shown');
    assert.equal(findAll('.value').length, 6, '6 value elements are shown');
  });

  test('Custom config setting and its tooltip is shown', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy(testPolicy)
      .setPolicyCustomConfig('"enabled" : false,"sendTestLog" : true,"protocol" : "UDP","policyType" : "filePolicy","name" : "Test File Policy1","description" : "Test File Policy1 Description."')
      .build();
    const expectedCustomSetting = '"enabled" : false,"sendTestLog" : true,"protocol" : "UDP","policyType" : "filePolicy","name" : "Test File Policy1","description" : "Test File Policy1 Description."';
    await render(hbs`{{usm-policies/policies/inspector/edr-policy}}`);
    assert.equal(findAll('.heading').length, 1, '1 Advanced heading is shown');
    assert.equal(findAll('.heading')[0].innerText, 'Advanced Configuration', 'advanced setting heading as expected');
    assert.equal(findAll('.title').length, 1, '1 property name is shown');
    assert.equal(findAll('.value').length, 1, '1 value element is shown');
    assert.equal(findAll('.value')[0].innerText.trim(),
      expectedCustomSetting,
      'custom config value is as expected');
    await triggerEvent('.value .tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(),
      expectedCustomSetting,
      'custom setting tooltip is as expected');
  });
});
