import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { blur, click, fillIn, find, findAll, focus, render, triggerKeyEvent, waitUntil } from '@ember/test-helpers';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { enrichedPillsData } from 'investigate-events/reducers/investigate/query-node/selectors';
import { createBasicPill, isIgnoredInitialEvent, doubleClick } from '../pill-util';
import KEY_MAP from 'investigate-events/util/keys';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';

const META_OPTIONS = metaKeySuggestionsForQueryBuilder(
  new ReduxDataHelper(setState).language().pillsDataEmpty().build()
);

import PILL_SELECTORS from '../pill-selectors';

const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.code;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.code;
const ARROW_DOWN_KEY = KEY_MAP.arrowDown.code;
const ARROW_UP_KEY = KEY_MAP.arrowUp.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const X_KEY = 88;
const DELETE_KEY = KEY_MAP.delete.code;
const BACKSPACE_KEY = KEY_MAP.backspace.code;
const modifiers = { shiftKey: true };

const trim = (text) => text.replace(/\s+/g, '').trim();
let setState;

const _getEnrichedPill = (component) => {
  component.set('metaOptions', META_OPTIONS);
  const pillState = new ReduxDataHelper(setState).pillsDataPopulated().language().build();
  const [ enrichedPill ] = enrichedPillsData(pillState);
  return enrichedPill;
};

const _setPillData = (component) => {
  const enrichedPill = _getEnrichedPill(component);
  component.set('pillData', enrichedPill);
  return enrichedPill;
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

  test('contains proper class when expensive', async function(assert) {
    let enrichedPill = _getEnrichedPill(this);
    enrichedPill = enrichedPill.setIn(['operator', 'isExpensive'], true);
    this.set('pillData', enrichedPill);
    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.expensivePill).length, 1, 'Class for expensive pill should be present');
    assert.equal(findAll(PILL_SELECTORS.expensiveIndicator).length, 1, 'Class for expensive icon should be present');
    assert.equal(this.$(PILL_SELECTORS.expensiveIndicator).prop('title'), 'Performing this operation might take more time.', 'Expected title');
  });

  test('contains proper class when invalid', async function(assert) {
    let enrichedPill = _getEnrichedPill(this);
    enrichedPill = enrichedPill.set('isInvalid', true);
    this.set('pillData', enrichedPill);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.invalidPill).length, 1);
  });

  test('contains proper class when selected', async function(assert) {
    let enrichedPill = _getEnrichedPill(this);
    enrichedPill = enrichedPill.set('isSelected', true);
    this.set('pillData', enrichedPill);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1);
  });

  test('contains a focus holder when selected', async function(assert) {
    let enrichedPill = _getEnrichedPill(this);
    enrichedPill = enrichedPill.set('isFocused', true);
    this.set('pillData', enrichedPill);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 1);
  });

  test('does not contain focus holder when not selected', async function(assert) {
    const enrichedPill = _getEnrichedPill(this);
    this.set('pillData', enrichedPill);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.focusHolderInput).length, 0);
  });

  test('it activates pill-meta if active upon initialization', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('it allows you to select a meta value', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option a
    await waitUntil(() => !find(PILL_SELECTORS.metaTrigger));
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
  });

  test('it allows you to select an operator after a meta value was selected', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');// option =
    await waitUntil(() => find(PILL_SELECTORS.operator));
    assert.equal(trim(find(PILL_SELECTORS.operator).textContent), '=');
  });

  test('it sets pill-value active after selecting an operator', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');// option =
    await waitUntil(() => find(PILL_SELECTORS.operator));
    assert.equal(findAll(PILL_SELECTORS.valueInput).length, 1, 'Missing value input field');
  });

  test('it allows you to edit the meta after it was selected', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
      }}
    `);
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
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 20);
    // Select meta options B
    await selectChoose(PILL_SELECTORS.meta, PILL_SELECTORS.powerSelectOption, 1);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'b');
  });

  test('A pill when supplied with meta, operator, and value will send a message to create', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', META_OPTIONS);

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
        metaOptions=metaOptions
      }}
    `);

    await createBasicPill();
  });

  test('A pill when supplied with meta and operator that does not accept a value will send a message to create', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', META_OPTIONS);

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
        metaOptions=metaOptions
      }}
    `);

    // Choose the first meta option
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));

    // Choose the third operator option which does not require a value
    await selectChoose(PILL_SELECTORS.operatorTrigger, 'exists'); // option exists
  });

  test('presents a delete icon when not active and pill created', async function(assert) {
    _setPillData(this);
    await render(hbs`{{query-container/query-pill isActive=false pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 1, 'Delete pill component is present');
  });

  test('does not present a delete icon when not a created pill', async function(assert) {
    this.set('pillData', null);
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 0, 'Delete pill component is not present');
  });

  test('does not present a delete icon when when active', async function(assert) {
    _setPillData(this);
    await render(hbs`
      {{query-container/query-pill
        isActive=true pillData=pillData
        metaOptions=metaOptions
      }}
    `);
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
        { id: '1', meta: 'a', operator: '=', value: '\'x\'', isSelected: false, isFocused: false },
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
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.deletePill);
  });

  test('messages up a pill has cancelled when child componenent sends ESCAPE', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_ADD_CANCELLED, 'Message sent for pill cancel is not correct');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
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
        metaOptions=metaOptions
      }}
    `);

    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
    assert.equal(trim(find(PILL_SELECTORS.operator).textContent), '=');
    assert.equal(trim(find(PILL_SELECTORS.value).textContent), '\'x\'');
  });

  test('A pill clears out and is available to create more pills', async function(assert) {
    const done = assert.async(2);
    assert.expect(3);

    this.set('metaOptions', META_OPTIONS);

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
        metaOptions=metaOptions
      }}
    `);

    await createBasicPill();
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
    await createBasicPill();
  });

  test('A pill when focused will send ENTERED event', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', META_OPTIONS);

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
        metaOptions=metaOptions
      }}
    `);
  });

  test('When nothing is chosen and component loses focus the component messages up a pill has cancelled', async function(assert) {
    const done = assert.async();

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_ADD_CANCELLED, 'Message sent for pill cancel is not correct');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
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

    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

    // Choose the first meta option
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    await blur(PILL_SELECTORS.operatorTrigger);
  });

  test('At no point during pill creation is a cancel thrown up because of focusOut/focusIn timing', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.notEqual(messageType, MESSAGE_TYPES.PILL_ADD_CANCELLED, 'No cancel should be sent');
    });

    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

    await createBasicPill();
  });

  test('If in value and user clicks away, the pill remains in creation state where no data entered is changed or removed', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);
    // Choose first meta option
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // option A
    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));
    // Choose the first operator option
    await selectChoose(PILL_SELECTORS.operatorTrigger, '='); // option =
    await waitUntil(() => find(PILL_SELECTORS.valueInput));
    // Fill in the value, to properly simulate the event we need to fillIn AND
    // triggerKeyEvent for the "x" character.
    await fillIn(PILL_SELECTORS.valueInput, 'x');
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', X_KEY); // x
    await blur(PILL_SELECTORS.valueInput);
    // The textContent of the pill will include the "x" because of the
    // transparent <span> used for resizing of the pill.
    assert.equal(trim(find(PILL_SELECTORS.queryPill).textContent), 'a=x');
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
    this.set('metaOptions', META_OPTIONS);

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
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
    this.set('pillData', _getEnrichedPill(this));

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

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
    let pillData = _getEnrichedPill(this);
    pillData = pillData.set('isSelected', true);
    this.set('pillData', pillData);

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

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
    this.set('pillData', _getEnrichedPill(this));

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

    await click(PILL_SELECTORS.meta);
  });

  test('Clicks are throttled', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(messageType === MESSAGE_TYPES.PILL_SELECTED, 'Should be selected');
      done();
    });
    this.set('pillData', _getEnrichedPill(this));

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

    // DO NOT `await` these, we want two fast clicks
    // so that only one handleMessage is called
    click(PILL_SELECTORS.queryPill);
    click(PILL_SELECTORS.queryPill);
    // DO NOT `await` these
  });

  test('double clicks sends appropriate event', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(messageType === MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, 'Should be opened for edit');
      done();
    });
    this.set('pillData', _getEnrichedPill(this));

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

    doubleClick(PILL_SELECTORS.queryPill);
  });

  test('it quotes pill value when meta is type "Text"', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_CREATED) {
        assert.equal(data.value, '\'foo\'', 'value not single quoted');
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    // Select meta option A which is of type Text
    await selectChoose(PILL_SELECTORS.meta, 'a (A)');
    await selectChoose(PILL_SELECTORS.operator, '=');
    await fillIn(PILL_SELECTORS.valueInput, 'foo');// missing wrapping quotes
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ENTER_KEY);
  });

  test('it does not quote the pill value when meta is type "UInt"', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_CREATED) {
        assert.equal(data.value, '80', 'value was quoted');
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    // Select meta option sessionid which is of type UInt64
    await selectChoose(PILL_SELECTORS.meta, 'sessionid');
    await selectChoose(PILL_SELECTORS.operator, '=');
    await fillIn(PILL_SELECTORS.valueInput, '80');
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ENTER_KEY);
  });

  test('Does not add quotes to a string if there are already single quotes', async function(assert) {
    this.set('pillData', _getEnrichedPill(this));
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    assert.equal(trim(find(PILL_SELECTORS.valueInput).value), '\'x\'');
  });

  test('replace double quotes with single quotes', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_CREATED) {
        assert.equal(data.value, '\'foo\'', 'value not quoted properly');
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    // Select meta option A which is of type Text
    await selectChoose(PILL_SELECTORS.meta, 'a (A)');
    await selectChoose(PILL_SELECTORS.operator, '=');
    await fillIn(PILL_SELECTORS.valueInput, '"foo"');// double quotes
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ENTER_KEY);
  });

  // Ember 3.3
  skip('edited pill will send message up when user indicates they would like to escape', async function(assert) {
    assert.expect(3);
    const pillData = _setPillData(this);

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_EDIT_CANCELLED, 'Message sent for pill cancel is not correct');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
      assert.equal(data, pillData, 'Message sent for pill create contains correct pill data');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);

    await click(PILL_SELECTORS.meta);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
  });

  skip('cursor positioning', async function(assert) {
    _setPillData(this);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.value);
    assert.equal(find(PILL_SELECTORS.valueInput).selectionStart, 3, 'not at end of value'); // 'a'|
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ARROW_LEFT_KEY);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ARROW_LEFT_KEY);
    await triggerKeyEvent(PILL_SELECTORS.valueInput, 'keydown', ARROW_LEFT_KEY);
    // TODO: The following assert fails because the cursor does not seem to move
    // to the left. Need to figure out how to make the cursor move.
    assert.equal(find(PILL_SELECTORS.valueInput).selectionStart, 0, 'not at beginning of value'); // |'a'
  });

  test('focused pill sends up a message when delete is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
      .pillsDataPopulated()
      .language()
      .markFocused(['1'])
      .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, data) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL, 'Message sent for pill delete');
      assert.deepEqual(data, { meta: 'a', operator: '=', value: '\'x\'', id: '1', isSelected: false, isFocused: true }, 'Message sent contains correct pill data');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
  });

  test('focused pill sends up a message when backspace is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
      .pillsDataPopulated()
      .language()
      .markFocused(['1'])
      .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, data) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL, 'Message sent for pill delete');
      assert.deepEqual(data, { meta: 'a', operator: '=', value: '\'x\'', id: '1', isSelected: false, isFocused: true }, 'Message sent contains correct pill data');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);
  });

  test('focused pill sends up a message when enter is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, data) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL, 'Message sent to open pill for edit');
      assert.deepEqual(data, { meta: 'a', operator: '=', value: '\'x\'', id: '1', isSelected: false, isFocused: true }, 'Message sent contains correct pill data');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ENTER_KEY);
  });

  test('focused pill sends up a message when shift and Up Arrow is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, 'Message sent to select all pills to the left');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_UP_KEY, modifiers);
  });

  test('focused pill sends up a message when shift and Left Arrow is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, 'Message sent to select all pills to the left');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY, modifiers);
  });

  test('focused pill sends up a message when shift and Down Arrow is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, 'Message sent to select all pills to the right');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_DOWN_KEY, modifiers);
  });

  test('focused pill sends up a message when shift and Right Arrow is pressed', async function(assert) {
    assert.expect(2);

    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, 'Message sent to select all pills to the right');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY, modifiers);
  });

  // Ember 3.3
  skip('An edited pill, when supplied with meta and operator that does not accept a value, will send a message to create', async function(assert) {
    const done = assert.async();
    const pillState = new ReduxDataHelper(setState)
      .pillsDataPopulated()
      .language()
      .build();
    const [ enrichedPill ] = enrichedPillsData(pillState);

    this.set('pillData', enrichedPill);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_EDITED, 'Message sent for pill create is not correct');
      assert.deepEqual(data, { id: '1', isSelected: false, isFocused: false, meta: 'a', operator: 'exists', value: null }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
      done();
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
        pillData=pillData
      }}
    `);
    // Double click to enter edit mode
    doubleClick(PILL_SELECTORS.queryPill);
    // Click on operator
    await click(PILL_SELECTORS.operator);
    // Delete selected option to bring up full list
    await fillIn(PILL_SELECTORS.operatorSelectInput, '');
    // Choose the third operator option which does not require a value
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 2); // option exists
  });

  test('if no meta/operator/value is selected and ARROW_LEFT is pressed, message is sent up', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
    .pillsDataEmpty()
    .language()
    .build();

    this.set('pillData', []);
    this.set('metaOptions', META_OPTIONS);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT, 'Message sent to add focus on the relevant pill');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
  });

  test('if no meta/operator/value is selected and ARROW_RIGHT is pressed, message is sent up', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
    .pillsDataEmpty()
    .language()
    .build();

    this.set('pillData', []);
    this.set('metaOptions', META_OPTIONS);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT, 'Message sent to add focus on the relevant pill');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
  });

  test('If on a focused pill and ARROW_LEFT is pressed, a message is sent up', async function(assert) {
    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT, 'Message sent to open a new pill trigger on left');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_LEFT_KEY);
  });

  test('If on a focused pill and ARROW_RIGHT is pressed, a message is sent up', async function(assert) {
    const pillState = new ReduxDataHelper(setState)
    .pillsDataPopulated()
    .language()
    .markFocused(['1'])
    .build();

    const [ enrichedPill ] = enrichedPillsData(pillState);
    this.set('pillData', enrichedPill);

    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT, 'Message sent to open a new pill trigger on right');
      assert.equal(position, 0, 'Message sent contains correct pill position');
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', ARROW_RIGHT_KEY);
  });
});