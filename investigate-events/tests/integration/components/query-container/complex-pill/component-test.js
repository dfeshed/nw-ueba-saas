import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';
import { doubleClick } from '../pill-util';
import KEY_MAP from 'investigate-events/util/keys';

const ESCAPE_KEY = KEY_MAP.escape.code;
const DELETE_KEY = KEY_MAP.delete.code;
const BACKSPACE_KEY = KEY_MAP.backspace.code;


module('Integration | Component | complex-pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(find(PILL_SELECTORS.complexPill).textContent.trim(), 'FOOOOOOOO', 'text renders');
  });

  test('it sends a message when delete is clicked', async function(assert) {
    const done = assert.async();

    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });

    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETED, 'Message sent for pill delete is not correct');
      assert.deepEqual(
        data,
        { complexFilterText: 'FOOOOOOOO' },
        'Message sent for pill delete contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill delete contains correct pill position');
      done();
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.deletePill);
  });

  test('it renders as input when active/editable', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(find(PILL_SELECTORS.complexPillInput).value, 'FOOOOOOOO', 'input has value');
  });

  test('has proper class when active/editable', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.complexPillActive).length, 1, 'proper class present');
  });

  test('has proper class when pill is selected', async function(assert) {
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: true
    });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'proper class present');
  });

  test('sends message to be selected when clicked', async function(assert) {
    assert.expect(3);

    this.set('handleMessage', (messageType, pillData, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_SELECTED, 'Message sent for pill delete is not correct');
      assert.deepEqual(pillData,
        { complexFilterText: 'FOOOOOOOO', isSelected: false },
        'Message sent for pill create contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
    });

    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: false
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.complexPill);
  });

  test('sends message to be deselected when selected and clicked', async function(assert) {
    assert.expect(3);

    this.set('handleMessage', (messageType, pillData, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DESELECTED, 'Message sent for pill delete is not correct');
      assert.deepEqual(pillData,
        { complexFilterText: 'FOOOOOOOO', isSelected: true },
        'Message sent for pill create contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill create contains correct pill position');
    });

    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: true
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.complexPill);
  });

  test('if active click does not send a message', async function(assert) {
    assert.expect(0);

    this.set('handleMessage', () => {
      assert.ok(false, 'should not get in here');
    });

    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=true
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.complexPill);
  });

  test('double clicks sends appropriate event', async function(assert) {
    const done = assert.async();
    const pillData = { complexFilterText: 'FOOOOOOOO' };
    this.set('handleMessage', (messageType, pD, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, 'Should be opened for edit');
      assert.ok(pillData === pD, 'should send out pill data');
      assert.ok(position === 0, 'should send out pill data');
      done();
    });
    this.set('pillData', pillData);

    await render(hbs`
      {{query-container/complex-pill
        isActive=false
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    doubleClick(PILL_SELECTORS.complexPill, true);
  });

  test('when active focus is placed in input', async function(assert) {
    const pillData = { complexFilterText: 'FOOOOOOOO' };
    this.set('handleMessage', () => {});
    this.set('pillData', pillData);

    await render(hbs`
      {{query-container/complex-pill
        isActive=true
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll('input[autofocus]').length, 1, 'input has focus');
  });

  test('it broadcasts a message when the ESCAPE key is pressed', async function(assert) {
    assert.expect(3);
    const pillData = { complexFilterText: 'FOOOOOOOO' };
    this.set('pillData', pillData);
    this.set('handleMessage', (messageType, pD, position) => {
      assert.ok(messageType === MESSAGE_TYPES.PILL_EDIT_CANCELLED, 'Edit should be cancelled');
      assert.ok(pillData === pD, 'should send out pill data');
      assert.ok(position === 0, 'should send out position');
    });
    await render(hbs`
      {{query-container/complex-pill
        isActive=true
        position=0
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);
    await triggerKeyEvent(PILL_SELECTORS.complexPillInput, 'keydown', ESCAPE_KEY);
  });

  test('sends message up when selected and delete is pressed', async function(assert) {
    assert.expect(2);

    this.set('handleMessage', (messageType) => {
      assert.ok(messageType === MESSAGE_TYPES.DELETE_PRESSED_ON_SELECTED_PILL, 'should send out correct action');
    });
    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: true
    });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', DELETE_KEY);
  });

  test('sends message up when selected and backspace is pressed', async function(assert) {
    assert.expect(2);

    this.set('handleMessage', (messageType) => {
      assert.ok(messageType === MESSAGE_TYPES.DELETE_PRESSED_ON_SELECTED_PILL, 'should send out correct action');
    });
    this.set('pillData', {
      complexFilterText: 'FOOOOOOOO',
      isSelected: true
    });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.selectedPill).length, 1, 'proper class present');
    await triggerKeyEvent(PILL_SELECTORS.focusHolderInput, 'keydown', BACKSPACE_KEY);
  });
});