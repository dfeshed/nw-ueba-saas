import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import PILL_SELECTORS from '../pill-selectors';

let setState;

module('Integration | Component | query-button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
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
});