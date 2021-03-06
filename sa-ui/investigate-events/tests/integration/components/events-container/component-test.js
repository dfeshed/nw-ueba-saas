import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import EventColumnGroups from '../../../data/subscriptions/column-group';

let setState;
const columnGroupManagerSelector = '.rsa-investigate-events-table__header__columnGroups';

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

  test('it renders the content when event results has errored and with results', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .atLeastOneQueryIssued(true)
      .isEventResultsError(true, 'error')
      .build();
    await render(hbs`{{events-container}}`);

    assert.ok(find('.rsa-investigate-query__body-master'), 'Expected event results content.');
  });

  test('it renders the error block when event results has errored with no results', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .isEventResultsError(true, 'error')
      .eventResults([])
      .build();
    await render(hbs`{{events-container}}`);

    assert.ok(find('.query-error'), 'Expected query error message on page');
    assert.ok(find(columnGroupManagerSelector), 'Did not find column groups along the error panel');
    assert.ok(find(`${columnGroupManagerSelector} .list-caption`).textContent.trim().includes('Summary List'),
      'Default column group is Summary List.');
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
