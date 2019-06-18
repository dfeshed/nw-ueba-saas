import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

const _trim = (text) => text.replace(/\s+/g, '').trim();

let setState;

module('Integration | Component | console-panel', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');

    this.set('timezone', {
      selected: {
        zoneId: 'America/Los_Angeles'
      }
    });

    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders the correct dom and data', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().build();

    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);

    assert.equal(findAll('.console-panel .warnings .warning').length, 0);
    assert.equal(findAll('.console-panel .console-content').length, 1);
    assert.equal(findAll('.console-panel .console-content .service').length, 1);
    assert.equal(findAll('.console-panel .console-content .timerange').length, 1);
    assert.equal(find('.console-panel .console-content .timerange .value .start').textContent.trim(), '"49682-11-11 15:20:00"');
    assert.equal(find('.console-panel .console-content .timerange .value .end').textContent.trim(), '"49682-11-11 15:20:00"');
  });

  test('renders the correct dom and data when in mixed mode', async function(assert) {
    new ReduxDataHelper(setState).hasRequiredValuesToQuery(true).withPreviousQuery().queryStats().queryStatsIsOpen().queryStatsIsRetrieving().isMixedMode().build();

    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);

    assert.equal(findAll('.console-panel .warnings i.is-mixed-mode').length, 1);
    assert.equal(findAll('.console-panel.is-mixed-mode').length, 1);
  });

  test('renders the correct progressLabel when Queued', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats('Queued').queryStatsIsOpen().build();

    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);

    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Queued');
  });

  test('renders the correct progressLabel when Executing', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats('Executing').queryStatsIsOpen().build();

    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);

    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Executing');
  });

  test('renders the correct progressLabel', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats('foo').queryStatsIsOpen().build();

    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);

    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Executing - foo');
  });

  test('renders the correct progressLabel when retrieving', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsRetrieving().queryStatsIsOpen().build();

    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);

    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Retrieving');
  });

  test('renders the correct dom hasWarning', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel.has-warning .console-content').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Executing - warning');
  });

  test('renders the correct dom hasError', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().hasRequiredValuesToQuery(true).queryStats().queryStatsHasError().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel.has-error .console-content').length, 1);
    assert.equal(findAll('.console-panel.has-error .console-content .fatal-errors i.rsa-icon-report-problem-triangle-filled').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Error');
    assert.ok(find('.console-panel .console-content .fatal-errors').textContent.trim().includes('concentrator'));
    assert.equal(find('.console-panel .console-content .fatal-errors .error-text').textContent.trim(), 'error');
  });

  test('renders the correct dom hasError without service', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().hasRequiredValuesToQuery(true).queryStats().queryStatsHasErrorWithoutId().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(find('.console-panel .console-content .fatal-errors').textContent.trim(), 'error');
  });

  test('renders warnings', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().hasRequiredValuesToQuery(true).queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .warnings .warning').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Executing - warning');
  });

  test('does not render progress-bar while querying', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .progress-bar').length, 0);
  });

  test('renders progress-bar when retrieving', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().queryStatsIsRetrieving().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .progress-bar').length, 1);
  });

  test('does not render devices when not complete', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .devices-status').length, 0);
  });

  test('renders devices when complete', async function(assert) {
    new ReduxDataHelper(setState).hasRequiredValuesToQuery(true).withPreviousQuery().queryStats().queryStatsIsOpen().queryStatsIsRetrieving().build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .devices-status').length, 1);
  });

  test('it shows proper message when query is canceled by the user', async function(assert) {
    new ReduxDataHelper(setState)
      .withPreviousQuery()
      .queryStats()
      .queryStatsIsOpen()
      .eventResultsStatus('canceled')
      .build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.equal(find('.console-panel .progress .value').textContent.trim(),
      'User Canceled',
      'incorrect message displayed if query canceled');
  });

  test('renders meta and text filter data', async function(assert) {
    const metaFilters = [
      { id: '1', type: 'query', meta: 'a', operator: '=', value: '"a"' },
      { id: '2', type: 'query', meta: 'b', operator: 'exists' },
      { id: '3', type: 'text', searchTerm: 'blahblahblah' }
    ];
    new ReduxDataHelper(setState)
      .withPreviousQuery(metaFilters)
      .queryStats()
      .queryStatsIsOpen()
      .build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    const filters = findAll('.console-panel .filters');
    assert.equal(filters.length, 2, 'incorrect number of filters');
    assert.equal(_trim(filters[0].textContent), 'MetaFilter:a="a"&&bexists', 'incorrect DOM for meta filter');
    assert.equal(_trim(filters[1].textContent), 'TextFilter:blahblahblah', 'incorrect DOM for text filter');
    assert.ok(find('.console-panel .filters .label i'), 'warning icon was not shown');
    assert.ok(find('.console-panel .filters .label[title="A text filter matches only indexed meta keys, possibly limiting results loaded in the Events panel."]'), 'warning title for hover not present');
  });

  test('does not render meta and text filter DOM if no data', async function(assert) {
    // No filter data
    const metaFilters = [];
    new ReduxDataHelper(setState)
      .withPreviousQuery(metaFilters)
      .queryStats()
      .queryStatsIsOpen()
      .build();
    await render(hbs`
      {{query-container/console-panel timezone=timezone}}
    `);
    assert.notOk(findAll('.console-panel .filters').length, 'found filter DOM when there should be none');
  });
});