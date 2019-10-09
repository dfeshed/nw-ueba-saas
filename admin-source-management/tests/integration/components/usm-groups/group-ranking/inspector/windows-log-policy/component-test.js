import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import dataWindow from '../../../../../../data/subscriptions/groups/fetchRankingView/dataWindow';

const [windowsTestPolicy] = dataWindow; // first (default) windows policy
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
    assert.equal(findAll('.heading').length, 3, '3 headings are shown');
    assert.equal(findAll('.heading .col-md-7')[0].innerText, 'Connection Settings', 'first heading first part is as expected');
    assert.equal(findAll('.heading .col-md-5')[0].innerText, '', 'first heading second part is as expected');
    assert.equal(findAll('.heading .col-md-7')[1].innerText, 'Channel Filter Settings', 'second heading first part is as expected');
    assert.equal(findAll('.heading .col-md-7')[2].innerText, 'Advanced Configuration', 'Third heading first part is as expected');
    assert.equal(findAll('.col-xs-12 .value')[4].innerText.trim(), 'TLS', 'Protocol value is as expected');
    assert.equal(findAll('.col-xs-12 .value')[6].innerText.trim(), 'Disabled', 'Disabled is as expected');
    assert.equal(findAll('.col-xs-12 .value')[10].innerText.trim(), '620,630,640', 'Channel Filters values is as expected');
    assert.equal(findAll('.col-xs-12 .title')[4].innerText.trim(), 'Advanced Setting', 'Advanced setting label is as expected');

    assert.equal(findAll('.col-xs-12 .value')[14].innerText.trim(), '{a:\"test\"}', 'Custom Config values is as expected'); // eslint-disable-line no-useless-escape
    assert.equal(findAll('.title').length, 5, '5 property names are shown');
    assert.equal(findAll('.value').length, 16, '16 value elements are shown');
    assert.equal(findAll('.value')[4].innerText.trim(), 'TLS', 'Protocol value shows as expected');
  });
});
