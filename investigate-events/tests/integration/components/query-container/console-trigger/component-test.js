import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render, triggerKeyEvent, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | Console Trigger', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders the correct dom', async function(assert) {
    new ReduxDataHelper(setState).queryStats().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);
    assert.equal(findAll('.console-trigger .rsa-icon-information-circle-lined').length, 1);
  });

  test('renders the correct dom hasWarning', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasWarning().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);
    assert.equal(findAll('.console-trigger.has-warning .rsa-icon-report-problem-triangle-filled').length, 1);
  });

  test('renders the correct dom hasError', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasError().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);
    assert.equal(findAll('.console-trigger.has-error .rsa-icon-report-problem-triangle-filled').length, 1);
  });

  test('renders the correct dom isDisabled', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsEmpty().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);
    assert.equal(findAll('.console-trigger.is-disabled').length, 1);
  });

  test('renders the correct dom isOpen', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);
    assert.equal(findAll('.console-trigger.is-open').length, 1);
  });

  test('it closes the console when pressing ESC', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);

    await triggerKeyEvent(window, 'keydown', 27);
    assert.equal(findAll('.console-trigger.is-open').length, 0);
  });

  test('it opens when the trigger is clicked', async function(assert) {
    new ReduxDataHelper(setState).queryStats().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);

    await click('.console-trigger i');
    assert.equal(findAll('.console-trigger.is-open').length, 1);
  });

  test('it closes when the trigger is clicked', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().build();
    await render(hbs`
      {{query-container/console-trigger}}
    `);

    await click('.console-trigger i');
    assert.equal(findAll('.console-trigger.is-open').length, 0);
  });

  test('it closes when the document is clicked', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().build();
    await render(hbs`
      <div class="test"></div>
      {{query-container/console-trigger}}
    `);

    await click('.test');
    assert.equal(findAll('.console-trigger.is-open').length, 0);
  });

  test('it does not close when the console panel is clicked', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsIsOpen().build();
    await render(hbs`
      <div class="query-bar-selection">
        <div class="console-panel">
          <h1>Test</h1>
          {{query-container/console-trigger}}
        </div>
      </div>
    `);

    await click('.console-panel h1');
    assert.equal(findAll('.console-trigger.is-open').length, 1);
  });

});
