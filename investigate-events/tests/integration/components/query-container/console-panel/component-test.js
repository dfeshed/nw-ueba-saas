import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

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
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);

    assert.equal(findAll('.console-panel .warnings .warning').length, 0);
    assert.equal(findAll('.console-panel .console-content').length, 1);
    assert.equal(findAll('.console-panel .console-content .service').length, 1);
    assert.equal(findAll('.console-panel .console-content .timerange').length, 1);
    assert.equal(find('.console-panel .console-content .timerange .value .start').textContent.trim(), '"49682-11-11 15:20:00"');
    assert.equal(find('.console-panel .console-content .timerange .value .end').textContent.trim(), '"49682-11-11 15:20:00"');
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'foo');
  });

  test('renders the correct dom hasWarning', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel.has-warning .console-content').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'warning');
  });

  test('renders the correct dom hasError', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().hasRequiredValuesToQuery(true).queryStats().queryStatsHasError().build();
    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel.has-error .console-content').length, 1);
    assert.equal(findAll('.console-panel.has-error .console-content .fatal-errors i.rsa-icon-report-problem-triangle-filled').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'Complete');
    assert.ok(find('.console-panel .console-content .fatal-errors').textContent.trim().includes('concentrator'));
    assert.equal(find('.console-panel .console-content .fatal-errors .error-text').textContent.trim(), 'error');
  });

  test('renders the correct dom hasError without service', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().hasRequiredValuesToQuery(true).queryStats().queryStatsHasErrorWithoutId().build();
    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);
    assert.equal(find('.console-panel .console-content .fatal-errors').textContent.trim(), 'error');
  });

  test('renders warnings', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().hasRequiredValuesToQuery(true).queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .warnings .warning').length, 1);
    assert.equal(find('.console-panel .console-content .progress .value').textContent.trim(), 'warning');
  });

  test('renders progress-bar', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .progress-bar').length, 1);
  });

  test('does not renders progress-bar when complete', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().queryStatsIsComplete().build();
    await render(hbs`
      {{query-container/console-panel timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}
    `);
    assert.equal(findAll('.console-panel .progress-bar').length, 0);
  });

  test('does not render devices when not complete', async function(assert) {
    new ReduxDataHelper(setState).withPreviousQuery().queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-panel}}
    `);
    assert.equal(findAll('.console-panel .devices-status').length, 0);
  });

  test('renders devices when complete', async function(assert) {
    new ReduxDataHelper(setState).hasRequiredValuesToQuery(true).withPreviousQuery().queryStats().queryStatsIsOpen().queryStatsIsComplete().build();
    await render(hbs`
      {{query-container/console-panel}}
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
      {{query-container/console-panel}}
    `);
    assert.equal(find('.console-panel .progress .value').textContent.trim(),
      'User Canceled',
      'incorrect message displayed if query canceled');
  });

});
