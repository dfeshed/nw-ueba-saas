import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { blur, click, fillIn, find, findAll, focus, render, triggerKeyEvent, waitUntil } from '@ember/test-helpers';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { enrichedPillsData } from 'investigate-events/reducers/investigate/next-gen/selectors';
import { createBasicPill, isIgnoredInitialEvent } from '../pill-util';
import KEY_MAP from 'investigate-events/util/keys';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';

// const { log } = console;

import PILL_SELECTORS from '../pill-selectors';

const ESCAPE_KEY = KEY_MAP.escape.code;
const X_KEY = 88;

const trim = (text) => text.replace(/\s+/g, '').trim();
let setState;

const _getEnrichedPill = () => {
  const pillState = new ReduxDataHelper(setState).pillsDataPopulated().language().build();
  const [ enrichedPill ] = enrichedPillsData(pillState);
  return enrichedPill;
};

const _setPillData = (component) => {
  const enrichedPill = _getEnrichedPill();
  component.set('pillData', enrichedPill);
};

module('Integration | Component | query-pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('contains proper class when invalid', async function(assert) {
    let enrichedPill = _getEnrichedPill();
    enrichedPill = enrichedPill.set('isInvalid', true);
    this.set('pillData', enrichedPill);
    await render(hbs`{{query-container/query-pill isActive=true pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1);
  });

  test('contains proper class when selected', async function(assert) {
    let enrichedPill = _getEnrichedPill();
    enrichedPill = enrichedPill.set('isSelected', true);
    this.set('pillData', enrichedPill);
    await render(hbs`{{query-container/query-pill isActive=true pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1);
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
    await selectChoose(PILL_SELECTORS.meta, 'alias.ipv6');
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'alias.ipv6');
    // Verify that operator gets control
    await focus(PILL_SELECTORS.operatorTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 4);
    // Click back on meta and verify that 1 down-selected option is visible
    await click(PILL_SELECTORS.meta);
    await focus(PILL_SELECTORS.metaTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 1);
    // Clear input to show all meta options
    await fillIn(PILL_SELECTORS.metaInput, '');
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 17);
    // Select meta options B
    await selectChoose(PILL_SELECTORS.meta, PILL_SELECTORS.powerSelectOption, 1);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'b');
  });

  test('A pill when supplied with meta, operator, and value will send a message to create', async function(assert) {

    const done = assert.async();
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
      assert.deepEqual(data, { meta: 'a', operator: '=', value: '\'x\'' }, 'Message sent for pill create contains correct pill data');
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
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
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
    _setPillData(this);
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
    _setPillData(this);
    await render(hbs`{{query-container/query-pill isActive=true pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 0, 'Delete pill component is not present');
  });

  test('messages up that a pill needs to be deleted when delete icon clicked', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETED, 'Message sent for pill delete is not correct');
      assert.deepEqual(data,
        { id: '1', meta: 'a', operator: '=', value: '\'x\'', isSelected: false },
        'Message sent for pill create contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    _setPillData(this);
    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
        position=0
        sendMessage=(action handleMessage)
    }}`);
    await click(PILL_SELECTORS.deletePill);
  });

  test('messages up a pill has cancelled when child componenent sends ESCAPE', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CANCELLED, 'Message sent for pill cancel is not correct');
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
    _setPillData(this);

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
      }}
    `);

    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
    assert.equal(trim(find(PILL_SELECTORS.operator).textContent), '=');
    assert.equal(trim(find(PILL_SELECTORS.value).textContent), '\'x\'');
  });

  test('A pill clears out and is available to create more pills', async function(assert) {
    const done = assert.async(2);
    assert.expect(3);

    new ReduxDataHelper(setState).language().pillsDataEmpty().build();

    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');

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
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
    await createBasicPill();
  });

  test('A pill when focused will send ENTERED event', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();

    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW, 'Message sent for pill create is not correct');
      assert.deepEqual(data.id, undefined, 'Pill data goes not contain an id');
      assert.equal(position, 12, 'Message sent for pill entered contains correct pill position');

      done();
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=12
        sendMessage=(action handleMessage)
      }}
    `);
  });

  test('When nothing is chosen and component loses focus the component messages up a pill has cancelled', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CANCELLED, 'Message sent for pill cancel is not correct');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
      }}
    `);

    await focus(PILL_SELECTORS.metaTrigger);
    await blur(PILL_SELECTORS.metaTrigger);
  });

  test('When something (meta) is chosen and component loses focus the component does not message up', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(false, `Should not get here with ${messageType}`);
    });

    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
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
    await blur(PILL_SELECTORS.operatorTrigger);
  });

  test('At no point during pill creation is a cancel thrown up because of focusOut/focusIn timing', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.notEqual(messageType, MESSAGE_TYPES.PILL_CANCELLED, 'No cancel should be sent');
    });

    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
      }}
    `);

    await createBasicPill();
  });

  test('If in value and user clicks away, the pill remains in creation state where no data entered is changed or removed', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
      }}
    `);
    // Choose first meta option
    selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    // Choose the first operator option
    selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0); // option =
    await waitUntil(() => find(PILL_SELECTORS.valueInput));
    // Fill in the value, to properly simulate the event we need to fillIn AND
    // triggerKeyEvent for the "x" character.
    await fillIn(PILL_SELECTORS.valueInput, 'x');
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', X_KEY); // x
    await blur(PILL_SELECTORS.valueInput);
    assert.equal(trim(find(PILL_SELECTORS.queryPill).textContent), 'a=');
    assert.equal(find(PILL_SELECTORS.valueInput).value, 'x');
  });

  test('If pill is active and single clicked, it will not message up', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(false, `Should not get here with ${messageType}`);
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
      }}
    `);

    // click the pill
    await click(PILL_SELECTORS.queryPill);
  });

  test('If pill is not active and single clicked, it will message up', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (messageType, data) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(messageType === MESSAGE_TYPES.PILL_SELECTED, 'Should be selected');
      assert.ok(data.isSelected === false, 'isSelected should be false because it is not selected');
    });
    this.set('pillData', _getEnrichedPill());

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    // Choose the first meta optio
    await click(PILL_SELECTORS.queryPill);
  });

  test('If selected pill is clicked, it will message up desleected', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (messageType, data) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(messageType === MESSAGE_TYPES.PILL_DESELECTED, 'Should be deselected');
      assert.ok(data.isSelected === true, 'isSelected should be true because it is selected');
    });
    let pillData = _getEnrichedPill();
    pillData = pillData.set('isSelected', true);
    this.set('pillData', pillData);

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    // Choose the first meta optio
    await click(PILL_SELECTORS.queryPill);
  });

  test('If pill is not selected or active, and meta is clicked, pill will message up with selected', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (messageType, data) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(messageType === MESSAGE_TYPES.PILL_SELECTED, 'Should be selected');
      assert.ok(data.isSelected === false, 'isSelected should be false because it is not selected');
    });
    this.set('pillData', _getEnrichedPill());

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    // Choose the first meta optio
    await click(PILL_SELECTORS.meta);
  });
});