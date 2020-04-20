import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers'; //eslint-disable-line

import {
  AFTER_OPTION_TAB_META
} from 'investigate-events/constants/pill';
import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';

module('Integration | Component | query-container/power-select-tabs', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Clicking on deselected tab sends out a message to parent component', async function(assert) {
    assert.expect(1);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.AFTER_OPTIONS_TAB_CLICKED, 'the correct message type is sent');
    });
    await render(hbs`
      {{query-container/power-select-tabs
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);

    await click('.power-select-tabs .recent-queries-tab');
  });

  test('Clicking on selected tab will not send message to parent component', async function(assert) {
    assert.expect(0);
    this.set('activePillTab', AFTER_OPTION_TAB_META);
    this.set('handleMessage', (messageType) => {
      assert.ok(false, `Should not get here with ${messageType}`);
    });
    await render(hbs`
      {{query-container/power-select-tabs
        activePillTab=activePillTab
        sendMessage=(action handleMessage)
      }}
    `);

    await click('.power-select-tabs .meta-tab');
  });
});