import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, find, focus, triggerKeyEvent, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';


module('Integration | Component | range-slider', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    // this.actions = {};
    // this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    this.set('start', [25, 75]);
    this.set('steps', 1);
    await render(hbs `{{range-slider start=start step=steps}}`);
    assert.equal(findAll('.noUi-target').length, 1, 'Could not find the component root DOM element');
  });

  test('it includes proper classes', async function(assert) {
    this.set('start', [25, 75]);
    this.set('steps', 1);
    await render(hbs `{{range-slider start=start step=steps}}`);
    assert.equal(findAll('.noUi-base').length, 1, 'Testing to see if the .noUi-base class exists');
    assert.equal(findAll('.noUi-handle-lower').length, 1, 'Testing to see if the .noUi-handle-lower class exists');
    assert.equal(findAll('.noUi-handle-upper').length, 1, 'Testing to see if the .noUi-handle-upper class exists');
    assert.equal(findAll('.noUi-tooltip').length, 2, 'Testing to see if the .noUi-tooltip class exists for all handles');
  });

  test('it includes the proper classes when isReadOnly is true', async function(assert) {
    this.set('start', [25, 75]);
    this.set('steps', 1);
    /* There is no isReadOnly attr in the range-slider addon, whenever using isReadOnly, make sure to set
    disabled flag to true as well */
    this.set('isReadOnly', true);
    this.set('disabled', true);
    await render(hbs `{{range-slider start=start step=steps isReadOnly=isReadOnly disabled=disabled}}`);
    assert.equal(findAll('.is-read-only').length, 1, 'Testing to see if the .is-read-only class exists');
  });

  test('it includes the proper classes when disabled is true', async function(assert) {
    this.set('start', [25, 75]);
    this.set('steps', 1);
    this.set('disabled', true);
    await render(hbs `{{range-slider start=start step=steps disabled=disabled}}`);
    assert.equal(findAll('.is-disabled').length, 1, 'Testing to see if the .is-disabled class exists');
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    this.set('start', [25, 75]);
    this.set('steps', 1);
    this.set('isError', true);
    await render(hbs `{{range-slider start=start step=steps isError=isError}}`);
    assert.equal(findAll('.is-error').length, 1, 'Testing to see if the .is-error class exists');
  });

  test('it sets the handle positions correctly based on default values in the start array', async function(assert) {
    this.set('start', [25, 75]);
    this.set('steps', 1);
    await render(hbs `{{range-slider start=start step=steps}}`);
    assert.equal(find('.noUi-handle-lower').textContent.trim(), this.get('start')[0], 'Lower handle default value is incorrect');
    assert.equal(find('.noUi-handle-upper').textContent.trim(), this.get('start')[1], 'Upper handle default value is incorrect');
  });

  test('it renders the single handle version of the range-slider', async function(assert) {
    this.set('start', [25]);
    this.set('step', 1);
    this.set('connect', false);
    await render(hbs `{{range-slider start=start step=step connect=connect}}`);
    assert.equal(findAll('.noUi-handle').length, 1, 'there is only 1 handle');
    assert.equal(findAll('.noUi-handle-lower').length, 1, 'there is 1 .noUi-handle-lower handle');
    assert.equal(findAll('.noUi-handle-upper').length, 0, 'there are 0 .noUi-handle-upper handles');
  });

  test('the single handle version of the range-slider is keyboard accessible', async function(assert) {
    assert.expect(3);
    this.set('start', [25]);
    this.set('step', 1);
    this.set('connect', false);
    this.set('callCount', -1); // purely to track how many times onChange gets triggered
    this.set('onChange', (value) => {
      this.set('callCount', this.get('callCount') + 1);
      const callCount = this.get('callCount');
      switch (callCount) {
        case 0:
          assert.equal(value, 26, `onChange(${value}) was properly triggered by the keyboard`);
          break;
        case 1:
          assert.equal(value, 25, `onChange(${value}) was properly triggered by the keyboard`);
          break;
        case 2:
          assert.equal(value, 24, `onChange(${value}) was properly triggered by the keyboard`);
          break;
        default:
          // default should never run since we're triggering 3 arrow key events that should all match the above cases
          assert.equal(true, false, `onChange(${value}) should NOT have been triggered eh!`);
      }
    });
    await render(hbs `{{range-slider start=start step=step connect=connect on-change=(action onChange)}}`);
    const handleEl = find('.noUi-handle');
    await focus(handleEl);
    await triggerKeyEvent(handleEl, 'keydown', 39); // 39 is the right arrow key
    await triggerKeyEvent(handleEl, 'keydown', 37); // 37 is the left arrow key
    await triggerKeyEvent(handleEl, 'keydown', 37); // 37 is the left arrow key
  });
});
