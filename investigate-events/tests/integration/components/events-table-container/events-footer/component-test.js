import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | events-footer', function(hooks) {
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

  test('it renders events-footer', async function(assert) {

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(findAll('.rsa-data-table-load-more').length, 1, 'event footer present');
  });

  test('it renders canceled message when there are results', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .queryCanceledWithResults()
      .build();

    await render(hbs`
      {{events-table-container/events-footer
      }}
    `);
    assert.equal(findAll('.rsa-data-table-load-more').length, 1, 'event footer present');
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'Retrieved 1 of 100 events prior to query cancellation.',
      'event footer present with the correct message'
    );
  });

  test('it renders sorting message when there are results', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .querySorting()
      .build();

    await render(hbs`
      {{events-table-container/events-footer}}
    `);
    assert.equal(findAll('.rsa-data-table-load-more .client-event-sorting').length, 1, 'event footer present');
  });

  test('it does not render a canceled message when there are no results loaded', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .queryCanceledWithNoResults()
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(findAll('.rsa-data-table-load-more').length, 1, 'event footer is present');
    assert.equal(findAll('.rsa-data-table-load-more').textContent, undefined, 'No message in the footer');
  });

  test('if events are complete, but hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(1)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();


    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when we get the max events'
    );
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'Reached the 1 event limit. Consider refining your query.',
      'Footer message when limit reached'
    );
  });

  test('if events are complete, and there are results, and did not hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);

    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when the entire event result is fetched'
    );
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'All results loaded',
      'Footer message when the entire event result is fetched'
    );
  });

  test('if events are complete, and there are results, and did not hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);

    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when the entire event result is fetched'
    );
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'All results loaded',
      'Footer message when the entire event result is fetched'
    );
  });

  test('if events are complete, and there are results, and did not hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when the entire event result is fetched'
    );
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'All results loaded',
      'Footer message when the entire event result is fetched'
    );
  });

  test('if events are complete, and there are results, and did not hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when the entire event result is fetched'
    );
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'All results loaded',
      'Footer message when the entire event result is fetched'
    );
  });

  test('if events are complete, and there are no results, a message is not displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.notOk(find('.rsa-loader'), 'spinner present');
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      false,
      'a message is not displayed when the entire event result is fetched and there are no results'
    );
  });

  test('if events are complete, and there are results, and did not hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when the entire event result is fetched'
    );
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim(),
      'All results loaded',
      'Footer message when the entire event result is fetched'
    );
  });

  test('if events are complete, and there are no results, a message is not displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      false,
      'a message is not displayed when the entire event result is fetched and there are no results'
    );
  });

  test('if events are canceled, but some results have returned, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('canceled')
      .eventCount(2)
      .streamLimit(100)
      .eventsPreferencesConfig()
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);

    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('1 of 2'),
      'correct message when partial results returned'
    );
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('cancellation'),
      'correct message when partial results returned'
    );
  });

  test('if an error is received, but some results have returned, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .isEventResultsError(true, 'error')
      .eventCount(2)
      .streamLimit(100)
      .eventsPreferencesConfig()
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-footer}}`);
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('1 of 2'),
      'correct message when partial results returned'
    );
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('error'),
      'correct message when partial results returned'
    );
  });

});
