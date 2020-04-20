import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, fillIn, click, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-editable-text', function(hooks) {
  setupRenderingTest(hooks);
  test('it renders and toggles edit mode', async function(assert) {
    const value = 'value';
    const persistChanges = () => {
      assert.ok(true);
    };

    this.set('value', value);
    this.set('persistChanges', persistChanges);

    await render(hbs `{{rsa-editable-text value=value persistChanges=persistChanges}}`);

    const el = find('.rsa-editable-text');

    assert.ok(el, '1');
    assert.equal(el.textContent.trim(), value);
    assert.ok(find('.rsa-editable-text .edit'), '2');
    assert.ok(el.textContent.includes('value'), '3');

    assert.notOk(el.className.includes('edit-mode'), '4');
    assert.notOk(find('.rsa-editable-text .save'), '5');
    assert.notOk(find('.rsa-editable-text .cancel'), '6');
    assert.notOk(find('.rsa-editable-text input'), '7');

    await click('.rsa-editable-text .edit');

    assert.ok(el.className.includes('edit-mode'), '8');
    assert.ok(find('.rsa-editable-text .save'), '9');
    assert.ok(find('.rsa-editable-text .cancel'), '10');
    assert.ok(find('.rsa-editable-text input'), '11');
    assert.equal(find('.rsa-editable-text input').value, value, '12');
  });

  test('it saves via icon icon', async function(assert) {
    assert.expect(4);

    const value = 'value';
    const persistChanges = () => {
      assert.ok(true);
    };

    this.set('value', value);
    this.set('persistChanges', persistChanges);

    await render(hbs `{{rsa-editable-text value=value persistChanges=persistChanges}}`);

    const el = find('.rsa-editable-text');
    const edit = find('.rsa-editable-text .edit');

    // console.log('click edit');
    await click(edit);
    await fillIn('.rsa-editable-text input', 'foo');
    assert.equal(find('.rsa-editable-text input').value, 'foo');

    // console.log('click save');
    await click('.rsa-editable-text .save');
    assert.equal(el.textContent.trim(), 'foo');
    assert.notOk(el.className.includes('edit-mode'));
  });

  test('it saves via enter key', async function(assert) {
    assert.expect(4);

    const value = 'value';
    const persistChanges = () => {
      assert.ok(true);
    };

    this.set('value', value);
    this.set('persistChanges', persistChanges);

    await render(hbs `{{rsa-editable-text value=value persistChanges=persistChanges}}`);

    const el = find('.rsa-editable-text');
    const edit = find('.rsa-editable-text .edit');

    await click(edit);
    await fillIn('.rsa-editable-text input', 'foo');
    assert.equal(find('.rsa-editable-text input').value, 'foo');

    await triggerEvent('.rsa-editable-text form', 'submit');
    assert.equal(el.textContent.trim(), 'foo');
    assert.notOk(el.className.includes('edit-mode'));
  });

  test('it cancels via icon click', async function(assert) {
    assert.expect(3);

    const value = 'value';
    const persistChanges = () => {
      assert.ok(false);
    };

    this.set('value', value);
    this.set('persistChanges', persistChanges);

    await render(hbs `{{rsa-editable-text value=value persistChanges=persistChanges}}`);

    const el = find('.rsa-editable-text');
    const edit = find('.rsa-editable-text .edit');

    await click(edit);
    await fillIn('.rsa-editable-text input', 'foo');
    assert.equal(find('.rsa-editable-text input').value, 'foo');

    await find('.rsa-editable-text .cancel').click();
    assert.equal(el.textContent.trim(), value);
    assert.notOk(el.className.includes('edit-mode'));
  });

  test('it cancels via esc key', async function(assert) {
    assert.expect(3);

    const value = 'value';
    const persistChanges = () => {
      assert.ok(false);
    };

    this.set('value', value);
    this.set('persistChanges', persistChanges);

    await render(hbs `{{rsa-editable-text value=value persistChanges=persistChanges}}`);

    const el = find('.rsa-editable-text');
    const edit = find('.rsa-editable-text .edit');

    await click(edit);
    await fillIn('.rsa-editable-text input', 'foo');
    assert.equal(find('.rsa-editable-text input').value, 'foo');

    await triggerKeyEvent('.rsa-editable-text input', 'keyup', 'Escape');

    assert.equal(el.textContent.trim(), value);
    assert.notOk(el.className.includes('edit-mode'));
  });

});
