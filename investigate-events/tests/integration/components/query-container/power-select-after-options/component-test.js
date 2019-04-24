import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render, settled, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import {
  AFTER_OPTION_FREE_FORM_LABEL,
  AFTER_OPTION_TEXT_LABEL
} from 'investigate-events/constants/pill';
import PILL_SELECTORS from '../pill-selectors';

const { log } = console; // eslint-disable-line no-unused-vars

const OPTION_A_LABEL = AFTER_OPTION_FREE_FORM_LABEL;
const OPTION_B_LABEL = AFTER_OPTION_TEXT_LABEL;
const MENU_OPTIONS = [
  { label: OPTION_A_LABEL, disabled: false, highlighted: false },
  { label: OPTION_B_LABEL, disabled: false, highlighted: false }
];
const MENU_OPTIONS_WITH_HIGHLIGHT = [
  { label: OPTION_A_LABEL, disabled: false, highlighted: true },
  { label: OPTION_B_LABEL, disabled: false, highlighted: false }
];

const EPS_API = {
  results: MENU_OPTIONS,
  resultsCount: null,
  actions: {
    search: () => {}
  }
};

module('Integration | Component | Power Select After Options', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('List of options renders properly', async function(assert) {
    this.set('options', MENU_OPTIONS);
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
      }}
    `);
    assert.ok(find(PILL_SELECTORS.powerSelectAfterOptions), 'List has proper class');
    assert.equal(findAll(PILL_SELECTORS.powerSelectAfterOption).length, 2, 'Proper number of option');
  });

  test('Option label is displayed', async function(assert) {
    this.set('options', MENU_OPTIONS);
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
      }}
    `);
    assert.equal(find(PILL_SELECTORS.powerSelectAfterOption).textContent.trim(), OPTION_A_LABEL, 'Option label is displayed');
  });

  test('Option can be disabled', async function(assert) {
    const disabledOption = {
      ...MENU_OPTIONS[0],
      disabled: true
    };
    this.set('options', [disabledOption]);
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
      }}
    `);
    assert.ok(find(`${PILL_SELECTORS.powerSelectAfterOption}[aria-disabled=true]`), 'Option is disabled');
    assert.notOk(find(`${PILL_SELECTORS.powerSelectAfterOption}[aria-disabled=false]`), 'No enabled options');
  });

  test('Option can be highlighted', async function(assert) {
    const highlightedOption = {
      ...MENU_OPTIONS[0],
      highlighted: true
    };
    this.set('options', [highlightedOption]);
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
      }}
    `);
    assert.ok(find(`${PILL_SELECTORS.powerSelectAfterOption}[aria-current=true]`), 'Option is highlighted');
    assert.notOk(find(`${PILL_SELECTORS.powerSelectAfterOption}[aria-current=false]`), 'No un-highlighted options');
  });

  test('Highlighting an option dispatches a HIGHLIGHTED_AFTER_OPTION event', async function(assert) {
    const done = assert.async();
    this.set('options', MENU_OPTIONS);
    this.set('select', EPS_API);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT) {
        assert.equal(data, 0, 'correct data passed');
        done();
      }
    });
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
        select=(readonly select)
        sendMessage=(action handleMessage)
      }}
    `);
    // mouse over first option
    const firstOption = find(PILL_SELECTORS.powerSelectAfterOption);
    await triggerEvent(firstOption, 'mouseenter');
  });

  test('When highlighted, clicking option will dispatch a CREATE_FREE_FORM_PILL event', async function(assert) {
    const done = assert.async();
    const highlightedOption = {
      ...MENU_OPTIONS[0],
      highlighted: true
    };
    this.set('options', [highlightedOption]);
    this.set('select', EPS_API);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.AFTER_OPTIONS_SELECTED) {
        assert.equal(data, highlightedOption.label, 'correct data');
        done();
      }
    });
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
        select=(readonly select)
        sendMessage=(action handleMessage)
      }}
    `);
    await click(PILL_SELECTORS.powerSelectAfterOption);
  });

  test('First item in list is automatically highlighted if EPS search result has 0 items', async function(assert) {
    const apiWithNoResults = {
      ...EPS_API,
      results: [],
      resultsCount: 0
    };
    this.set('options', MENU_OPTIONS);
    this.set('select', EPS_API);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.AFTER_OPTIONS_HIGHLIGHT) {
        assert.equal(data, 0, 'correct data');
        this.set('options', MENU_OPTIONS_WITH_HIGHLIGHT);
      }
    });
    await render(hbs`
      {{query-container/power-select-after-options
        _previouslyHighlightedIndex=null
        options=(readonly options)
        select=(readonly select)
        sendMessage=(action handleMessage)
      }}
    `);
    const _options = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const hasHighlight = _options.some((d) => d.getAttribute('aria-current') === 'true');
    assert.notOk(hasHighlight, 'no item should be highlighted');
    this.set('select', apiWithNoResults);
    await settled();
    const _options2 = findAll(PILL_SELECTORS.powerSelectAfterOption);
    const hasHighlight2 = _options2.some((d) => d.getAttribute('aria-current') === 'true');
    assert.ok(hasHighlight2, 'an item should be highlighted');
  });
});
