import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import fileTestPolicy from '../../../../../../data/subscriptions/groups/fetchRankingView/dataFile';

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
      .focusedPolicy(fileTestPolicy)
      .build();
    await render(hbs`{{usm-groups/group-ranking/inspector/file-policy}}`);
    assert.equal(findAll('.heading').length, 1, '1 headings are shown');
    assert.equal(findAll('.heading .col-md-7')[0].innerText, 'File Settings', 'first heading first part is as expected');
    assert.equal(findAll('.heading .col-md-5')[0].innerText, 'GOVERNING POLICY - GROUP', 'first heading second part is as expected');
    // TODO when we have separate section headings for non-source settings & source settings
    // assert.equal(findAll('.heading .col-md-7')[1].innerText, 'Some 2nd Section Heading', 'second heading first part is as expected');
    assert.equal(findAll('.title').length, 5, '5 property names are shown');
    assert.equal(findAll('.value').length, 12, '12 value elements are shown');
    assert.equal(findAll('.value')[2].innerText.trim(), 'Disabled', 'Status is Disabled as expected');
    assert.equal(findAll('.value')[3].innerText.trim(), 'test - test', 'Origin policy & group of Status are as expected');
    assert.equal(findAll('.value')[4].innerText.trim(), 'Disabled', 'Status is Disabled as expected');
    assert.equal(findAll('.value')[5].innerText.trim(), 'test - test', 'Origin policy & group of Send Test Log are as expected');
    assert.equal(findAll('.value')[6].innerText.trim(), '10.10.10.10', 'Status is 10.10.10.10 as expected');
    assert.equal(findAll('.value')[7].innerText.trim(), 'test - test', 'Origin policy & group of Primary Destination are as expected');
    assert.equal(findAll('.value')[8].innerText.trim(), '10.10.10.12', 'Status is 10.10.10.12 as expected');
    assert.equal(findAll('.value')[9].innerText.trim(), 'test - test', 'Origin policy & group of Secondary Destination are as expected');
    assert.equal(findAll('.value')[10].innerText.trim(), 'TLS', 'Protocol value shows TLS as expected');
    assert.equal(findAll('.value')[11].innerText.trim(), 'test - test', 'Origin policy & group of Protocol are as expected');
  });
});