import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import windowsTestPolicy from '../../../../../../data/subscriptions/groups/fetchRankingView/dataWindow';

let setState;

module('Integration | Component | group-ranking/inspector | windows-log-policy', function(hooks) {
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
    await render(hbs`{{usm-groups/group-ranking/inspector/windows-log-policy}}`);
    assert.equal(findAll('.usm-policies-inspector-windows-log').length, 1, 'The component appears in the DOM');
  });

  test('It shows the correct sections and properties', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy(windowsTestPolicy)
      .build();
    await render(hbs`{{usm-groups/group-ranking/inspector/windows-log-policy}}`);
    assert.equal(findAll('.heading').length, 2, '2 headings are shown');
    assert.equal(findAll('.heading .col-md-7')[0].innerText, 'Windows Log Settings', 'first heading first part is as expected');
    assert.equal(findAll('.heading .col-md-5')[0].innerText, 'GOVERNING POLICY - GROUP', 'first heading second part is as expected');
    assert.equal(findAll('.heading .col-md-7')[1].innerText, 'Channel Filter Settings', 'second heading first part is as expected');
    assert.equal(findAll('.title').length, 4, '4 property names are shown');
    assert.equal(findAll('.value').length, 12, '12 value elements are shown');
    assert.equal(findAll('.value')[4].innerText.trim(), 'TLS', 'Protocol value shows as expected');
  });
});