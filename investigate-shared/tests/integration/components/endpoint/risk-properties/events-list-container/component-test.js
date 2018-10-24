import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { eventsData } from './data';
import { selectors } from '../../../../../integration/components/events-list/selectors';

module('Integration | Component | endpoint/risk-properties/events-list-container', function(hooks) {
  setupRenderingTest(hooks);

  test('renders endpoint row templates', async function(assert) {
    this.set('state', {
      eventsData,
      expandedEventId: null,
      eventsLoadingStatus: 'completed'
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
      eventsData,
      expandedId: null,
      eventsLoadingStatus: 'completed'
    });
    this.set('expand', () => {
      this.set('state', {
        eventsData,
        expandedEventId: 6019,
        eventsLoadingStatus: 'completed'
      });
    });
    await render(hbs`{{endpoint/risk-properties/events-list-container riskState=state expandEvent=(action expand)}}`);
    assert.equal(findAll(selectors.row).length, 2, 'total 2 rows');
    assert.equal(findAll(selectors.endpointDetail).length, 0, 'detail 0');

    await click(`${selectors.row}:nth-of-type(1) ${selectors.endpointHeader}`);
    assert.equal(findAll(selectors.endpointDetail).length, 1, 'on click detail 1');
  });

  test('loading spinner present when storyline event status not completed', async function(assert) {
    this.set('state', {
      eventsData,
      expandedEventId: null,
      eventsLoadingStatus: 'loading'
    });
    await render(hbs`{{endpoint/risk-properties/events-list-container riskState=state}}`);

    assert.equal(findAll(selectors.row).length, 2);
    assert.equal(findAll(selectors.loader).length, 1);
  });
});