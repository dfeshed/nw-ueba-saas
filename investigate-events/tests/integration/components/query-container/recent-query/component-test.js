import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, typeInSearch } from 'ember-power-select/test-support/helpers';
import { click, fillIn, find, findAll, render, triggerKeyEvent, typeIn } from '@ember/test-helpers';

import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL,
  AFTER_OPTION_TEXT_DISABLED_LABEL
} from 'investigate-events/constants/pill';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';

let setState;

const META_OPTIONS = metaKeySuggestionsForQueryBuilder(
  new ReduxDataHelper(setState).language().pillsDataEmpty().build()
);

const { log } = console;// eslint-disable-line no-unused-vars

const ARROW_DOWN = KEY_MAP.arrowDown.code;
const ARROW_UP = KEY_MAP.arrowUp.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const TAB_KEY = KEY_MAP.tab.code;
const ENTER_KEY = KEY_MAP.enter.code;
const modifiers = { shiftKey: true };

// This trim also removes extra spaces inbetween words
const trim = (text) => text.replace(/\s+/g, ' ').trim();

module('Integration | Component | Recent Query', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it displays recent queries', async function(assert) {
    assert.expect(1);
    const state = new ReduxDataHelper(setState)
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);

    const { investigate: { queryNode: { recentQueriesUnfilteredList } } } = state;
    const recentQueriesArray = recentQueriesUnfilteredList.map((q) => q.query);
    const selectorArray = findAll(PILL_SELECTORS.recentQueriesOptions);
    const optionsArray = selectorArray.map((el) => el.textContent);
    assert.deepEqual(recentQueriesArray, optionsArray, 'Found the correct recent queries in the powerSelect');
  });

  test('If no recent queries, tabbing to recentQueries tab will show a placeholder message', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .pillsDataEmpty()
      .language()
      .build();

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);

    await clickTrigger(PILL_SELECTORS.recentQuery);

    // Should be able to see the placeholder for recent queries if none are present
    assert.ok(find(PILL_SELECTORS.powerSelectNoMatch).textContent.trim().includes('recent'), 'Correct placeholder message');
  });

  test('it broadcasts a message to toggle tabs when tab or shift tab is pressed', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.AFTER_OPTIONS_TAB_TOGGLED, 'Correct message sent up');
    });
    await render(hbs`
      {{query-container/recent-query
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', TAB_KEY);

    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', TAB_KEY, modifiers);
  });

  test('it broadcasts a message when some text is typed in recent queries tab', async function(assert) {
    const done = assert.async();
    const X_CHAR = 88;
    new ReduxDataHelper(setState)
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();

    this.set('handleMessage', (type, { data, dataSource }) => {
      if (type === MESSAGE_TYPES.RECENT_QUERIES_TEXT_TYPED) {
        assert.ok('message dispatched');
        assert.equal(data, 'x', 'Incorrect data is being sent up');
        assert.equal(dataSource, 'recent-query', 'Source of the component is incorrect');
        done();
      }
    });
    await render(hbs`
      {{query-container/recent-query
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    // Type in text
    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'x');
    // Trigger keyDown event to emulate actual type in
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', X_CHAR);
  });

  test('Pressing escape from recent-query when there is some partially entered text cleans up the input and broadcasts a message', async function(assert) {
    assert.expect(2);

    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', () => {});

    await render(hbs`
      {{query-container/recent-query
        isActive=true
        position=0
        sendMessage=(action handleMessage)
      }}
    `);

    await clickTrigger(PILL_SELECTORS.recentQuery);
    // Type in text
    await typeIn(PILL_SELECTORS.recentQuerySelectInput, 'foo = bar');

    this.set('handleMessage', (type) => {
      assert.equal(type, MESSAGE_TYPES.RECENT_QUERIES_ESCAPE_KEY, 'Escape keypress handled');
    });
    await triggerKeyEvent(PILL_SELECTORS.recentQueryTrigger, 'keydown', ESCAPE_KEY);

    assert.equal(find('.ember-power-select-search-input').textContent, '', 'Found text in the input bar on escape');
  });

  test('it shows Advanced Options to create different types of pill in recent query tab', async function(assert) {
    const _hasOption = (arr, str) => arr.some((d) => d.innerText.includes(str));

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    const options = findAll(PILL_SELECTORS.powerSelectAfterOption);
    assert.equal(options.length, 2, 'incorrect number of Advanced Options');
    assert.ok(_hasOption(options, AFTER_OPTION_FREE_FORM_LABEL), 'missing option to create a free-form filter');
    assert.ok(_hasOption(options, AFTER_OPTION_TEXT_LABEL), 'missing option to create a text filter');
  });

  test('it broadcasts a message to create a free-form pill when the ENTER key is pressed from recent query tab', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, ['(foobar)', 'recent-query'], 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/recent-query
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    await typeInSearch('(foobar)');
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ENTER_KEY);
  });

  test('it selects Free-Form Filter via CTRL + ↓ in recent query tab', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ARROW_DOWN, { ctrlKey: true });
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
  });

  test('If no options are present, Text Filter is highlighted by default', async function(assert) {

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent.trim(), AFTER_OPTION_TEXT_LABEL, 'second Advanced Option was not highlighted');
  });

  test('Highlight will move from options in the dropdown to Advanced Options list and back in recent query tab', async function(assert) {

    new ReduxDataHelper(setState)
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);

    await clickTrigger(PILL_SELECTORS.recentQuery);
    // Reduce options to those that have an "ip"
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'ip');
    // Arrow down two places
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ARROW_DOWN);
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ARROW_DOWN);
    // Should be in Advanced Options now
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(trim(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent), AFTER_OPTION_FREE_FORM_LABEL, 'first Advanced Option was not highlighted');
    // Arrow up
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ARROW_UP);
    // Should be back in meta options list
    assert.notOk(find(PILL_SELECTORS.powerSelectAfterOptionHighlight), 'no Advanced Options should be highlighted');
    assert.ok(find(PILL_SELECTORS.powerSelectOption), 'meta option should be highlighted');
  });

  test('Highlight will NOT move from Advanced Options to main list if all options have been filtered out in recent query tab', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .recentQueriesFilteredList()
      .recentQueriesUnfilteredList()
      .recentQueriesCallInProgress()
      .build();
    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    // Reduce options to those that have an "x", which are none
    await fillIn(PILL_SELECTORS.recentQuerySelectInput, 'x');
    // Should be in Advanced Options now
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'only one option should be highlighted');
    assert.equal(trim(find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent), AFTER_OPTION_TEXT_LABEL, 'first Advanced Option was not highlighted');
    // Arrow up
    await triggerKeyEvent(PILL_SELECTORS.recentQuerySelectInput, 'keydown', ARROW_UP);
    // Should still be back in Advanced Options
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOptionHighlight).length, 1, 'advanced option did not retain highlighting');
  });

  test('it broadcasts a message to create a text pill', async function(assert) {
    const done = assert.async();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_TEXT_PILL) {
        assert.ok(Array.isArray(data), 'correct data type');
        assert.propEqual(data, ['foobar', 'recent-query'], 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/recent-query
        isActive=true
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    await typeInSearch('foobar');
    // trigger Text Filter option
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_LABEL));
    assert.ok(textFilter, 'unable to find Text Filter option');
    await click(textFilter);
  });

  test('it renders a disabled option for text filters when there is a text filter already in recent query tab', async function(assert) {
    await render(hbs`
      {{query-container/recent-query
        isActive=true
        hasTextPill=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    // trigger Text Filter option
    const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const textFilter = afterOptions.find((d) => d.textContent.includes(AFTER_OPTION_TEXT_DISABLED_LABEL));
    assert.ok(textFilter, 'unable to find Text Filter option');
  });

  test('it highlights proper Advanced Option if all EPS options filtered out in recent query tab', async function(assert) {
    let option;

    await render(hbs`
      {{query-container/recent-query
        isActive=true
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    // Type in text
    await typeInSearch('x');
    option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_TEXT_LABEL), 'Text Filter was not highlighted');
    // Reset
    await typeInSearch('');
    // Type in complex text
    await typeInSearch('(x)');
    option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_FREE_FORM_LABEL), 'Free-Form Filter was not highlighted');
  });

  test('it highlights Text Filter Advanced Option if pre-populated with query text that does not match any recent queries', async function(assert) {
    await render(hbs`
      {{query-container/recent-query
        isActive=true
        prepopulatedRecentQueryText='browser'
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    const option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_TEXT_LABEL), 'Text Filter was not highlighted');
  });

  test('it highlights Free-Form Filter Advanced Option if pre-populated with query text that does not match any recent queries, and a text filter exists', async function(assert) {
    await render(hbs`
      {{query-container/recent-query
        hasTextPill=true
        isActive=true
        prepopulatedRecentQueryText='browser'
      }}
    `);
    await clickTrigger(PILL_SELECTORS.recentQuery);
    const option = find(PILL_SELECTORS.powerSelectAfterOptionHighlight).textContent;
    assert.ok(option.includes(AFTER_OPTION_FREE_FORM_LABEL), 'Free-Form Filter was not highlighted');
  });

});