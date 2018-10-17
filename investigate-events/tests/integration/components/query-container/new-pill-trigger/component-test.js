import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { createBasicPill, isIgnoredInitialEvent } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';

const META_OPTIONS = metaKeySuggestionsForQueryBuilder(
  new ReduxDataHelper(setState).language().pillsDataEmpty().build()
);

const ENTER_KEY = KEY_MAP.enter.code;
const ESCAPE_KEY = KEY_MAP.escape.code;
const ARROW_LEFT_KEY = KEY_MAP.arrowLeft.code;
const ARROW_RIGHT_KEY = KEY_MAP.arrowRight.code;

let setState;

module('Integration | Component | New Pill Trigger', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('shows trigger by default', async function(assert) {
    await render(hbs`{{query-container/new-pill-trigger}}`);
    assert.ok(find(PILL_SELECTORS.newPillTrigger) !== null, 'trigger renders by default');
  });

  test('shows new pill entry when trigger is triggered', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/new-pill-trigger
        metaOptions=metaOptions
      }}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('shows new pill entry when trigger position and new pill position match', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/new-pill-trigger
        startTriggeredPosition=56
        newPillPosition=56
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('does not show new pill entry when trigger position and new pill position do not match', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
    await render(hbs`
      {{query-container/new-pill-trigger
        startTriggeredPosition=57
        newPillPosition=56
        metaOptions=metaOptions
      }}
    `);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 0);
  });

  test('ESC key returns user to trigger', async function(assert) {
    this.set('metaOptions', META_OPTIONS);
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
    this.set('metaOptions', META_OPTIONS);
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
    this.set('metaOptions', META_OPTIONS);
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_ADD_CANCELLED, 'Wrong message type');
      assert.deepEqual(data, null, 'Cancel does not include pill data');
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
    this.set('metaOptions', META_OPTIONS);
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CREATED, 'Wrong message type');
      assert.deepEqual(data, { meta: 'a', operator: '=', value: '\'x\'' }, 'Message sent for pill create contains correct pill data');
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
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT, 'Correct message type');
      assert.equal(position, 0, 'Correct position of the pill');
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
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (messageType, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT, 'Correct message type');
      assert.equal(position, 0, 'Correct position of the pill');
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
    const done = assert.async(4);
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data, position) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.equal(data, 'foobar', 'Correct data');
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
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('it broadcasts a message when creating a free-form pill from operator', async function(assert) {
    const done = assert.async();
    this.set('metaOptions', META_OPTIONS);
    this.set('handleMessage', (type, data, position) => {
      if (type === MESSAGE_TYPES.CREATE_FREE_FORM_PILL) {
        assert.equal(data, 'a foobar', 'Correct data');
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
    await typeInSearch('foobar');
    await triggerKeyEvent(PILL_SELECTORS.operatorSelectInput, 'keydown', ENTER_KEY);
  });
});