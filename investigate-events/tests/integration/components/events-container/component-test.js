import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';

let setState;

module('Integration | Component | events-container', function(hooks) {
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

  test('it renders an error when event results has errored', async function(assert) {
    new ReduxDataHelper(setState)
      .isServicesRetrieveError(false)
      .isEventResultsError(true)
      .build();
    await render(hbs`{{events-container}}`);
    assert.equal(find('.query-error').textContent, 'This is an error message', 'Expected query error message on page');
  });

  test('it does not render error block if no error', async function(assert) {
    new ReduxDataHelper(setState)
      .isServicesRetrieveError(false)
      .queryStats()
      .isEventResultsError(false)
      .atLeastOneQueryIssued(false) // dont want to render result table
      .hasIncommingQueryParams(false) // dont want to render result table
      .hasRequiredValuesToQuery(false)
      .build();
    await render(hbs`{{events-container}}`);
    assert.equal(findAll('.query-error').length, 0, 'Expected query error message on page');
  });

  test('it renders an error when summary has errored', async function(assert) {
    new ReduxDataHelper(setState)
      .isServicesRetrieveError(false)
      .isEventResultsError(false)
      .hasFatalSummaryError(true)
      .build();
    await render(hbs`{{events-container}}`);
    assert.equal(find('.summary-error').textContent, 'The service is unavailable', 'Expected error message on page');
  });
});
