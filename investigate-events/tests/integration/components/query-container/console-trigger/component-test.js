import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
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

});
