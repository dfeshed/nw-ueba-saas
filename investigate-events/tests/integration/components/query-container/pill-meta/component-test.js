import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { blur, fillIn, find, findAll, focus, render, settled, triggerKeyEvent } from '@ember/test-helpers';

import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper, { DEFAULT_LANGUAGES } from '../../../../helpers/redux-data-helper';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';

const META_OPTIONS = metaKeySuggestionsForQueryBuilder(
  new ReduxDataHelper(setState).language().pillsDataEmpty().build()
);

// const { log } = console;

const ARROW_RIGHT = KEY_MAP.arrowRight.code;
const ARROW_LEFT = KEY_MAP.arrowLeft.code;
const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const TAB_KEY = KEY_MAP.tab.code;

const trim = (text) => text.replace(/\s+/g, '').trim();

let setState;

module('Integration | Component | Pill Meta', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it shows only the value if inactive', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', DEFAULT_LANGUAGES[0]);
    this.set('metaOptions', []);
    await render(hbs`
      {{query-container/pill-meta
        isActive=false
        selection=selection
        metaOptions=metaOptions
      }}
    `);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
  });

  test('it shows only value if active, but no options', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', DEFAULT_LANGUAGES[0]);
    this.set('metaOptions', []);
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        selection=selection
        metaOptions=metaOptions
      }}
    `);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
  });

  test('it shows a Power Select if active and has options', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('it broadcasts a message when a Power Select option is choosen', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        assert.deepEqual(data, DEFAULT_LANGUAGES[1], 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);// option b
  });

  test('it broadcasts a message when the ARROW_RIGHT key is pressed and there is no selection', async function(assert) {
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.META_ARROW_RIGHT_KEY_WITH_NO_SELECTION) {
        assert.ok('message dispatched');
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT);
    return settled();
  });

  test('it broadcasts a message when the ARROW_LEFT key is pressed and there is no selection', async function(assert) {
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.META_ARROW_LEFT_KEY) {
        assert.ok('message dispatched');
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_LEFT);
    return settled();
  });

  test('it broadcasts a message when the ARROW_RIGHT key is pressed and there is a selection', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        this.set('selection', data);
      } else if (type === MESSAGE_TYPES.META_ARROW_RIGHT_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        selection=selection
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT);
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.META_ESCAPE_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it broadcasts a message when the ENTER key is pressed and a selection has not been made', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.META_ENTER_KEY) {
        assert.ok('message dispatched');
      }
      done();
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it does not broadcast a message when the ENTER key is pressed and a selection has been made', async function(assert) {
    const done = assert.async(2);
    assert.expect(0);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        // This will be called first to set the selection, so the logic to
        // prevent ENTER key from dispatching an event can work properly
        this.set('selection', data);
      } else if (type === MESSAGE_TYPES.META_ENTER_KEY) {
        // Should not get here
        assert.notOk('message should not be dispatched');
      }
      done();
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        selection=selection
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it does not broadcast a message when the ENTER key is pressed and text has been entered into input', async function(assert) {
    assert.expect(0);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.META_ENTER_KEY) {
        assert.notOk('message should not be dispatched');
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await fillIn(PILL_SELECTORS.metaSelectInput, 'Yuuuuge');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it removes the selection when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    let iteration = 1;
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        this.set('selection', data);
        if (iteration === 2) {
          assert.equal(data, null, 'selection should be null');
          done();
        }
        iteration++;
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        selection=selection
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it selects meta if a trailing SPACE is entered and there is one option', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        assert.deepEqual(data, DEFAULT_LANGUAGES[1], 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await fillIn('input', 'b ');
  });

  test('it does not selects meta if a trailing SPACE is entered and there is more than one option', async function(assert) {
    assert.expect(0);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await fillIn('input', 'c. ');// Will match 2 items (c.a and c.b)
    return settled();
  });

  test('it selects meta if a trailing SPACE is entered and there is an exact match', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        assert.deepEqual(data, DEFAULT_LANGUAGES[2], 'Wrong message data');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await fillIn('input', 'c ');
  });

  test('it clears out last search if Power Select looses, then gains focus', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        metaOptions=metaOptions
      }}
    `);
    await focus(PILL_SELECTORS.metaTrigger);
    // assert number of options
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 21);
    // perform a search that down-selects the list of options
    await typeInSearch('c');
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 8);
    // blur and assert no options present
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', TAB_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 0);
    // focus and assert number of options
    await focus(PILL_SELECTORS.metaTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 21);
    return settled();
  });

  test('if meta is selected (not just half entered) and you click away, leave the meta there', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        this.set('selection', data);
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        selection=selection
        sendMessage=(action handleMessage)
        metaOptions=metaOptions
      }}
    `);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);
    await blur(PILL_SELECTORS.metaTrigger);
    assert.equal(find(PILL_SELECTORS.metaSelectInput).value, 'b');
  });

  test('it broadcasts a message to create a free-form pill when the ENTER key is pressed', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.equal(data, 'foobar', 'correct data');
        assert.equal(find(PILL_SELECTORS.metaInput).value, '', 'meta input was reset');
        done();
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await typeInSearch('foobar');
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it does NOT broadcasts a message to create a free-form pill if no value is entered', async function(assert) {
    assert.expect(0);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.notOk('should not get here');
      }
    });
    await render(hbs`
      {{query-container/pill-meta
        isActive=true
        metaOptions=metaOptions
        sendMessage=(action handleMessage)
      }}
    `);
    await clickTrigger(PILL_SELECTORS.meta);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });
});