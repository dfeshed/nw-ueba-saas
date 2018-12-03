import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { waitUntil, settled, render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { respondData } from './respond-data';
import { selectors } from '../../../../../integration/components/events-list/selectors';

const riskScoreContext = {
  distinctAlertCount: {
    critical: 1,
    high: 0,
    medium: 0,
    low: 0
  }
};

module('Integration | Component | endpoint/risk-properties/events-list-container', function(hooks) {
  setupRenderingTest(hooks);

  test('renders endpoint row templates', async function(assert) {
    this.set('state', {
      eventsData: respondData,
      expandedEventId: null,
      eventsLoadingStatus: 'completed',
      riskScoreContextError: null,
      riskScoreContext
    });
    await render(hbs`{{endpoint/risk-properties/events-list-container riskState=state}}`);
    assert.equal(findAll(selectors.list).length, 1);
    assert.equal(findAll(selectors.row).length, 2);
    assert.equal(findAll(selectors.endpointHeader).length, 2);
    assert.equal(findAll(selectors.loader).length, 0);
    assert.equal(find(selectors.count).textContent.trim(), '2');
    assert.equal(find(selectors.label).textContent.trim(), 'events');
  });

  test('onclick the table row main will expand the event showing any details for the given type', async function(assert) {
    this.set('state', {
      eventsData: respondData,
      expandedEventId: null,
      eventsLoadingStatus: 'completed',
      riskScoreContextError: null,
      riskScoreContext
    });
    this.set('expand', () => {
      this.set('state', {
        eventsData: respondData,
        expandedEventId: 0,
        eventsLoadingStatus: 'completed',
        riskScoreContextError: null,
        riskScoreContext
      });
    });
    await render(hbs`{{endpoint/risk-properties/events-list-container riskState=state expandEvent=(action expand)}}`);
    assert.equal(findAll(selectors.row).length, 2, 'total 2 rows');
    assert.equal(findAll(selectors.endpointDetail).length, 0, 'detail 0');

    await click(`${selectors.row}:nth-of-type(1) ${selectors.endpointHeader}`);

    await waitUntil(() => findAll(selectors.endpointDetail).length === 1);
    await settled();

    assert.equal(findAll(selectors.endpointDetail).length, 1, 'on click detail 1');
  });

  test('loading spinner present when storyline event status not completed', async function(assert) {
    this.set('state', {
      eventsData: respondData,
      expandedEventId: null,
      eventsLoadingStatus: 'loading',
      riskScoreContextError: null,
      riskScoreContext
    });
    await render(hbs`{{endpoint/risk-properties/events-list-container riskState=state}}`);

    assert.equal(findAll(selectors.row).length, 2);
    assert.equal(findAll(selectors.loader).length, 1);
  });

  test('show error message when risk score context is empty for a file.', async function(assert) {
    this.set('state', {
      expandedEventId: null,
      riskScoreContextError: null
    });
    await render(hbs`{{endpoint/risk-properties/events-list-container riskState=state}}`);

    assert.equal(findAll('.rsa-panel-message').length, 1, 'Error Message for No events available exists.');
  });
});
