import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { waitUntil, click, find, findAll, render } from '@ember/test-helpers';
import { selectors } from './selectors';
import { getAllEvents, getAllAlerts, getSelection, getEventSelection, getAlertSelection, filterEventsBySelection } from './data';

module('Integration | Component | events-list', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('expandedId', null);
    this.set('selectionExists', []);
    this.set('alerts', getAllAlerts());
    this.set('loadingStatus', 'completed');
    this.set('clearSelection', () => {});
    this.set('expandStorylineEvent', (id) => this.set('expandedId', id));
  });

  test('renders both generic and endpoint row templates', async function(assert) {
    this.set('items', getAllEvents());
    this.set('selection', getSelection());

    await render(hbs`{{events-list
      items=items
      alerts=alerts
      expandedId=expandedId
      loadingStatus=loadingStatus
      selection=selection
      selectionExists=selectionExists
      clearSelection=clearSelection
      expandStorylineEvent=expandStorylineEvent}}`);

    assert.equal(findAll(selectors.list).length, 1);
    assert.equal(findAll(selectors.row).length, 16);
    assert.equal(findAll(selectors.genericHeader).length, 12);
    assert.equal(findAll(selectors.endpointHeader).length, 4);
    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.loader).length, 0);
    assert.equal(findAll(selectors.clear).length, 0);
    assert.equal(find(selectors.count).textContent.trim(), '16');
    assert.equal(find(selectors.label).textContent.trim(), 'events');
  });

  test('onclick the table row main will expand the event showing any details for the given type', async function(assert) {
    this.set('items', getAllEvents());
    this.set('selection', getSelection());

    await render(hbs`{{events-list
      items=items
      alerts=alerts
      expandedId=expandedId
      loadingStatus=loadingStatus
      selection=selection
      selectionExists=selectionExists
      clearSelection=clearSelection
      expandStorylineEvent=expandStorylineEvent}}`);

    assert.equal(findAll(selectors.row).length, 16);
    assert.equal(findAll(selectors.genericHeader).length, 12);
    assert.equal(findAll(selectors.genericFooter).length, 12);
    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointHeader).length, 4);
    assert.equal(findAll(selectors.endpointFooter).length, 4);
    assert.equal(findAll(selectors.endpointDetail).length, 0);

    await click(`${selectors.row}:nth-of-type(1) ${selectors.genericHeader}`);

    assert.equal(findAll(selectors.genericDetail).length, 1);
    assert.equal(findAll(selectors.endpointDetail).length, 0);

    assert.equal(findAll(selectors.genericHeader).length, 12);
    assert.equal(findAll(selectors.genericFooter).length, 11);
    assert.equal(findAll(selectors.endpointHeader).length, 4);
    assert.equal(findAll(selectors.endpointFooter).length, 4);

    await click(`${selectors.row}:nth-of-type(6) ${selectors.endpointHeader}`);

    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 1);

    assert.equal(findAll(selectors.genericHeader).length, 12);
    assert.equal(findAll(selectors.genericFooter).length, 12);
    assert.equal(findAll(selectors.endpointHeader).length, 4);
    assert.equal(findAll(selectors.endpointFooter).length, 3);
  });

  test('loading spinner present when storyline event status not completed', async function(assert) {
    this.set('items', getAllEvents());
    this.set('selection', getSelection());
    this.set('loadingStatus', 'streaming');

    await render(hbs`{{events-list
      items=items
      alerts=alerts
      expandedId=expandedId
      loadingStatus=loadingStatus
      selection=selection
      selectionExists=selectionExists
      clearSelection=clearSelection
      expandStorylineEvent=expandStorylineEvent}}`);

    assert.equal(findAll(selectors.row).length, 16);
    assert.equal(findAll(selectors.loader).length, 1);
  });

  test('clear selection present when incident selection is alert', async function(assert) {
    const selection = getAlertSelection();
    this.set('items', filterEventsBySelection('alert'));
    this.set('selection', selection);
    this.set('selectionExists', selection.ids);

    await render(hbs`{{events-list
      items=items
      alerts=alerts
      expandedId=expandedId
      loadingStatus=loadingStatus
      selection=selection
      selectionExists=selectionExists
      clearSelection=clearSelection
      expandStorylineEvent=expandStorylineEvent}}`);

    assert.equal(findAll(selectors.row).length, 3);
    assert.equal(findAll(selectors.clear).length, 1);
    assert.equal(findAll(selectors.clearButton).length, 1);
    assert.equal(find(selectors.clearButton).textContent.trim(), 'See All Events');
  });

  test('clear selection button will reset applied event filter and hide clear option', async function(assert) {
    const selection = getEventSelection();
    this.set('items', filterEventsBySelection('event'));
    this.set('selection', selection);
    this.set('selectionExists', selection.ids);
    this.set('clearSelection', () => {
      this.set('selectionExists', []);
      this.set('selection', getSelection());
      this.set('items', getAllEvents());
    });

    await render(hbs`{{events-list
      items=items
      alerts=alerts
      expandedId=expandedId
      loadingStatus=loadingStatus
      selection=selection
      selectionExists=selectionExists
      clearSelection=clearSelection
      expandStorylineEvent=expandStorylineEvent}}`);

    assert.equal(findAll(selectors.row).length, 1);
    assert.equal(findAll(selectors.clear).length, 1);
    assert.equal(find(selectors.count).textContent.trim(), '1');
    assert.equal(find(selectors.label).textContent.trim(), 'event');

    await click(selectors.clearButton);

    await waitUntil(() => findAll(selectors.row).length > 10);
    assert.ok(findAll(selectors.row).length > 10);
    assert.equal(findAll(selectors.clear).length, 0);
  });
});
