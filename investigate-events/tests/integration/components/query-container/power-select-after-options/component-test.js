import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';

// These labels and options should match what's defined in the component
const OPTION_A_LABEL = 'Free Form Filter';
const OPTION_B_LABEL = 'Text Filter';
const MENU_OPTIONS = [
  { label: OPTION_A_LABEL, disabled: false, highlighted: false },
  { label: OPTION_B_LABEL, disabled: false, highlighted: false }
];

const EPS_API = {
  results: MENU_OPTIONS,
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
      if (type === MESSAGE_TYPES.HIGHLIGHTED_AFTER_OPTION) {
        assert.equal(data, OPTION_A_LABEL, 'correct data passed');
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
    await triggerEvent(firstOption, 'mouseover');
  });

  test('When highlighted, clicking option will dispatch a CREATE_FREE_FORM_PILL event', async function(assert) {
    const done = assert.async();
    const highlightedOption = {
      ...MENU_OPTIONS[0],
      highlighted: true
    };
    this.set('options', [highlightedOption]);
    this.set('select', EPS_API);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.ok('message called');
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
      results: []
    };
    this.set('options', MENU_OPTIONS);
    this.set('select', EPS_API);
    await render(hbs`
      {{query-container/power-select-after-options
        options=(readonly options)
        select=(readonly select)
      }}
    `);
    this.set('select', apiWithNoResults);
    const _options = findAll(PILL_SELECTORS.powerSelectAfterOption);
    assert.equal(_options.length, 1, 'Correct number of options');
    // Use the below when we enable Text Filter
    // assert.equal(_options.length, MENU_OPTIONS.length, 'Correct number of options');
    assert.equal(_options[0].getAttribute('aria-current'), 'true', 'First option is highlighted');
    // drop first item as it's the one that should be highlighted
    _options.shift();
    const hasHighlightedOptions = _options.some((d) => d.getAttribute('aria-current') === 'true');
    assert.notOk(hasHighlightedOptions, 'Other options are NOT highlighted');
  });
});
