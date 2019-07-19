import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import dataFile from '../../../../../../data/subscriptions/groups/fetchRankingView/dataFile';

const [, fileTestPolicyWithSources] = dataFile;

let setState;

module('Integration | Component | group-ranking/inspector | file-policy', function(hooks) {
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
    await render(hbs`{{usm-groups/group-ranking/inspector/file-policy}}`);
    assert.equal(findAll('.usm-policies-inspector-file').length, 1, 'The component appears in the DOM');
  });

  test('It shows the correct sections and properties', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy(fileTestPolicyWithSources)
      .build();
    await render(hbs`{{usm-groups/group-ranking/inspector/file-policy}}`);
    // should be 1 File Settings heading + 2 Source Settings headings for apache & exchange
    assert.equal(findAll('.heading').length, 3, '1 File Settings heading + 2 Source Settings headings are shown');
    assert.equal(findAll('.heading .col-md-7')[0].innerText, 'File Settings', 'File Settings heading is as expected');
    assert.equal(findAll('.heading .col-md-5')[0].innerText, '', 'File Settings heading second part is as expected');
    assert.equal(findAll('.heading .col-md-7')[1].innerText, 'Source Settings (apache)', 'apache source heading is as expected');
    assert.equal(findAll('.heading .col-md-7')[2].innerText, 'Source Settings (exchange)', 'exchange source heading is as expected');
    // check total number of setting names & values
    assert.equal(findAll('.title').length, 16, '16 property names are shown');
    assert.equal(findAll('.value').length, 38, '38 value elements are shown');
    // File Settings (connection settings) values
    assert.equal(findAll('.value')[2].innerText.trim(), 'Enabled', 'Status is Enabled as expected');
    assert.equal(findAll('.value')[3].innerText.trim(), 'test - test', 'Origin policy & group of Status are as expected');
    assert.equal(findAll('.value')[4].innerText.trim(), 'Disabled', 'Send Test Log is Disabled as expected');
    assert.equal(findAll('.value')[5].innerText.trim(), 'test - test', 'Origin policy & group of Send Test Log are as expected');
    assert.equal(findAll('.value')[6].innerText.trim(), '10.10.10.10', 'Primary Destination is 10.10.10.10 as expected');
    assert.equal(findAll('.value')[7].innerText.trim(), 'test - test', 'Origin policy & group of Primary Destination are as expected');
    assert.equal(findAll('.value')[8].innerText.trim(), '10.10.10.12', 'Secondary Destination is 10.10.10.12 as expected');
    assert.equal(findAll('.value')[9].innerText.trim(), 'test - test', 'Origin policy & group of Secondary Destination are as expected');
    assert.equal(findAll('.value')[10].innerText.trim(), 'TLS', 'Protocol value shows TLS as expected');
    assert.equal(findAll('.value')[11].innerText.trim(), 'test - test', 'Origin policy & group of Protocol are as expected');
    // apache source settings values
    assert.equal(findAll('.value')[14].innerText.trim(), 'Disabled', 'Log Collection is Disabled as expected');
    assert.equal(findAll('.value')[15].innerText.trim(), 'apache policyName - apache groupName', 'Origin policy & group of Log Collection are as expected');
    assert.equal(findAll('.value')[16].innerText.trim(), 'Collect new data only', 'Data Collection is Collect new data only as expected');
    assert.equal(findAll('.value')[17].innerText.trim(), 'apache policyName - apache groupName', 'Origin policy & group of Data Collection are as expected');
    assert.equal(findAll('.value')[18].innerText.trim(), 'UTF-8', 'File Encoding is UTF-8 as expected');
    assert.equal(findAll('.value')[19].innerText.trim(), 'apache policyName - apache groupName', 'Origin policy & group of File Encoding are as expected');
    assert.equal(findAll('.value')[20].innerText.trim(), '/c/apache_path-hint-1/*.log, /c/Program Files/Apache Group/Apache[2-9]/*.log, apache_path-hint-2', 'Paths is /c/apache_path-hint-1/*.log, /c/Program Files/Apache Group/Apache[2-9]/*.log, apache_path-hint-2 as expected');
    assert.equal(findAll('.value')[21].innerText.trim(), 'apache policyName - apache groupName', 'Origin policy & group of Paths are as expected');
    assert.equal(findAll('.value')[22].innerText.trim(), 'Meta-Source-Name', 'Source Name is Meta-Source-Name as expected');
    assert.equal(findAll('.value')[23].innerText.trim(), 'apache policyName - apache groupName', 'Origin policy & group of Source Name are as expected');
    assert.equal(findAll('.value')[24].innerText.trim(), 'exclude-string-1, exclude-string-2, exclude-string-3', 'Exclusion Filters is exclude-string-1, exclude-string-2, exclude-string-3 as expected');
    assert.equal(findAll('.value')[25].innerText.trim(), 'apache policyName - apache groupName', 'Origin policy & group of Exclusion Filters are as expected');
    // exchange source settings values
    assert.equal(findAll('.value')[28].innerText.trim(), 'Enabled', 'Log Collection is Enabled as expected');
    assert.equal(findAll('.value')[29].innerText.trim(), 'exchange policyName - exchange groupName', 'Origin policy & group of Log Collection are as expected');
    assert.equal(findAll('.value')[30].innerText.trim(), 'Collect historical and new data', 'Data Collection is Collect historical and new data as expected');
    assert.equal(findAll('.value')[31].innerText.trim(), 'exchange policyName - exchange groupName', 'Origin policy & group of Data Collection are as expected');
    assert.equal(findAll('.value')[32].innerText.trim(), 'UTF-8', 'File Encoding is UTF-8 as expected');
    assert.equal(findAll('.value')[33].innerText.trim(), 'exchange policyName - exchange groupName', 'Origin policy & group of File Encoding are as expected');
    assert.equal(findAll('.value')[34].innerText.trim(), '/[cd]/exchange/logs/*.log', 'Paths is /[cd]/exchange/logs/*.log as expected');
    assert.equal(findAll('.value')[35].innerText.trim(), 'exchange policyName - exchange groupName', 'Origin policy & group of Paths are as expected');
    assert.equal(findAll('.value')[36].innerText.trim(), 'Exchange aye!', 'Source Name is Exchange aye! as expected');
    assert.equal(findAll('.value')[37].innerText.trim(), 'exchange policyName - exchange groupName', 'Origin policy & group of Source Name are as expected');
    // currently no exclusion filters for this one
    // assert.equal(findAll('.value')[38].innerText.trim(), 'someExclusionFilter, exclude-string-2, exclude-string-3', 'Exclusion Filters is someExclusionFilter, exclude-string-2, exclude-string-3 as expected');
    // assert.equal(findAll('.value')[39].innerText.trim(), 'test - test', 'Origin policy & group of Exclusion Filters are as expected');
  });
});