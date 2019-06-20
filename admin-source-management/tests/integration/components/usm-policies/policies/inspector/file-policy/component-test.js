import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

const testPolicy = {
  id: 'policy_F001',
  policyType: 'filePolicy',
  name: 'F001',
  description: 'File Policy # F001',
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
  associatedGroups: []
};

module('Integration | Component | Policy Inspector | File Policy', function(hooks) {
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
    await render(hbs`{{usm-policies/policies/inspector/file-policy}}`);
    assert.equal(findAll('.usm-policies-inspector-file').length, 1, 'The component appears in the DOM');
  });

  test('It shows the correct sections for properties', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .policyWizFileSourceTypes()
      .focusedPolicy({ ...testPolicy })
      .setPolicyFileSources([
        {
          fileType: 'apache',
          enabled: false,
          startOfEvents: false,
          fileEncoding: 'UTF-8',
          paths: ['/c/apache_path-hint-1/*.log', '/c/Program Files/Apache Group/Apache[2-9]/*.log', 'apache_path-hint-2'],
          sourceName: 'Meta-Source-Name',
          exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
        },
        {
          fileType: 'exchange',
          enabled: true,
          startOfEvents: true,
          fileEncoding: 'UTF-8',
          paths: ['/[cd]/exchange/logs/*.log'],
          sourceName: 'Exchange aye!',
          exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
        }
      ])
      .build();

    await render(hbs`{{usm-policies/policies/inspector/file-policy}}`);
    assert.equal(findAll('.heading').length, 3, '1 file settings heading + 2 source settings headings');
    assert.equal(findAll('.heading')[0].innerText, 'File Settings', 'File Settings heading is as expected');
    assert.equal(findAll('.heading')[1].innerText, 'Source Settings (apache)', 'apache source heading is as expected');
    assert.equal(findAll('.heading')[2].innerText, 'Source Settings (exchange)', 'exchange source heading is as expected');
    assert.equal(findAll('.title').length, 17, '17 property names are shown');
    assert.equal(findAll('.value').length, 17, '17 value elements are shown');
    // test the specific values for props that get translated
    assert.equal(findAll('.value')[5].innerText, 'Disabled', 'apache source enabled: Disabled');
    assert.equal(findAll('.value')[6].innerText, 'Collect historical and new data', 'apache source startOfEvents: Collect historical and new data');
    assert.equal(findAll('.value')[11].innerText, 'Enabled', 'exchange source enabled: Enabled');
    assert.equal(findAll('.value')[12].innerText, 'Collect new data only', 'exchange source startOfEvents: Collect new data only');
  });

  test('It does NOT show blank properties', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .policyWizFileSourceTypes()
      .focusedPolicy({ ...testPolicy })
      .setPolicyFileSources([
        {
          fileType: 'apache',
          enabled: false,
          startOfEvents: false,
          fileEncoding: '',
          paths: [],
          sourceName: '',
          exclusionFilters: []
        }
      ])
      .build();

    await render(hbs`{{usm-policies/policies/inspector/file-policy}}`);
    assert.equal(findAll('.heading').length, 2, '1 file settings heading + 1 source settings heading');
    assert.equal(findAll('.heading')[0].innerText, 'File Settings', 'File Settings heading is as expected');
    assert.equal(findAll('.heading')[1].innerText, 'Source Settings (apache)', 'apache source heading is as expected');
    // there would be 11 .title's/.value's if the blank properties were rendered
    assert.equal(findAll('.title').length, 7, '7 property names are shown');
    assert.equal(findAll('.value').length, 7, '7 value elements are shown');
  });

  test('It only shows one section when there are not sources', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .focusedPolicy({ ...testPolicy })
      .setPolicyFileSources([])
      .build();

    await render(hbs`{{usm-policies/policies/inspector/file-policy}}`);
    assert.equal(findAll('.heading').length, 1, '1 file settings heading');
    assert.equal(findAll('.heading')[0].innerText, 'File Settings', 'File Settings heading is as expected');
    assert.equal(findAll('.title').length, 5, '5 property names are shown');
    assert.equal(findAll('.value').length, 5, '5 value elements are shown');
  });

});