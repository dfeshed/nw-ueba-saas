import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { fillIn, find, findAll, focus, render, settled, triggerKeyEvent } from '@ember/test-helpers';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper, { DEFAULT_LANGUAGES } from '../../../../helpers/redux-data-helper';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import PILL_SELECTORS from '../pill-selectors';

const ARROW_RIGHT = KEY_MAP.arrowRight.code;
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
    await render(hbs`{{query-container/pill-meta isActive=false selection=selection}}`);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
  });

  test('it shows only value if active, but no options', async function(assert) {
    // Set a selection just so we have a value to compare against. Otherwise
    // it'd be an empty string.
    this.set('selection', DEFAULT_LANGUAGES[0]);
    await render(hbs`{{query-container/pill-meta isActive=true selection=selection}}`);
    assert.equal(trim(find(PILL_SELECTORS.meta).textContent), 'a');
  });

  test('it shows a Power Select if active and has options', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/pill-meta isActive=true}}`);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('it broadcasts a message when a Power Select option is choosen', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        assert.deepEqual(data, DEFAULT_LANGUAGES[1], 'Wrong message data');
        done();
      }
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);// option b
  });

  test('it does not broadcasts a message when the ARROW_RIGHT key is pressed and there is no selection', async function(assert) {
    assert.expect(0);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT);
    return settled();
  });

  test('it broadcasts a message when the ARROW_RIGHT key is pressed and there is a selection', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        this.set('selection', data);
      } else if (type === MESSAGE_TYPES.META_ARROW_RIGHT_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`{{query-container/pill-meta isActive=true selection=selection sendMessage=(action handleMessage)}}`);
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 1);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ARROW_RIGHT);
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', (type) => {
      if (type === MESSAGE_TYPES.META_ESCAPE_KEY) {
        assert.ok('message dispatched');
        done();
      }
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    // await focus(metaPowerSelectTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ESCAPE_KEY);
  });

  test('it selects meta if a trailing SPACE is entered and there is one option', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        assert.deepEqual(data, DEFAULT_LANGUAGES[1], 'Wrong message data');
        done();
      }
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    await fillIn('input', 'b ');
  });

  test('it does not selects meta if a trailing SPACE is entered and there is more than one option', async function(assert) {
    assert.expect(0);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', () => {
      assert.notOk('message dispatched');
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    await fillIn('input', 'c. ');// Will match 2 items (c.a and c.b)
    return settled();
  });

  test('it selects meta if a trailing SPACE is entered and there is an exact match', async function(assert) {
    const done = assert.async();
    assert.expect(1);
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    this.set('handleMessage', (type, data) => {
      if (type === MESSAGE_TYPES.META_SELECTED) {
        assert.deepEqual(data, DEFAULT_LANGUAGES[2], 'Wrong message data');
        done();
      }
    });
    await render(hbs`{{query-container/pill-meta isActive=true sendMessage=(action handleMessage)}}`);
    await fillIn('input', 'c ');
  });

  test('it clears out last search if Power Select looses, then gains focus', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/pill-meta isActive=true}}`);
    await focus(PILL_SELECTORS.metaTrigger);
    // assert number of options
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 5);
    // perform a search that down-selects the list of options
    await typeInSearch('c');
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 3); // c, c.a, c.b
    // blur and assert no options present
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', TAB_KEY);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 0);
    // focus and assert number of options
    await focus(PILL_SELECTORS.metaTrigger);
    assert.equal(findAll(PILL_SELECTORS.powerSelectOption).length, 5);
    return settled();
  });
});