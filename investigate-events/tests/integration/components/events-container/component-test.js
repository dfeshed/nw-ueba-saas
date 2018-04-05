import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';

import engineResolverFor from '../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';

let setState;

module('Integration | Component | events-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
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
      .isEventResultsError(false)
      .atLeastOneQueryIssued(false) // dont want to render result table
      .hasIncommingQueryParams(false) // dont want to render result table
      .hasRequiredValuesToQuery(false)
      .build();
    await render(hbs`{{events-container}}`);
    assert.equal(findAll('.query-error').length, 0, 'Expected query error message on page');
  });
});