import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { selectors } from './context-selectors';
import { waitForEntityHighlight } from './wait-for-highlight';
import { waitUntil, click, find, findAll, render } from '@ember/test-helpers';
import { getEndpointAlertSelection, getEndpointEventSelection, filterEndpointEventsBySelection } from './data';

const timeout = 10000;
const MOUSE_ENTER = 'mouseenter';
const MOUSE_LEAVE = 'mouseleave';

module('Integration | Component | events-list | context tooltip', function(hooks) {
  setupRenderingTest(hooks);

  test('when views are recycled context tooltip will highlight properly', async function(assert) {
    const alertSelection = getEndpointAlertSelection();
    this.set('selection', alertSelection);
    this.set('selectionExists', alertSelection.ids);
    this.set('items', filterEndpointEventsBySelection('alert'));
    this.set('clearSelection', () => {
      const eventSelection = getEndpointEventSelection();
      this.set('selection', eventSelection);
      this.set('selectionExists', eventSelection.ids);
      this.set('items', filterEndpointEventsBySelection('event'));
    });

    this.set('openContextPanel', () => {});
    this.set('openContextAddToList', () => {});

    await render(hbs`
      {{context-tooltip clickDataAction=(action openContextPanel) addToListAction=(action openContextAddToList)}}

      {{events-list
      items=items
      alerts=alerts
      expandedId=expandedId
      loadingStatus=loadingStatus
      selection=selection
      selectionExists=selectionExists
      clearSelection=clearSelection
      expandStorylineEvent=expandStorylineEvent}}`);

    assert.equal(findAll(selectors.row).length, 4);

    await waitForEntityHighlight();

    const [ element ] = findAll(selectors.hostName);
    this.$(element).trigger(MOUSE_ENTER);

    await waitUntil(() => findAll(selectors.tooltip).length > 0, { timeout });

    assert.equal(find(selectors.tooltip).textContent, 'INENMENONS4L2C');

    this.$(element).trigger(MOUSE_LEAVE);

    await waitUntil(() => findAll(selectors.tooltip).length === 0, { timeout });

    await click(selectors.clearButton);

    await waitUntil(() => findAll(selectors.row).length === 1, { timeout });

    assert.equal(findAll(selectors.row).length, 1);

    await waitForEntityHighlight();

    const [ soloElement ] = findAll(selectors.hostName);
    this.$(soloElement).trigger(MOUSE_ENTER);

    await waitUntil(() => findAll(selectors.tooltip).length > 0, { timeout });

    assert.equal(find(selectors.tooltip).textContent, 'LINUXHOSTNAME');
  });
});
