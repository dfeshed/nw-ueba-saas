import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, fillIn, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration - Component - rsa-date-time-input/input', function(hooks) {

  setupRenderingTest(hooks);

  const timestamp = 193885209000;
  const timezone = 'America/Los_Angeles';

  test('month input renders with expected value', async function(assert) {
    assert.expect(4);
    this.set('handleChange', (value) => {
      assert.equal(value, 0, 'The onChange action should be called with the expected value, which is zero (january)');
    });
    await render(hbs`{{rsa-date-time-input/input type="month" value=1 onChange=handleChange}}`);
    assert.equal(find('.date-time-input').classList.contains('month'), true);
    assert.equal(find('input').value, '02');
    await fillIn('input', '1');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '01');
  });

  test('changing the month to an empty value displays an empty value not 00 or any other value', async function(assert) {
    assert.expect(3);
    await render(hbs`{{rsa-date-time-input/input type="month" value=1}}`);
    assert.equal(find('.date-time-input').classList.contains('month'), true);
    assert.equal(find('input').value, '02');
    await fillIn('input', '');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '');
  });

  test('date/day input renders with expected value', async function(assert) {
    assert.expect(3);
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input/input type="date" value=22}}`);
    assert.equal(find('.date-time-input').classList.contains('date'), true);
    assert.equal(find('input').value, '22');
    await fillIn('input', '1');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '01');
  });

  test('year input renders with expected value', async function(assert) {
    assert.expect(4);
    this.set('handleChange', (value) => {
      assert.equal(value, 2002, 'The onChange action should be called with the expected value');
    });
    await render(hbs`{{rsa-date-time-input/input type="year" value=1976 onChange=handleChange}}`);
    assert.equal(find('.date-time-input').classList.contains('year'), true);
    assert.equal(find('.date-time-input input').value, '1976');
    await fillIn('input', '2');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '2002');
  });

  test('an empty year input renders with expected value', async function(assert) {
    assert.expect(4);
    this.set('handleChange', (value) => {
      assert.equal(value, null, 'The onChange action should be called with the expected value');
    });
    await render(hbs`{{rsa-date-time-input/input type="year" value=1976 onChange=handleChange}}`);
    assert.equal(find('.date-time-input').classList.contains('year'), true);
    assert.equal(find('.date-time-input input').value, '1976');
    await fillIn('input', '');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '');
  });

  test('hour input renders with expected value', async function(assert) {
    await render(hbs`{{rsa-date-time-input/input type="hour" value=17}}`);
    assert.equal(find('.date-time-input').classList.contains('hour'), true);
    assert.equal(find('.date-time-input input').value, '17');
    await fillIn('input', '2');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '02');
  });

  test('minute input renders with expected value', async function(assert) {
    await render(hbs`{{rsa-date-time-input/input type="minute" value=0}}`);
    assert.equal(find('.date-time-input').classList.contains('minute'), true);
    assert.equal(find('.date-time-input input').value, '00');
    await fillIn('input', '1');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '01');
  });

  test('second input renders with expected value', async function(assert) {
    await render(hbs`{{rsa-date-time-input/input type="second" value=9}}`);
    assert.equal(find('.date-time-input').classList.contains('second'), true);
    assert.equal(find('.date-time-input input').value, '09');
    await fillIn('input', '2');
    await triggerEvent('input', 'blur');
    assert.equal(find('input').value, '02');
  });
});