import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

import PILL_SELECTORS from '../pill-selectors';

let setState;

module('Integration | Component | query-button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('Button is disabled when the query is not ready', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(false)
      .pillsDataEmpty()
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/query-button
        executeQuery=executeQuery
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.queryButtonDisabled).length, 1, 'button should be disabled');
  });

  test('Calls function to execute query', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .pillsDataEmpty()
      .build();

    this.set('executeQuery', () => {
      assert.ok(true, 'executeQuery is called');
      done();
    });

    await render(hbs`
      {{query-container/query-button
        executeQuery=executeQuery
      }}
    `);

    await click(PILL_SELECTORS.queryButton);
  });

  test('Shows a textual label if query is NOT running', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .isQueryRunning(false)
      .build();

    await render(hbs`{{query-container/query-button}}`);
    assert.equal(find(PILL_SELECTORS.queryButton).textContent.trim(), 'Query Events', 'displays textual label');
  });

  test('Shows a spinner if query is running', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .isQueryRunning(true)
      .build()
      .investigate;

    await render(hbs`{{query-container/query-button}}`);
    assert.equal(findAll(PILL_SELECTORS.loadingQueryButton).length, 1, 'displays loading button');
  });
});