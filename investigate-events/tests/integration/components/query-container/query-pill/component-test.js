import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { click, fillIn, find, findAll, focus, render, triggerKeyEvent, waitUntil } from '@ember/test-helpers';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { enrichedPillsData } from 'investigate-events/reducers/investigate/next-gen/selectors';
import { createBasicPill } from '../pill-util';
import KEY_MAP from 'investigate-events/util/keys';

// const { log } = console;

import PILL_SELECTORS from '../pill-selectors';

const ESCAPE_KEY = KEY_MAP.escape.code;

const trim = (text) => text.replace(/\s+/g, '').trim();

let setState;

module('Integration | Component | Query Pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it sends a message that it was initialized', async function(assert) {
    this.set('handleMessage', (messageType) => {
      assert.equal(messageType, 'PILL::INITIALIZED', 'Initalization message does not match');
    });
    await render(hbs`{{query-container/query-pill sendMessage=(action handleMessage)}}`);
  });

  test('it activates pill-meta if active upon initialization', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('it allows you to select a meta value', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option a
    await waitUntil(() => !find(PILL_SELECTORS.metaTrigger));
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
  });

  test('it allows you to select an operator after a meta value was selected', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);// option =
    await waitUntil(() => find(PILL_SELECTORS.operator));
    assert.equal(trim(find(PILL_SELECTORS.operator).textContent), '=');
  });

  test('it sets pill-value active after selecting an operator', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0);// option =
    await waitUntil(() => find(PILL_SELECTORS.operator));
    assert.equal(findAll(PILL_SELECTORS.valueInput).length, 1, 'Missing value input field');
  });

  test('it allows you to edit the meta after it was selected', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/query-pill isActive=true}}`);
    // Select meta option A
    await selectChoose(PILL_SELECTORS.meta, PILL_SELECTORS.powerSelectOption, 0);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
    // Verify that operator gets control
    await focus(PILL_SELECTORS.operatorTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 7);
    // Click back on meta and verify that 1 down-selected option is visible
    await click(PILL_SELECTORS.meta);
    await focus(PILL_SELECTORS.metaTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 1);
    // Clear input to show all meta options
    await fillIn(PILL_SELECTORS.metaInput, '');
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 5);
    // Select meta options B
    await selectChoose(PILL_SELECTORS.meta, PILL_SELECTORS.powerSelectOption, 1);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'b');
  });

  test('A pill when supplied with meta, operator, and value will send a message to create', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();

    this.set('handleMessage', (messageType, data, position) => {

      // first message will be initialized, get rid of it
      if (messageType === 'PILL::INITIALIZED') {
        return;
      }

      assert.equal(messageType, 'PILL::CREATED', 'Message sent for pill create is not correct');
      assert.deepEqual(data, { meta: 'a', operator: '=', value: 'x' }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    await render(hbs`
      {{query-container/query-pill
        position=0
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);

    await createBasicPill();
  });

  test('A pill when supplied with meta and operator that does not accept a value will send a message to create', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();

    this.set('handleMessage', (messageType, data, position) => {

      // first message will be initialized, get rid of it
      if (messageType === 'PILL::INITIALIZED') {
        return;
      }

      assert.equal(messageType, 'PILL::CREATED', 'Message sent for pill create is not correct');
      assert.deepEqual(data, { meta: 'a', operator: 'exists', value: null }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
      }}
    `);

    // Choose the first meta option
    selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));

    // Choose the third operator option which does not require a value
    selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 2); // option exists
  });

  test('presents a delete icon when not active and pill created', async function(assert) {
    this.set('pillData', { id: 1 });
    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{query-container/query-pill isActive=false pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 1, 'Delete pill component is present');
  });

  test('does not present a delete icon when not a created pill', async function(assert) {
    this.set('pillData', null);
    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{query-container/query-pill isActive=false pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 0, 'Delete pill component is not present');
  });

  test('does not present a delete icon when when active', async function(assert) {
    this.set('pillData', { id: 1 });
    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{query-container/query-pill isActive=true pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 0, 'Delete pill component is not present');
  });

  test('messages up that a pill needs to be deleted when delete icon clicked', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {

      // first message will be initialized, get rid of it
      if (messageType === 'PILL::INITIALIZED') {
        return;
      }

      assert.equal(messageType, 'PILL::DELETED', 'Message sent for pill delete is not correct');
      assert.deepEqual(data, { id: '1', meta: 'a', operator: '=', value: 'x' }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    new ReduxDataHelper(setState).language().build();
    const pills = new ReduxDataHelper().language().pillsDataPopulated().build();
    const enrichedPills = enrichedPillsData(pills);
    this.set('pillData', enrichedPills[0]);
    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
        position=0
        sendMessage=(action handleMessage)
    }}`);
    await click(PILL_SELECTORS.deletePill);
  });

  test('messages up a pill has cancelled when child componenent sents ESCAPE', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {

      // first message will be initialized, get rid of it
      if (messageType === 'PILL::INITIALIZED') {
        return;
      }

      assert.equal(messageType, 'PILL::CANCELLED', 'Message sent for pill cancel is not correct');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
      }}
    `);

    await focus(PILL_SELECTORS.metaTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
  });

  test('prepopulates with data when passed existing pill', async function(assert) {
    new ReduxDataHelper(setState).language().build();
    const pills = new ReduxDataHelper().language().pillsDataPopulated().build();
    const enrichedPills = enrichedPillsData(pills);
    this.set('pillData', enrichedPills[0]);

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
      }}
    `);

    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
    assert.equal(trim(find(PILL_SELECTORS.operator).textContent), '=');
    assert.equal(trim(find(PILL_SELECTORS.value).textContent), 'x');
  });

  test('A pill clears out and is available to create more pills', async function(assert) {
    const done = assert.async(2);
    assert.expect(3);

    new ReduxDataHelper(setState).language().pillsDataEmpty().build();

    this.set('handleMessage', (messageType) => {

      // first message will be initialized, get rid of it
      if (messageType === 'PILL::INITIALIZED') {
        return;
      }

      assert.equal(messageType, 'PILL::CREATED', 'Message sent for pill create is not correct');

      done();
    });

    await render(hbs`
      {{query-container/query-pill
        position=0
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);

    await createBasicPill();

    // meta power select should now be visible
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);

    await createBasicPill();
  });
});