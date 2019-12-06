import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, find, findAll, render, triggerKeyEvent, typeIn, waitUntil } from '@ember/test-helpers';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { clickTextFilterOption, createBasicPill, isIgnoredInitialEvent, toggleTab } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { OPERATOR_OR } from 'investigate-events/constants/pill';

let setState;
let metaOptions = [];
const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.key;
const ARROW_DOWN_KEY = KEY_MAP.arrowDown.key;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.key;
const ENTER_KEY = KEY_MAP.enter.key;
const ESCAPE_KEY = KEY_MAP.escape.key;
const SPACE_KEY = KEY_MAP.space.key;
const DELETE_KEY = KEY_MAP.delete.key;
const BACKSPACE_KEY = KEY_MAP.backspace.key;

module('Integration | Component | New Pill Trigger', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    if (metaOptions.length < 1) {
      metaOptions = metaKeySuggestionsForQueryBuilder(
        new ReduxDataHelper(setState).language().pillsDataEmpty().build()
      );
    }
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('shows trigger by default', async function(assert) {
    await render(hbs`{{query-container/new-pill-trigger}}`);
    assert.ok(find(PILL_SELECTORS.newPillTrigger) !== null, 'trigger renders by default');
  });

  test('shows new pill entry when trigger is triggered', async function(assert) {
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('shows new pill entry when trigger position and new pill position match', async function(assert) {
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/new-pill-trigger
        cursorPosition=56
        newPillPosition=56
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('does not show new pill entry when trigger position and new pill position do not match', async function(assert) {
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/new-pill-trigger
        cursorPosition=57
        newPillPosition=56
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 0);
  });

  test('ESC key returns user to trigger', async function(assert) {
    this.set('metaOptions', metaOptions);
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
    await focus(PILL_SELECTORS.metaTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 0);
  });

  test('Entering the new pill broadcasts a message', async function(assert) {
    this.set('metaOptions', metaOptions);
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW, 'Wrong message type');
      assert.deepEqual(data, undefined, 'ENTERED on new pill does not include data');
      assert.equal(position, 5, 'Wrong position number');
    });

    await render(hbs`
      {{query-container/new-pill-trigger
        newPillPosition=5
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
  });

  test('ESC broadcasts a cancel message', async function(assert) {
    this.set('metaOptions', metaOptions);
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_ADD_CANCELLED, 'Wrong message type');
      assert.deepEqual(data, undefined, 'Cancel should not include data');
      assert.equal(position, 5, 'Wrong position number');
    });

    await render(hbs`
      {{query-container/new-pill-trigger
        newPillPosition=5
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.metaTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
  });

  test('it broadcasts a message when a pill is created', async function(assert) {
    this.set('metaOptions', metaOptions);
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Wrong message type');
      assert.propEqual(data, {
        meta: 'a',
        operator: '=',
        value: '\'x\'',
        type: 'query'
      }, 'Message sent for pill create contains correct pill data');
      assert.equal(position, 5, 'Wrong position number');
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        newPillPosition=5
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await createBasicPill();
  });

  test('if no meta/operator/value is selected and ARROW_LEFT is pressed, a message is sent up', async function(assert) {
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT, 'Correct message type');
      assert.equal(position, 0, 'Correct position of the pill');
      assert.notOk(data, 'Data should not be passed');
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT_KEY);
  });

  test('if no meta/operator/value is selected and ARROW_RIGHT is pressed, a message is sent up', async function(assert) {
    assert.expect(3);
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }
      if (messageType === MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT) {
        assert.equal(messageType, MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT, 'Correct message type');
        assert.equal(position, 0, 'Correct position of the pill');
        assert.notOk(data, 'Data should not be passed');
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT_KEY);
  });

  test('it broadcasts a message when creating a free-form pill from meta', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data, position) => {
      if (isIgnoredInitialEvent(type)) {
        return;
      }
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.propEqual(data, {
          type: 'complex',
          complexFilterText: '(foobar)'
        }, 'Correct data');
        assert.equal(position, 0, 'Correct position of the pill');
      }
      done();
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('(foobar)');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message when creating a free-form pill from operator', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data, position) => {
      if (isIgnoredInitialEvent(type)) {
        return;
      }
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.propEqual(data, {
          type: 'complex',
          complexFilterText: 'a (foobar)'
        }, 'Correct data');
        assert.equal(position, 0, 'Correct position of the pill');
        done();
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);
    await clickTrigger(PILL_SELECTORS.operator);
    await typeInSearch('(foobar)');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message when creating a text pill from meta', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data, position) => {
      if (isIgnoredInitialEvent(type)) {
        return;
      }
      if (type === MESSAGE_TYPES.CREATE_TEXT_PILL) {
        assert.propEqual(data, {
          type: 'text',
          searchTerm: 'foobar'
        }, 'Correct data');
        assert.equal(position, 0, 'Correct position of the pill');
      }
      done();
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('foobar');
    await clickTextFilterOption();
  });

  test('Typing in recent query tab should broadcast a message', async function(assert) {
    const done = assert.async();
    const recentQueriesUnfilteredList = [];
    const recentQueriesFilteredList = [];

    this.set('metaOptions', metaOptions);
    this.set('recentQueriesUnfilteredList', recentQueriesUnfilteredList);
    this.set('recentQueriesFilteredList', recentQueriesFilteredList);

    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    this.set('handleMessage', (messageType, stringifiedPillText) => {
      if (messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW ||
          messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW
      ) {
        return;
      }
      assert.equal(messageType, MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT);
      assert.equal(stringifiedPillText, 'foo');
      done();
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        recentQueriesUnfilteredList=recentQueriesUnfilteredList
        recentQueriesFilteredList=recentQueriesFilteredList
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.newPillTrigger);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'foo');
    // This is just to make sure `foo` is actually recorded as typed in event
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', SPACE_KEY);
  });

  test('selecting a recent query broadcasts a message from new-pill-trigger', async function(assert) {
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

  test('it broadcasts a message when a "(" is typed', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data, position) => {
      if (messageType === MESSAGE_TYPES.PILL_OPEN_PAREN) {
        assert.deepEqual(data, undefined, 'should not include pill data');
        assert.equal(position, 1, 'correct position number');
        done();
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=1
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.metaTrigger);
    await typeIn(PILL_SELECTORS.metaInput, '(');
  });

  test('it broadcasts a message when a ")" is typed', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data, position) => {
      if (messageType === MESSAGE_TYPES.PILL_CLOSE_PAREN) {
        assert.deepEqual(data, undefined, 'should not include pill data');
        assert.equal(position, 1, 'correct position number');
        done();
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=1
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.metaTrigger);
    await typeIn(PILL_SELECTORS.metaInput, ')');
  });

  test('can create new pills using value suggestions in new pill trigger', async function(assert) {
    assert.expect(2);
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    const suggestions = [
      {
        displayName: 'fooboom',
        description: 'BOO',
        type: 'Suggestions'
      },
      {
        displayName: 'barboom',
        description: 'BOO',
        type: 'Suggestions'
      }
    ];
    this.set('valueSuggestions', suggestions);

    this.set('handleMessage', (messageType, data) => {
      if (
        messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW ||
        messageType === MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT ||
        messageType === MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW
      ) {
        return;
      } else if (messageType === MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS) {
        assert.equal(data.metaName, 'alert', 'Message sent for value suggestions does not contain correct meta');
      } else if (messageType === MESSAGE_TYPES.PILL_CREATED) {
        assert.propEqual(data, {
          meta: 'alert',
          operator: 'contains',
          value: '\'fooboom\'',
          type: 'query'
        }, 'Message sent for pill create contains correct pill data');
        done();
      }
    });

    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        valueSuggestions=valueSuggestions
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.metaTrigger);

    await selectChoose(PILL_SELECTORS.metaTrigger, 'alert');

    await selectChoose(PILL_SELECTORS.operatorTrigger, 'contains');

    await waitUntil(() => findAll(PILL_SELECTORS.powerSelectOption).length > 1, { timeout: 5000 }).then(async() => {
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN_KEY);
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ARROW_DOWN_KEY);
      await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
    });
  });

  test('it dispatched the correct event when a logical operator is typed', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (messageType, data, position) => {
      if (messageType === MESSAGE_TYPES.PILL_LOGICAL_OPERATOR) {
        const { operator, pillData } = data;
        assert.equal(operator.type, OPERATOR_OR, 'correct type of operator sent');
        assert.notOk(pillData, 'pillData should be undefined');
        assert.equal(position, 1, 'correct position sent');
        done();
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=1
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.metaTrigger);
    await typeIn(PILL_SELECTORS.metaInput, 'OR');
  });


  test('DELETE broadcasts a mete delete message', async function(assert) {
    const done = assert.async();
    assert.expect(5);
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED) {
        assert.ok(data, 'should send out pill data');
        assert.ok(data.isDeleteEvent, 'should be a delete event');
        assert.ok(!data.isFocusedPill, 'should be a focused pill');
        done();
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=0
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.newPillTrigger).length, 0, 'In the new pill state');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', DELETE_KEY);
    await waitUntil(() => findAll(PILL_SELECTORS.newPillTrigger).length > 0, { timeout: 5000 }).then(async() => {
      assert.equal(findAll(PILL_SELECTORS.newPillTrigger).length, 1, 'It is not in the new pill state');
    });
  });

  test('Backspace broadcasts a message', async function(assert) {
    const done = assert.async();
    assert.expect(4);
    this.set('metaOptions', metaOptions);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED) {
        assert.ok(data, 'should send out pill data');
        assert.ok(data.isBackspaceEvent, 'should be a backspace event');
        assert.ok(!data.isFocusedPill, 'is not a focused pill');
        done();
      }
    });
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
        newPillPosition=1
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', BACKSPACE_KEY);
    await waitUntil(() => findAll(PILL_SELECTORS.newPillTrigger).length > 0, { timeout: 5000 }).then(async() => {
      assert.equal(findAll(PILL_SELECTORS.newPillTrigger).length, 1, 'It is not in the new pill state');
    });
  });
});