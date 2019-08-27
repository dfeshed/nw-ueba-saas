import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { blur, click, fillIn, find, findAll, focus, render, triggerKeyEvent, typeIn, waitUntil, settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { enrichedPillsData } from 'investigate-events/reducers/investigate/query-node/selectors';
import { createBasicPill, doubleClick, isIgnoredInitialEvent, toggleTab } from '../pill-util';
import KEY_MAP from 'investigate-events/util/keys';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import { metaKeySuggestionsForQueryBuilder, languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL
} from 'investigate-events/constants/pill';
import PILL_SELECTORS from '../pill-selectors';

let setState;
let metaOptions = [];
let languageAndAliases = {};
const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.key;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.key;
const ARROW_DOWN_KEY = KEY_MAP.arrowDown.key;
const BACKSPACE_KEY = KEY_MAP.backspace.key;
const DELETE_KEY = KEY_MAP.delete.key;
const ENTER_KEY = KEY_MAP.enter.key;
const ESCAPE_KEY = KEY_MAP.escape.key;
const SPACE_KEY = KEY_MAP.space.key;
const TAB_KEY = KEY_MAP.tab.key;
const THREE_KEY = '3';
const X_KEY = 'KeyX';
const modifiers = { shiftKey: true };

const trim = (text) => text.replace(/\s+/g, '').trim();

const _getEnrichedPill = (component) => {
  component.set('metaOptions', metaOptions);
  const pillState = new ReduxDataHelper(setState).pillsDataPopulated().language().build();
  const [ enrichedPill ] = enrichedPillsData(pillState);
  return enrichedPill;
};

const _setPillData = (component) => {
  const enrichedPill = _getEnrichedPill(component);
  component.set('pillData', enrichedPill);
  return enrichedPill;
};

const _assertOptionsInPowerSelect = (assert, optionArray, selector) => {
  const selectorArray = findAll(selector);
  const optionsArray = selectorArray.map((el) => el.textContent);
  assert.deepEqual(
    optionsArray,
    optionArray,
    'Should see options that are relevant to the text typed in'
  );
};

// meta ob, op ob, with value
const _validateUntilValue = (assert, meta, operator, value) => {
  // meta should be selected
  assert.equal(find(PILL_SELECTORS.meta).textContent.trim(), meta, 'correct pill-meta selected');
  // operator should be selected too
  assert.equal(find(PILL_SELECTORS.operator).textContent.trim(), operator, 'correct pill-operator selected');
  // focus should be in pill-value
  assert.equal(findAll(PILL_SELECTORS.valueSelectInput).length, 1, 'Should be placed in pill-value');
  // with that value
  assert.equal(
    find(PILL_SELECTORS.valueSelectInput).value.trim(),
    value,
    'Should be placed in pill-value with the correct text'
  );
};

// meta ob, op ob, no value
const _validateUntilOperator = (assert, meta, operator) => {
  // meta should be selected
  assert.equal(find(PILL_SELECTORS.meta).textContent.trim(), meta, 'correct pill-meta selected');
  // operator should be selected too
  assert.equal(find(PILL_SELECTORS.operator).textContent.trim(), operator, 'correct pill-operator selected');
  // focus should be in pill-value
  assert.equal(findAll(PILL_SELECTORS.valueSelectInput).length, 1, 'Should be placed in pill-value');
};

// metaString, no operator ob, no value
const _validateUntilInvalidMeta = (assert, metaString) => {
  const text = find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim();
  assert.ok(
    text.includes('meta') && text.includes('filtered'),
    'no relevant meta present'
  );
  assert.equal(find(PILL_SELECTORS.metaInput).value.trim(), metaString, 'With the correct text pasted in');
};

// meta ob, operator string, no value
const _validateUntilInvalidOperator = (assert, meta, operatorString) => {
  // meta should be selected
  assert.equal(find(PILL_SELECTORS.meta).textContent.trim(), meta, 'correct pill-meta selected');
  // operator couldn't be mapped, so pasting the remaining string in operator
  // Preserve the string once we've decided it's not a valid object
  assert.equal(
    find(PILL_SELECTORS.operatorSelectInput).value.trim(),
    operatorString,
    'correct text should be placed in operator power-select'
  );
  // no relevant operators though
  assert.equal(
    find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim(),
    'All operators filtered out',
    'no relevant operator present'
  );
};

module('Integration | Component | Query Pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    if (metaOptions.length < 1 || Object.keys(languageAndAliases).length < 1) {
      const state = new ReduxDataHelper(setState).aliases().language().pillsDataEmpty().build();
      metaOptions = metaKeySuggestionsForQueryBuilder(state);
      languageAndAliases = languageAndAliasesForParser(state);
    }
    this.owner.inject('component', 'i18n', 'service:i18n');
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
    assert.equal(find(PILL_SELECTORS.expensiveIndicator).getAttribute('title'), 'Performing this operation might take more time.', 'Expected title');
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
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('it allows you to select a meta value', async function(assert) {
    this.set('metaOptions', metaOptions);
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
    this.set('metaOptions', metaOptions);
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
    this.set('metaOptions', metaOptions);
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
    assert.equal(findAll(PILL_SELECTORS.valueSelectInput).length, 1, 'Missing value input field');
  });

  test('it allows you to edit the meta after it was selected', async function(assert) {
    this.set('metaOptions', metaOptions);
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
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
      assert.propEqual(data, { meta: 'a', operator: '=', value: '\'x\'', type: 'query' }, 'Message sent for pill create contains correct pill data');
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
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
      assert.propEqual(data, { meta: 'a', operator: 'exists', value: null, type: 'query' }, 'Message sent for pill create contains correct pill data');
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

  test('A pill when supplied with meta, operator (length), and value will not wrap the value in quotes', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
      assert.propEqual(data, { meta: 'c', operator: 'length', value: '3', type: 'query' }, 'Message sent for pill create contains correct pill data');
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

    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 2); // option C

    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'length');

    await waitUntil(() => find(PILL_SELECTORS.valueTrigger));

    await fillIn(PILL_SELECTORS.valueSelectInput, '3');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', THREE_KEY); // 3

    // Create pill
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('A pill without type Text will still wrap a value in quotes if it is a valid alias', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('languageAndAliasesForParser', languageAndAliases);

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
      assert.propEqual(data, { meta: 'ip.proto', operator: '=', value: '\'TCP\'', type: 'query' }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    await render(hbs`
      {{query-container/query-pill
        position=0
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
        languageAndAliasesForParser=languageAndAliasesForParser
      }}
    `);

    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 14); // ip.proto

    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));

    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');

    await waitUntil(() => find(PILL_SELECTORS.valueTrigger));

    await fillIn(PILL_SELECTORS.valueSelectInput, 'TCP');

    // Create pill
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('A pill without type Text will not wrap a text value in quotes if it is not a valid alias', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('languageAndAliasesForParser', languageAndAliases);

    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
      // Note the lack of quotes around TCCP in the line below
      assert.propEqual(data, { meta: 'ip.proto', operator: '=', value: 'TCCP', type: 'query' }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');

      done();
    });

    await render(hbs`
      {{query-container/query-pill
        position=0
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
        languageAndAliasesForParser=languageAndAliasesForParser
      }}
    `);

    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 14); // ip.proto

    await waitUntil(() => find(PILL_SELECTORS.operatorTrigger));

    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');

    await waitUntil(() => find(PILL_SELECTORS.valueTrigger));

    await fillIn(PILL_SELECTORS.valueSelectInput, 'TCCP');

    // Create pill
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('selecting meta sends out correct messages', async function(assert) {
    assert.expect(2);
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      } else if (messageType === MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT) {
        assert.equal(data, 'a', 'Message sent for recent query suggestions doesnt contain correct pill data');
      } else if (messageType === MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS) {
        assert.equal(data.metaName, 'a', 'Message sent for value suggestions does not contain correct meta');
      }
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
  });

  test('selecting meta and operator that does accept values sends out a query suggestions message', async function(assert) {
    const done = assert.async();
    let count = 0;
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }

      if (count === 0) {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT, 'Message sent for recent query suggestions is not correct');
        assert.equal(data, 'a', 'Message sent for recent query suggestions doesnt contain correct pill data');
        count++;
      } else if (count === 1) {
        // Will send out a call to fetch pill values whenever a meta is selected.
        assert.equal(messageType, MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS, 'Message sent for pill value suggestions is not correct');
        assert.equal(data.metaName, 'a', 'Message sent for pill value suggestions doesnt contain correct pill data');
        count++;
      } else {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT, 'Message sent for recent query suggestions is not correct');
        assert.equal(data, 'a =', 'Message sent for recent query suggestions doesnt contain correct pill data');
        done();
      }
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

    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');
  });

  test('selecting an operator that does not accept values will not send a message to recent query api', async function(assert) {
    const done = assert.async();
    let count = 0;
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }

      if (count === 0) {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT, 'Message sent for recent query suggestions is not correct');
        assert.equal(data, 'a', 'Message sent for recent query suggestions doesnt contain correct pill data');
        count++;
      } else if (count === 1) {
        // Will send out a call to fetch pill values whenever a meta is selected.
        assert.equal(messageType, MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS, 'Message sent for pill value suggestions is not correct');
        assert.equal(data.metaName, 'a', 'Message sent for pill value suggestions doesnt contain correct pill data');
        count++;
      } else {
        assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Message sent for pill create is not correct');
        assert.propEqual(data, { meta: 'a', operator: 'exists', value: null, type: 'query' }, 'Message sent for pill create contains correct pill data');
        done();
      }
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

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'exists');
  });

  test('presents a delete icon when not active and pill created', async function(assert) {
    _setPillData(this);
    await render(hbs`{{query-container/query-pill isActive=false pillData=pillData}}`);
    assert.equal(findAll(PILL_SELECTORS.deletePill).length, 1, 'Delete pill component is present');
  });

  test('does not present a delete icon when not a created pill', async function(assert) {
    this.set('pillData', null);
    this.set('metaOptions', metaOptions);
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
      assert.propEqual(data,
        { id: '1', meta: 'a', operator: '=', value: '\'x\'', type: 'query', isSelected: false, isFocused: false },
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

    this.set('metaOptions', metaOptions);
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

    this.set('metaOptions', metaOptions);

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
    this.set('metaOptions', metaOptions);

    this.set('handleMessage', (messageType, data, position) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        assert.deepEqual(data.id, undefined, 'Pill data goes not contain an id');
        assert.equal(position, 12, 'Message sent for pill entered contains correct pill position');
        done();
      }
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=12
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.meta);
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

    this.set('metaOptions', metaOptions);
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

    this.set('metaOptions', metaOptions);
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

    this.set('metaOptions', metaOptions);
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
    this.set('metaOptions', metaOptions);
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
    await waitUntil(() => find(PILL_SELECTORS.valueSelectInput));
    // Fill in the value, to properly simulate the event we need to fillIn AND
    // triggerKeyEvent for the "x" character.
    await fillIn(PILL_SELECTORS.valueSelectInput, 'x');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', X_KEY); // x
    await blur(PILL_SELECTORS.valueTrigger);
    assert.equal(trim(find(PILL_SELECTORS.queryPill).textContent), 'a=x');
  });

  test('If pill is active and single clicked, it will not message up', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', (messageType) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.ok(false, `Should not get here with ${messageType}`);
    });
    this.set('metaOptions', metaOptions);

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
    this.set('metaOptions', metaOptions);
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
    await selectChoose(PILL_SELECTORS.meta, PILL_SELECTORS.powerSelectOption, 1); // a
    await selectChoose(PILL_SELECTORS.operator, '=');
    await fillIn(PILL_SELECTORS.valueSelectInput, 'foo');// missing wrapping quotes
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('it does not quote the pill value when meta is type "UInt"', async function(assert) {
    this.set('metaOptions', metaOptions);
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
    await fillIn(PILL_SELECTORS.valueSelectInput, '80');
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  test('Does not add quotes to a string if there are already single quotes', async function(assert) {
    this.set('pillData', _getEnrichedPill(this));
    await render(hbs`
      {{query-container/query-pill
        isActive=false
        pillData=pillData
        metaOptions=metaOptions
      }}
    `);
    await waitUntil(() => {
      const el = find(PILL_SELECTORS.value);
      return el && trim(el.textContent) !== '';
    });
    assert.equal(trim(find(PILL_SELECTORS.value).textContent), '\'x\'');
  });

  test('replace double quotes with single quotes', async function(assert) {
    this.set('metaOptions', metaOptions);
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
    await selectChoose(PILL_SELECTORS.meta, PILL_SELECTORS.powerSelectOption, 1); // a
    await selectChoose(PILL_SELECTORS.operator, '=');
    await fillIn(PILL_SELECTORS.valueSelectInput, '"foo"');// double quotes
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
  });

  // skip tests that are failing due to Backtracking
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
    assert.equal(find(PILL_SELECTORS.valueSelectInput).selectionStart, 3, 'not at end of value'); // 'a'|
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_LEFT_KEY);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_LEFT_KEY);
    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_LEFT_KEY);
    // TODO: The following assert fails because the cursor does not seem to move
    // to the left. Need to figure out how to make the cursor move.
    assert.equal(find(PILL_SELECTORS.valueSelectInput).selectionStart, 0, 'not at beginning of value'); // |'a'
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
      assert.propEqual(data, { meta: 'a', operator: '=', value: '\'x\'', type: 'query', id: '1', isSelected: false, isFocused: true }, 'Message sent contains correct pill data');
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
      assert.propEqual(data, { meta: 'a', operator: '=', value: '\'x\'', type: 'query', id: '1', isSelected: false, isFocused: true }, 'Message sent contains correct pill data');
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
      assert.propEqual(data, { meta: 'a', operator: '=', value: '\'x\'', type: 'query', id: '1', isSelected: false, isFocused: true }, 'Message sent contains correct pill data');
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

  // skip tests that are failing due to Backtracking
  skip('An edited pill, when supplied with meta and operator that does not accept a value, will send a message to create', async function(assert) {
    const done = assert.async();
    const pillState = new ReduxDataHelper(setState)
      .pillsDataPopulated()
      .language()
      .build();
    const [ enrichedPill ] = enrichedPillsData(pillState);

    this.set('pillData', enrichedPill);
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data, position) => {
      if (messageType === MESSAGE_TYPES.PILL_EDITED) {
        assert.deepEqual(data, { id: '1', isSelected: false, isFocused: false, meta: 'a', operator: 'exists', value: null }, 'Message sent for pill create contains correct pill data');
        assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
        done();
      }
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
    await doubleClick(PILL_SELECTORS.queryPill);
    // Click on operator
    await click(PILL_SELECTORS.operator);
    // Delete selected option to bring up full list
    await typeInSearch(PILL_SELECTORS.operator, '');
    // Choose the third operator option which does not require a value
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 0); // option exists
  });

  test('if no meta/operator/value is selected and ARROW_LEFT is pressed, message is sent up', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('pillData', []);
    this.set('metaOptions', metaOptions);

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
    this.set('metaOptions', metaOptions);

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

  test('it sends a message up from meta to create a free-form pill', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.propEqual(data, {
          complexFilterText: '(foobar)',
          type: 'complex'
        }, 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('(foobar)');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it sends a message up from operator to create a free-form pill', async function(assert) {
    const done = assert.async();
    const _getOption = (arr, str) => arr.find((d) => d.innerText.includes(str));
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.propEqual(data, {
          complexFilterText: 'a (foobar)',
          type: 'complex'
        }, 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/query-pill
        canPerformTextSearch=true
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.queryPill);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);
    await clickTrigger(PILL_SELECTORS.operator);
    await typeInSearch('(foobar)');
    const pillText = find(PILL_SELECTORS.activeQueryPill).textContent;
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const freeFormOption = _getOption(afterOptions, AFTER_OPTION_FREE_FORM_LABEL);
    const textOption = _getOption(afterOptions, AFTER_OPTION_TEXT_LABEL);
    assert.equal(trim(freeFormOption.textContent), trim(`${pillText}${AFTER_OPTION_FREE_FORM_LABEL}`), 'free-form after-option label incorrect');
    assert.equal(trim(textOption.textContent), trim(`${pillText}${AFTER_OPTION_TEXT_LABEL}`), 'text after-option label incorrect');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
  });

  test('it sends a message up from value to create a free-form pill', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.propEqual(data, {
          complexFilterText: 'a = (foobar)',
          type: 'complex'
        }, 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.queryPill);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0); // a
    await selectChoose(PILL_SELECTORS.operatorTrigger, PILL_SELECTORS.powerSelectOption, 2); // =
    await clickTrigger(PILL_SELECTORS.value);
    await typeInSearch('(foobar)');
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const freeFormFilter = afterOptions.find((d) => d.textContent.includes('Free-Form Filter'));
    assert.ok(freeFormFilter, 'unable to find Free-Form Filter option');
    await click(freeFormFilter);
  });

  test('clicking away pill when meta tab is selected, then clicking back in - meta tab is selected', async function(assert) {
    this.set('metaOptions', metaOptions);

    const assertTabContents = (assert, metaTab, recentQueriesTab) => {
      assert.ok(find(PILL_SELECTORS.pillTabs), 'Should be able to see tabs in current component');
      assert.ok(find(metaTab), 'Should be able to see meta tab in current component');
      assert.ok(find(recentQueriesTab), 'Should be able to see recent queries tab in current component');
    };
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    // Should be able to see pill tabs with meta tab selected
    assertTabContents(assert, PILL_SELECTORS.metaTabSelected, PILL_SELECTORS.recentQueriesTab);
    // click on recent queries tab
    await click(PILL_SELECTORS.recentQueriesTab);
    // remove focus
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ESCAPE_KEY);
    // trigger drop-down
    await clickTrigger(PILL_SELECTORS.recentQuery);
    // should be able to see tabs with recent queries tab selected
    assertTabContents(assert, PILL_SELECTORS.metaTab, PILL_SELECTORS.recentQueriesTabSelected);

  });

  test('Pressing tab toggles between meta and recent queries tab', async function(assert) {
    this.set('metaOptions', metaOptions);

    const assertTabContents = (assert, metaTab, recentQueriesTab) => {
      assert.ok(find(PILL_SELECTORS.pillTabs), 'Should be able to see tabs in current component');
      assert.ok(find(metaTab), 'Should be able to see meta tab in current component');
      assert.ok(find(recentQueriesTab), 'Should be able to see recent queries tab in current component');
    };
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', TAB_KEY);
    // should be able to see tabs with recent queries tab selected
    assertTabContents(assert, PILL_SELECTORS.metaTab, PILL_SELECTORS.recentQueriesTabSelected);

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', TAB_KEY);
    // Should be able to see pill tabs with meta tab selected
    assertTabContents(assert, PILL_SELECTORS.metaTabSelected, PILL_SELECTORS.recentQueriesTab);

  });

  test('If no recent queries, tabbing to recentQueries tab will show a placeholder message', async function(assert) {
    assert.expect(6);
    this.set('metaOptions', metaOptions);

    const assertTabContents = (assert, metaTab, recentQueriesTab) => {
      assert.ok(find(PILL_SELECTORS.pillTabs), 'Should be able to see tabs in current component');
      assert.ok(find(metaTab), 'Should be able to see meta tab in current component');
      assert.ok(find(recentQueriesTab), 'Should be able to see recent queries tab in current component');
    };
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', TAB_KEY);

    assertTabContents(assert, PILL_SELECTORS.metaTab, PILL_SELECTORS.recentQueriesTabSelected);

    // Should be able to see the placeholder for recent queries if none are present
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', TAB_KEY);

    await selectChoose(PILL_SELECTORS.meta, 'sessionid');

    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', TAB_KEY);
    // can see it in operator component too
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', TAB_KEY);

    await selectChoose(PILL_SELECTORS.operator, '=');

    await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', TAB_KEY);

    // can see it in value component too
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');
  });

  test('Recent Queries are available as power-select options if not empty', async function(assert) {
    this.set('metaOptions', metaOptions);
    const state = new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesUnfilteredList()
      .build();

    const { investigate: { queryNode: { recentQueriesUnfilteredList } } } = state;

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', TAB_KEY);

    const selectorArray = findAll(PILL_SELECTORS.recentQueriesOptions);
    const optionsArray = selectorArray.map((el) => el.textContent);
    const expectedRecentQueries = recentQueriesUnfilteredList.map((q) => q.query);
    assert.deepEqual(expectedRecentQueries, optionsArray, 'Found the correct recent queries in the powerSelect');

  });

  // ***************************** Typed in Recent Queries and toggled to Meta **************** //

  // ***************************** Requests coming from  pill-meta **************************** //

  // Testing pill-meta behavior
  // type in `al`
  test('Tabbing to meta component with prepopulated meta string with many matches will filter meta options', async function(assert) {
    assert.expect(3);

    this.set('metaOptions', metaOptions);

    // options for typed in `al`
    const optionsSet = [
      'alias.ip',
      'alias.ipv6',
      'alias.mac',
      'alert'
    ];
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'al');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);

    // Should see meta options relevant to text typed in
    _assertOptionsInPowerSelect(assert, optionsSet, PILL_SELECTORS.powerSelectOptionValue);
  });

  // type in `alert`
  test('Tabbing to meta component with string that maps to a meta object and leaves focus in pill-operator', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);

    // Should see meta being selected as it maps to a relevant object
    assert.equal(find(PILL_SELECTORS.meta).textContent.trim(), 'alert', 'correct pill-meta selected');
    // Should be placed in operator drop-down
    assert.equal(findAll(PILL_SELECTORS.operatorTrigger).length, 1, 'operator drop-down visible');
    assert.equal(findAll(PILL_SELECTORS.operatorSelectInput).length, 1, 'operator input visible');
  });

  // type in no relevant text - `foobar`
  test('Tabbing to meta component with prepopulated meta string that does\'t match with any options', async function(assert) {
    assert.expect(4);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'foobar');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // Should see a string with no relevant meta in options
    _validateUntilInvalidMeta(assert, 'foobar');
  });

  // type in no relevant text with a space in between? - `foo bar`
  test('Tabbing to meta component with prepopulated meta string with spaces that does\'t match with any options', async function(assert) {
    assert.expect(4);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'foo bar');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // still in meta component. Space does not put it in pill-operator
    _validateUntilInvalidMeta(assert, 'foo bar');
  });

  // Testing pill-operator behavior
  // type in `alert e`
  test('Tabbing to meta comp with prepopulated operator string with many matches will filter operator options', async function(assert) {
    assert.expect(3);
    const operatorOptions = ['exists', 'ends'];

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert e');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // options relevant to text typed in
    _assertOptionsInPowerSelect(assert, operatorOptions, PILL_SELECTORS.powerSelectOptionValue);
  });

  // type in `alert contains`
  test('Tabbing to meta comp with a string that maps to meta & operaror object will place focus in pill-value', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert contains');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // Should see meta and operator selected, placed in value
    _validateUntilOperator(assert, 'alert', 'contains');
  });

  // type in `alert =`, hit backspace to delete, toggle view should remain same
  test('hitting backspace in a recent queries tab and toggling the view keeps the text intact', async function(assert) {
    assert.expect(6);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert =');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    await toggleTab(PILL_SELECTORS.valueSelectInput);
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', BACKSPACE_KEY);

    // Backspace isn't working as it's supposed to, so this is a workaround.
    await click(PILL_SELECTORS.recentQuery);
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);

    // Should see meta being selected as it maps to a relevant object
    assert.equal(find(PILL_SELECTORS.meta).textContent.trim(), 'alert', 'correct pill-meta selected');
    // Should be placed in operator drop-down
    assert.equal(findAll(PILL_SELECTORS.operatorTrigger).length, 1, 'operator drop-down visible');
    assert.equal(findAll(PILL_SELECTORS.operatorSelectInput).length, 1, 'operator input visible');
    assert.equal(
      find(PILL_SELECTORS.operatorSelectInput).value.trim(),
      '',
      'correct text -> no text should be found after hitting backspace in recent queries tab'
    );
  });

  // type in `alert foo`
  test('Tabbing to meta comp with prepopulated operator string with no matches leaves all options filtered out', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert foo');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // meta selected, text pasted in operator with no relevant options
    _validateUntilInvalidOperator(assert, 'alert', 'foo');
  });

  // type in `alert foo abcd`
  test('Tabbing to meta comp with prepopulated operator with spaces string with no matches leaves all options filtered out', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert foo abcd');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // meta selected, text pasted in operator with no relevant options
    _validateUntilInvalidOperator(assert, 'alert', 'foo abcd');

  });

  // type in `alert exists foo`
  test('Tabbing to meta comp with a operator string that does not accept a value, but is provided one will leave all options filtered out', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert exists foo');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // operator that does not accept values, but is provided with one
    // should remain in operator comp with no options filtered out.
    _validateUntilInvalidOperator(assert, 'alert', 'exists foo');
  });

  // type in `alert ! = foo`
  test('Tabbing to meta comp with spaces between prepopulated operator string will give no results', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert ! = foo');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // Should respect the rules for typing in meta, op
    // Remain in operator with no relevant operators.
    _validateUntilInvalidOperator(assert, 'alert', '! = foo');
  });

  // Testing pill-value behavior
  // type in `alert contains foo`
  test('Tabbing to meta comp with a string that sets meta, operator and value components', async function(assert) {
    assert.expect(6);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await toggleTab(PILL_SELECTORS.metaSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert contains foo');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    // perfect mapping
    _validateUntilValue(assert, 'alert', 'contains', 'foo');


  });

  // ***************************** Requests coming from  pill-operator **************************** //
  // Meta has already been set. Tab was toggled while in operator.

  // Testing pill-operator behavior
  // type in `e`
  test('Tabbing to meta comp with prepopulated operator which matches multiple options', async function(assert) {
    assert.expect(3);
    const operatorOptions = ['exists', 'ends'];

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert e');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    _assertOptionsInPowerSelect(assert, operatorOptions, PILL_SELECTORS.powerSelectOptionValue);
  });

  // type in `=`
  test('Tabbing to meta comp with prepopulated operator that maps to op object will leave the focus on pill-value', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert =');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    _validateUntilOperator(assert, 'alert', '=');
  });

  // type in `foo`
  test('Tabbing to meta comp with prepopulated string that does not match with anything leaves all options filtered out', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert foo bar');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    _validateUntilInvalidOperator(assert, 'alert', 'foo bar');
  });

  // type in `! = foo`
  test('Tabbing to meta comp with prepopulated operator string with spaces in between leaves all options filtered out', async function(assert) {
    assert.expect(5);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    // we type in alert again as fillIn replaces the current value
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert ! = foo');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    _validateUntilInvalidOperator(assert, 'alert', '! = foo');
  });

  // Testing pill-value behavior
  // type in `!= foo`
  test('Tabbing to meta comp with prepopulated operator string that maps to operator, sets text in pill-value with focus', async function(assert) {
    assert.expect(6);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
    assert.equal(findAll(PILL_SELECTORS.noResultsMessageSelector).length, 1, 'Did not find No Recent Queries Message');
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');

    // we type in alert again as fillIn replaces the current value
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert != foo');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
    await settled();

    _validateUntilValue(assert, 'alert', '!=', 'foo');
  });

  // ***************************** END of Typed in Recent Queries and toggled to Meta Tab**************** //

  test('Typing in recent query tab should broadcast a message', async function(assert) {
    const done = assert.async();

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, stringifiedPillText) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
      assert.equal(stringifiedPillText, 'foo');
      done();
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'foo');
    // This is just to make sure `foo` is actually recorded as typed in event
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);
  });

  test('Typing in recent query tab will not broadcast a message if just spaces are typed in', async function(assert) {
    assert.expect(0);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      assert.ok(false, `Should not get here with ${messageType}`);
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);
  });

  test('Typing spaces when some text is present in recent query tab will broadcast a message', async function(assert) {
    const done = assert.async();

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, stringifiedPillText) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
      assert.equal(stringifiedPillText, 'a ');
      done();
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'a ');

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);
  });

  test('Typing in pill-meta tab and toggling to recent queries will send out a message', async function(assert) {
    const done = assert.async();

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, stringifiedPillText) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
      assert.equal(stringifiedPillText, 'alert');
      done();
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await fillIn(PILL_SELECTORS.metaInput, 'alert');

    await toggleTab(PILL_SELECTORS.metaSelectInput);
  });

  test('Typing in pill-operator and toggling to recent queries will send out a message', async function(assert) {

    const done = assert.async();
    let count = 0;

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, stringifiedPillText) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }

      // It will be triggered twice because we need a count to be updated everytime
      // some text is entered or a selection is made.
      if (count === 0) {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
        assert.equal(stringifiedPillText, 'alert');
        count++;
      } else if (count === 1) {
        // Will send out a call to fetch pill values whenever a meta is selected.
        assert.equal(messageType, MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS, 'Message sent for pill value suggestions is not correct');
        assert.equal(stringifiedPillText.metaName, 'alert', 'Message sent for pill value suggestions doesnt contain correct pill data');
        count++;
      } else {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
        assert.equal(stringifiedPillText, 'alert contai');
        done();
      }
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await fillIn(PILL_SELECTORS.operatorSelectInput, 'contai');

    await toggleTab(PILL_SELECTORS.operatorSelectInput);
  });

  test('Typing in pill-value and toggling to recent queries tab will send out a message', async function(assert) {
    const done = assert.async();
    let count = 0;

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, stringifiedPillText) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      // This message is sent everytime some text is entered or a selection is made, as
      // we need to keep the counts in sync.
      if (count === 0) {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
        assert.equal(stringifiedPillText, 'alert');
        count++;
      } else if (count === 1) {
        // Will send out a call to fetch pill values whenever a meta is selected.
        assert.equal(messageType, MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS, 'Message sent for pill value suggestions is not correct');
        assert.equal(stringifiedPillText.metaName, 'alert', 'Message sent for pill value suggestions doesnt contain correct pill data');
        count++;
      } else if (count === 2) {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
        assert.equal(stringifiedPillText, 'alert contains');
        count++;
      } else if (count === 3) {
        assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
        assert.equal(stringifiedPillText, 'alert contains foo');
        count++;
      } else if (count === 4) {
        // Will send out a call to fetch pill values whenever a meta is selected or text is typed in pill-value
        assert.equal(messageType, MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS, 'Message sent for pill value suggestions is not correct');
        assert.equal(stringifiedPillText.filter, 'foo', 'Message sent for pill value suggestions doesnt contain correct pill data');
        done();
      }
    });

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'contains');

    await fillIn(PILL_SELECTORS.valueSelectInput, 'foo');

    await toggleTab(PILL_SELECTORS.valueSelectInput);
  });

  test('Typing text in meta tab from pill-meta and toggling over to recent-query tab will filter the current list', async function(assert) {
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();
    this.set('metaOptions', metaOptions);

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 7, 'Did not find some recent queries?');
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'medium');

    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 3, 'Did not find some recent queries?');
  });

  test('Typing text in meta-tab and toggling over to recent-query tab from pill-operator will filter the list', async function(assert) {
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'medium');
    await toggleTab(PILL_SELECTORS.operatorSelectInput);

    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 3, 'Did not find some recent queries?');
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'medium = 1');

    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 1, 'Did not find some recent queries?');
  });

  test('Typing text in meta-tab tab and toggling over to recent-query from pill-value will filter the list', async function(assert) {
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'medium');

    // Now in pill-operator component
    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');

    await toggleTab(PILL_SELECTORS.valueSelectInput);
    await settled();
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 3, 'Did not find some recent queries?');

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'medium = 32');
    await settled();
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 3, 'Did not find some recent queries?');

  });

  test('When no text is present, unfiltered List is displayed in recent-query', async function(assert) {
    const state = new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();
    this.set('metaOptions', metaOptions);
    const { investigate: { queryNode: { recentQueriesUnfilteredList } } } = state;

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    const selectorArray = findAll(PILL_SELECTORS.recentQueriesOptions);
    const optionsArray = selectorArray.map((el) => el.textContent);
    const unfilteredList = recentQueriesUnfilteredList.map((ob) => ob.query);
    assert.deepEqual(unfilteredList, optionsArray, 'There is a mis-match in the options displayed');
  });

  test('Pressing escape from pill-meta when there is some partially entered text cleans up the input', async function(assert) {
    assert.expect(1);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await fillIn(PILL_SELECTORS.metaInput, 'al');
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);

    assert.equal(find('.ember-power-select-search-input').textContent, '', 'Found text in the input bar on escape');
  });

  test('Pressing escape from pill-operator when there is some partially entered text cleans up the input', async function(assert) {
    assert.expect(1);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');


    await fillIn(PILL_SELECTORS.operatorSelectInput, '!');
    await triggerKeyEvent(PILL_SELECTORS.operatorTrigger, 'keydown', ESCAPE_KEY);

    assert.equal(find('.ember-power-select-search-input').textContent, '', 'Found text in the input bar on escape');
  });

  test('Pressing escape from pill-value when there is some partially entered text cleans up the input', async function(assert) {
    assert.expect(1);

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, '=');

    await fillIn(PILL_SELECTORS.valueSelectInput, '1');
    await triggerKeyEvent(PILL_SELECTORS.valueTrigger, 'keydown', ESCAPE_KEY);

    assert.equal(find('.ember-power-select-search-input').textContent, '', 'Found text in the input bar on escape');
  });

  test('selecting a meta in meta tab, switching over to recent and deleting, toggling back to meta deletes the selected meta', async function(assert) {
    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');
    await toggleTab(PILL_SELECTORS.operatorSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, '');

    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);

    assert.equal(find(PILL_SELECTORS.meta).textContent.trim(), '', 'incorrect data in pill-meta');
  });

  test('selecting a recent query broadcasts a message', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERY_PILL_CREATED, 'Incorrect message being sent up');
      assert.equal(data, 'medium = 32', 'Incorrect data for recent query being sent up');
      done();
    });
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();
    this.set('metaOptions', metaOptions);

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await selectChoose(PILL_SELECTORS.recentQuery, 'medium = 32');
  });

  test('selecting a recent query through keyboard broadcasts a message', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERY_PILL_CREATED, 'Incorrect message being sent up');
      assert.equal(data, 'medium = 32 || medium = 1', 'Incorrect data for recent query being sent up');
      done();
    });
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .build();
    this.set('metaOptions', metaOptions);

    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ARROW_DOWN_KEY);
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ENTER_KEY);
  });

  test('it dispatched the correct event when a "(" is typed', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_OPEN_PAREN) {
        assert.deepEqual(data, undefined, 'should not include pill data');
        done();
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeIn(PILL_SELECTORS.metaInput, '(');
  });

  test('it dispatched the correct event when a ")" is typed', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data) => {
      if (messageType === MESSAGE_TYPES.PILL_CLOSE_PAREN) {
        assert.deepEqual(data, undefined, 'should not include pill data');
        done();
      }
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        metaOptions=metaOptions
        position=0
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeIn(PILL_SELECTORS.metaInput, ')');
  });

  test('Toggling from recent query with meta selected will send out a message', async function(assert) {
    const done = assert.async();

    this.set('metaOptions', metaOptions);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, data) => {
      if (
        messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW ||
        messageType === MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT
      ) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS);
      assert.equal(data.metaName, 'alert');
      assert.equal(data.filter, 'bar');
      done();
    });
    await render(hbs`
      {{query-container/query-pill
        isActive=true
        position=0
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'alert = bar');
    await toggleTab(PILL_SELECTORS.recentQuerySelectInput);
  });
});
