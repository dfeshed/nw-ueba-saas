import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click, triggerKeyEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const timezone = 'America/New_York';

module('Integration - Component - rsa-date-time-input/am-pm', function(hooks) {

  setupRenderingTest(hooks);

  test('the component renders with pm displayed if the value is passed', async function(assert) {
    this.set('timezone', timezone);
    this.set('timestamp', 1464108661196);
    await render(hbs`
      {{rsa-date-time-input/am-pm
        timezone=timezone
        timestamp=timestamp
      }}
    `);
    assert.equal(find('input').value, 'pm');
  });

  test('the component renders with am displayed if the value is passed', async function(assert) {
    this.set('timezone', timezone);
    this.set('timestamp', 1464098421196);
    await render(hbs`
      {{rsa-date-time-input/am-pm
        timezone=timezone
        timestamp=timestamp
      }}
    `);
    assert.equal(find('input').value, 'am');
  });

  test('clicking on the input toggles the displayed value and triggers onChange', async function(assert) {
    this.set('timezone', timezone);
    this.set('timestamp', 1464098421196);
    this.set('handleChange', (amPm) => {
      assert.equal(amPm, 'pm');
    });
    await render(hbs`
      {{rsa-date-time-input/am-pm
        timezone=timezone
        timestamp=timestamp
      }}
    `);
    assert.equal(find('input').value, 'am');
    await click('input');
    assert.equal(find('input').value, 'pm');
  });

  test('if the input has focus, the up arrow, down arrow, and spacebar toggle the value', async function(assert) {
    const spacebarKeyCode = 32;
    const upArrowKeyCode = 38;
    const downArrowKeyCode = 40;
    const tabKeyCode = 9;
    const deleteKeyCode = 46;
    this.set('handleChange', (amPm) => {
      assert.equal(amPm, 'pm');
    });
    this.set('timezone', timezone);
    this.set('timestamp', 1464098421196);
    await render(hbs`
      {{rsa-date-time-input/am-pm
        timezone=timezone
        timestamp=timestamp
      }}
    `);
    await click('input');
    assert.equal(find('input').value, 'pm');
    await triggerKeyEvent('input', 'keyup', spacebarKeyCode);
    assert.equal(find('input').value, 'am', 'Spacebar toggles am-pm');
    await triggerKeyEvent('input', 'keyup', upArrowKeyCode);
    assert.equal(find('input').value, 'pm', 'Up arrow toggles am-pm');
    await triggerKeyEvent('input', 'keyup', downArrowKeyCode);
    assert.equal(find('input').value, 'am', 'Down arrow toggles am-pm');
    await triggerKeyEvent('input', 'keyup', tabKeyCode);
    assert.equal(find('input').value, 'am', 'Keys other than spacebar, up, down arrows should not toggle am-pm');
    await triggerKeyEvent('input', 'keyup', deleteKeyCode);
    assert.equal(find('input').value, 'am', 'Keys other than spacebar, up, down arrows should not toggle am-pm');
  });
});