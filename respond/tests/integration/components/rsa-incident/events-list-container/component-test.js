import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { waitUntil, click, find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { selectors } from '../../../../integration/components/events-list/selectors';
import { storyLineEvents, storylineEventsWithStatus, storylineEventsWithSelection } from './data';

module('Integration | Component | events-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  test('renders both generic and endpoint row templates', async function(assert) {
    patchReducer(this, Immutable.from(storyLineEvents));

    await render(hbs`{{rsa-incident/events-list-container}}`);

    assert.equal(findAll(selectors.list).length, 1);
    assert.equal(findAll(selectors.row).length, 17);
    assert.equal(findAll(selectors.genericHeader).length, 9);
    assert.equal(findAll(selectors.uebaHeader).length, 3);
    assert.equal(findAll(selectors.processHeader).length, 1);
    assert.equal(findAll(selectors.endpointHeader).length, 4);
    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.loader).length, 0);
    assert.equal(findAll(selectors.clear).length, 0);
    assert.equal(find(selectors.count).textContent.trim(), '17');
    assert.equal(find(selectors.label).textContent.trim(), 'events');
  });

  test('onclick the table row main will expand the event showing any details for the given type', async function(assert) {
    patchReducer(this, Immutable.from(storyLineEvents));

    await render(hbs`{{rsa-incident/events-list-container}}`);

    assert.equal(findAll(selectors.row).length, 17);
    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);

    await click(`${selectors.row}:nth-of-type(1) ${selectors.genericHeader}`);
    await waitUntil(() => findAll(selectors.genericDetail).length === 1);

    assert.equal(findAll(selectors.genericDetail).length, 1);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);


    await click(`${selectors.row}:nth-of-type(8) ${selectors.endpointHeader}`);
    await waitUntil(() => findAll(selectors.genericDetail).length === 0);
    await waitUntil(() => findAll(selectors.endpointDetail).length === 1);

    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 1);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);


    await click(`${selectors.row}:nth-of-type(8) ${selectors.endpointDetail}`);

    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 1);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);


    await click(`${selectors.row}:nth-of-type(8) ${selectors.endpointHeader}`);
    await waitUntil(() => findAll(selectors.endpointDetail).length === 0);

    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);


    await click(`${selectors.row}:nth-of-type(2) ${selectors.genericHeader}`);
    await waitUntil(() => findAll(selectors.genericDetail).length === 1);

    assert.equal(findAll(selectors.genericDetail).length, 1);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);


    await click(`${selectors.row}:nth-of-type(2) ${selectors.genericDetail}`);

    assert.equal(findAll(selectors.genericDetail).length, 1);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 0);


    await click(`${selectors.row}:nth-of-type(4) ${selectors.uebaHeader}`);
    await waitUntil(() => findAll(selectors.uebaDetail).length === 1);

    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 1);
    assert.equal(findAll(selectors.processDetail).length, 0);

    await click(`${selectors.row}:nth-of-type(6) ${selectors.processHeader}`);
    await waitUntil(() => findAll(selectors.processDetail).length === 1);

    assert.equal(findAll(selectors.genericDetail).length, 0);
    assert.equal(findAll(selectors.endpointDetail).length, 0);
    assert.equal(findAll(selectors.uebaDetail).length, 0);
    assert.equal(findAll(selectors.processDetail).length, 1);


  });

  test('loading spinner present when storyline event status not completed', async function(assert) {
    const storylineState = storylineEventsWithStatus('streaming');
    patchReducer(this, Immutable.from(storylineState));

    await render(hbs`{{rsa-incident/events-list-container}}`);

    assert.equal(findAll(selectors.row).length, 17);
    assert.equal(findAll(selectors.loader).length, 1);
  });

  test('clear selection present when incident selection is alert', async function(assert) {
    const storylineState = storylineEventsWithSelection('alert');
    patchReducer(this, Immutable.from(storylineState));

    await render(hbs`{{rsa-incident/events-list-container}}`);

    assert.equal(findAll(selectors.row).length, 3);
    assert.equal(findAll(selectors.clear).length, 1);
    assert.equal(findAll(selectors.clearButton).length, 1);
    assert.equal(find(selectors.clearButton).textContent.trim(), 'See All Events');
  });

  test('clear selection button will reset applied event filter and hide clear option', async function(assert) {
    const storylineState = storylineEventsWithSelection('event');
    patchReducer(this, Immutable.from(storylineState));

    await render(hbs`{{rsa-incident/events-list-container}}`);

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
