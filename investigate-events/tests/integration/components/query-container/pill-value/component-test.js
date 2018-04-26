import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import hbs from 'htmlbars-inline-precompile';
import { fillIn, render, settled, triggerKeyEvent } from '@ember/test-helpers';

const BACKSPACE_KEY = '8';
const ENTER_KEY = '13';
const ESCAPE_KEY = '27';
const LEFT_ARROW_KEY = '37';
const RIGHT_ARROW_KEY = '39';
const X_KEY = '88';

// const { log } = console;

module('Integration | Component | Pill Value', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it broadcasts a message when a Left Arrow key pressed', async function(assert) {
    assert.expect(1);
    this.set('handleMessage', (type) => {
      assert.equal(type, 'PILL::VALUE_ARROW_LEFT_KEY', 'Wrong message type');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent('.pill-value input', 'keyup', LEFT_ARROW_KEY);
    return settled();
  });

  test('it broadcasts a message when a Right Arrow key is pressed', async function(assert) {
    assert.expect(1);
    this.set('handleMessage', (type) => {
      assert.equal(type, 'PILL::VALUE_ARROW_RIGHT_KEY', 'Wrong message type');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent('.pill-value input', 'keyup', RIGHT_ARROW_KEY);
    return settled();
  });

  test('it broadcasts a message when a Backspace key is pressed', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (type, data) => {
      assert.equal(type, 'PILL::VALUE_BACKSPACE_KEY', 'Wrong message type');
      assert.equal(data, '', 'Wrong input string');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent('.pill-value input', 'keyup', BACKSPACE_KEY);
    return settled();
  });

  test('it does not broadcasts a message when an Enter key is pressed and there is no value', async function(assert) {
    assert.expect(0);
    this.set('handleMessage', () => {
      assert.ok(false, 'Should not have received PILL::VALUE_ENTER_KEY message');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent('.pill-value input', 'keyup', ENTER_KEY);
    return settled();
  });

  test('it broadcasts a message when an Enter key is pressed and there is a value', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (type, data) => {
      assert.equal(type, 'PILL::VALUE_ENTER_KEY', 'Wrong message type');
      assert.equal(data, undefined, 'Wrong input string');
    });
    await render(hbs`{{query-container/pill-value isActive=true valueString='x' sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent('.pill-value input', 'keyup', ENTER_KEY);
    return settled();
  });

  test('it broadcasts a message when an Escape is pressed', async function(assert) {
    assert.expect(1);
    this.set('handleMessage', (type) => {
      assert.equal(type, 'PILL::VALUE_ESCAPE_KEY', 'Wrong message type');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    await triggerKeyEvent('.pill-value input', 'keyup', ESCAPE_KEY);
    return settled();
  });

  test('it broadcasts a message when an "x" key is pressed', async function(assert) {
    assert.expect(2);
    this.set('handleMessage', (type, data) => {
      assert.equal(type, 'PILL::VALUE_SET', 'Wrong message type');
      assert.equal(data, 'x', 'Wrong input string');
    });
    await render(hbs`{{query-container/pill-value isActive=true sendMessage=(action handleMessage)}}`);
    // To work around a possible bug in @ember/test-helpers, I'll fill in the
    // input with an "x", then trigger the keyup event.
    await fillIn('.pill-value input', 'x');
    await triggerKeyEvent('.pill-value input', 'keyup', X_KEY);
    return settled();
  });
});