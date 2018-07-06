import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { createBasicPill, isIgnoredInitialEvent } from '../pill-util';
import PILL_SELECTORS from '../pill-selectors';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import KEY_MAP from 'investigate-events/util/keys';

const ESCAPE_KEY = KEY_MAP.escape.code;
let setState;

module('Integration | Component | new-pill-trigger', function(hooks) {
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
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/new-pill-trigger}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('shows new pill entry when trigger position and new pill position match', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/new-pill-trigger startTriggeredPosition=56 newPillPosition=56}}`);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
  });

  test('does not show new pill entry when trigger position and new pill position do not match', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/new-pill-trigger startTriggeredPosition=57 newPillPosition=56}}`);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 0);
  });

  test('ESC key returns user to trigger', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/new-pill-trigger}}`);
    await click(PILL_SELECTORS.newPillTrigger);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 1);
    await focus(PILL_SELECTORS.metaTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
    assert.equal(findAll(PILL_SELECTORS.metaTrigger).length, 0);
  });

  test('Entering the new pill broadcasts a message', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW, 'Wrong message type');
      assert.deepEqual(data, undefined, 'ENTERED on new pill does not include data');
      assert.equal(position, 5, 'Wrong position number');
    });

    await render(hbs`
      {{query-container/new-pill-trigger
        newPillPosition=5
        sendMessage=(action handleMessage)}}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
  });

  test('ESC broadcasts a cancel message', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    assert.expect(3);
    this.set('handleMessage', (messageType, data, position) => {
      if (isIgnoredInitialEvent(messageType)) {
        return;
      }

      assert.equal(messageType, MESSAGE_TYPES.PILL_CANCELLED, 'Wrong message type');
      assert.deepEqual(data, null, 'Cancel does not include pill data');
      assert.equal(position, 5, 'Wrong position number');
    });

    await render(hbs`
      {{query-container/new-pill-trigger
        newPillPosition=5
        sendMessage=(action handleMessage)}}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await focus(PILL_SELECTORS.metaTrigger);
    await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
  });

  test('it broadcasts a message when a pill is created', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
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
        sendMessage=(action handleMessage)}}
    `);
    await click(PILL_SELECTORS.newPillTrigger);
    await createBasicPill();
  });
});