import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { selectors } from './selectors';
import { storyLineEvents, storylineEventsWithStatus, storylineEventsWithSelection } from './data';

module('Integration | Component | events-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  test('renders generic row for each event type', async function(assert) {
    patchReducer(this, Immutable.from(storyLineEvents));

    await render(hbs`{{rsa-incident/events-list}}`);

    assert.equal(findAll(selectors.table).length, 1);
    assert.equal(findAll(selectors.row).length, 12);
    assert.equal(findAll(selectors.genericRow).length, 12);
    assert.equal(findAll(selectors.genericMain).length, 12);
    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.loader).length, 0);
    assert.equal(findAll(selectors.clear).length, 0);
    assert.equal(find(selectors.count).textContent.trim(), '12');
    assert.equal(find(selectors.label).textContent.trim(), 'events');
  });

  test('onclick the table row will expand the event showing any details for the given type', async function(assert) {
    patchReducer(this, Immutable.from(storyLineEvents));

    await render(hbs`{{rsa-incident/events-list}}`);

    assert.equal(findAll(selectors.row).length, 12);
    assert.equal(findAll(selectors.genericRow).length, 12);
    assert.equal(findAll(selectors.genericMain).length, 12);
    assert.equal(findAll(selectors.genericDetail).length, 0);

    await click(`${selectors.row}:nth-of-type(1)`);

    assert.equal(findAll(selectors.row).length, 12);
    assert.equal(findAll(selectors.genericRow).length, 12);
    assert.equal(findAll(selectors.genericMain).length, 12);
    assert.equal(findAll(selectors.genericDetail).length, 1);

    await click(`${selectors.row}:nth-of-type(2)`);

    assert.equal(findAll(selectors.row).length, 12);
    assert.equal(findAll(selectors.genericRow).length, 12);
    assert.equal(findAll(selectors.genericMain).length, 12);
    assert.equal(findAll(selectors.genericDetail).length, 1);

    await click(`${selectors.row}:nth-of-type(2)`);

    assert.equal(findAll(selectors.row).length, 12);
    assert.equal(findAll(selectors.genericRow).length, 12);
    assert.equal(findAll(selectors.genericMain).length, 12);
    assert.equal(findAll(selectors.genericDetail).length, 0);
  });

  test('loading spinner present when storyline event status not completed', async function(assert) {
    const storylineState = storylineEventsWithStatus('streaming');
    patchReducer(this, Immutable.from(storylineState));

    await render(hbs`{{rsa-incident/events-list}}`);

    assert.equal(findAll(selectors.row).length, 12);
    assert.equal(findAll(selectors.loader).length, 1);
  });

  test('clear selection present when incident selection is alert', async function(assert) {
    const storylineState = storylineEventsWithSelection('alert');
    patchReducer(this, Immutable.from(storylineState));

    await render(hbs`{{rsa-incident/events-list}}`);

    assert.equal(findAll(selectors.row).length, 3);
    assert.equal(findAll(selectors.clear).length, 1);
    assert.equal(findAll(selectors.clearButton).length, 1);
    assert.equal(find(selectors.clearButton).textContent.trim(), 'show all events');
  });

  test('clear selection button will reset applied event filter and hide clear option', async function(assert) {
    const storylineState = storylineEventsWithSelection('event');
    patchReducer(this, Immutable.from(storylineState));

    await render(hbs`{{rsa-incident/events-list}}`);

    assert.equal(findAll(selectors.row).length, 1);
    assert.equal(findAll(selectors.clear).length, 1);

    await click(selectors.clearButton);

    assert.ok(findAll(selectors.row).length > 10);
    assert.equal(findAll(selectors.clear).length, 0);
  });
});
