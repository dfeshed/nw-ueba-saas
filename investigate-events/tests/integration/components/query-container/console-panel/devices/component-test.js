import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | console-devices', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');

    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders the correct dom', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsRetrieving().build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);
    assert.equal(findAll('.devices-status').length, 1);
    assert.equal(findAll('.devices-status div.all-online').length, 1);
    assert.equal(findAll('.devices-status div.some-offline i.rsa-icon-report-problem-triangle-filled').length, 0);
    assert.equal(findAll('.devices-status .one-line-summary i.rsa-icon-arrow-down-12-filled').length, 0);
    assert.equal(findAll('.devices-status div.some-offline').length, 0);
  });

  test('renders the offline status when a single device is offline', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsRetrieving().queryStatsWithOffline().build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);
    assert.equal(findAll('.devices-status div.all-online').length, 0);
    assert.equal(findAll('.devices-status div.some-offline').length, 1);
    assert.equal(findAll('.devices-status div.some-offline i.rsa-icon-report-problem-triangle-filled').length, 1);
    assert.equal(find('.devices-status div.some-offline').textContent.trim(), '1 service is offline.');
  });

  test('renders the offline status when multiple devices are offline', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsRetrieving().queryStatsWithMultipleOffline().build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);
    assert.equal(findAll('.devices-status div.all-online').length, 0);
    assert.equal(findAll('.devices-status div.some-offline').length, 1);
    assert.equal(find('.devices-status div.some-offline').textContent.trim(), '2 services are offline.');
    assert.equal(findAll('.devices-status div.some-offline i.rsa-icon-report-problem-triangle-filled').length, 1);
  });

  test('renders the summary of the top level device when streaming', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsIsRetrieving().eventCount(10).build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);

    assert.equal(findAll('.devices-status ul.device-hierarchy').length, 1);
    assert.equal(findAll('.devices-status ul.device-hierarchy li').length, 1);
    assert.equal(find('.devices-status ul.device-hierarchy li:first-of-type .device').textContent.trim(), '1');
    assert.ok(find('.devices-status ul.device-hierarchy li:first-of-type').textContent.trim().includes('found (~2s)  10 event(s).'));
  });

  test('renders the summary of the top level device when complete', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsIsComplete().eventCount(10).build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);

    assert.equal(findAll('.devices-status ul.device-hierarchy').length, 1);
    assert.equal(findAll('.devices-status ul.device-hierarchy li').length, 1);
    assert.equal(find('.devices-status ul.device-hierarchy li:first-of-type .device').textContent.trim(), '1');
    assert.ok(find('.devices-status ul.device-hierarchy li:first-of-type').textContent.trim().includes('found (~2s) and retrieved (~1s)  10 event(s).'));
  });

  test('renders the summary of the top level device when no events', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsNoTime().eventCount(0).build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);

    assert.ok(find('.devices-status ul.device-hierarchy li:first-of-type').textContent.trim().includes('found (<1s) 0 event(s).'));
  });

  test('renders when hasError', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsIsRetrieving().eventCount(10).queryStatsWithOffline().build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);
    assert.ok(find('.devices-status .one-line-summary .circle.error'));
  });

  test('renders when hasWarning', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsIsRetrieving().eventCount(10).queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel/devices}}
    `);
    assert.equal(findAll('.devices-status .one-line-summary .circle.warning').length, 1);
  });

  test('renders when with children and open', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsIsRetrieving().eventCount(10).queryStatsWithHierarcy().build();
    await render(hbs`
      {{query-container/console-panel/devices isExpanded=true}}
    `);
    assert.equal(findAll('.devices-status .one-line-summary .circle.populated.open').length, 1);
    assert.equal(findAll('.devices-status .device-hierarchy').length, 2);
    assert.equal(findAll('.devices-status .one-line-summary i.rsa-icon-arrow-down-12-filled').length, 1);
  });

  test('renders when with children and closed', async function(assert) {
    new ReduxDataHelper(setState).hasSummaryData(true, '1').queryStats().queryStatsIsRetrieving().eventCount(10).queryStatsWithHierarcy().build();
    await render(hbs`
      {{query-container/console-panel/devices isExpanded=false}}
    `);
    assert.equal(findAll('.devices-status .one-line-summary .circle.populated.closed').length, 1);
    assert.equal(findAll('.devices-status .one-line-summary i.rsa-icon-arrow-down-12-filled').length, 1);
  });

});
