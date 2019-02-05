import { module, test } from 'qunit';
import { set } from '@ember/object';
import { later, run, scheduleOnce } from '@ember/runloop';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { clearRender, triggerKeyEvent, waitUntil, focus, blur, settled, click, find, findAll, fillIn, render } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { formModel, anotherModel } from './data';
import { selectors } from './selectors';
import formValidations from './validations';
import { formGroup, formGroupSync, fieldsetSync } from './helpers';
import { FormElement } from './shim';
import { Promise } from 'rsvp';

const ENTER_KEY = 13;
const PROPAGATE = 100;
const timeout = 10000;

let group, label, input, select, radioOne, labelOne, radioTwo, labelTwo, legend, inputGroupOne, inputGroupTwo, options, selectedItem, validation, groupValidation, inlineMessages, submitted, radioShapeOne, radioShapeTwo;

module('Integration | Component | Form Element', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(async function() {
    this.set('formSave', (value) => {
      submitted = value;
    });
    this.set('formModel', formModel);
    this.set('formValidations', formValidations);
    this.set('options', ['', 'x', 'y']);

    this.owner.register('component:forms/form-element', FormElement);

    await render(hbs`
      {{#forms/form-element formModel=formModel formSave=(action formSave) formValidations=formValidations as |form|}}
        {{#form.input property="foo.bar.baz" label="baz" as |input|}}
          {{input.component}}
        {{/form.input}}

        {{#form.select property="zip" label="zip" as |select|}}
          {{#select.component options=options as |unit|}}
            {{unit}}
          {{/select.component}}
        {{/form.select}}

        {{#form.input property="wat" label="wat" as |input|}}
          {{input.component}}
        {{/form.input}}

        {{#form.radio property="enabled" legend="legenndd" as |radio|}}
          {{radio.component value=true label="enabled"}}
          {{radio.component value=false label="disabled"}}
        {{/form.radio}}

        {{#form.input property="optional" label="optional" as |input|}}
          {{input.component}}
        {{/form.input}}

        {{form.save}}
        {{form.reset}}
      {{/forms/form-element}}
    `);
  });

  hooks.afterEach(async function() {
    submitted = undefined;
  });

  test('form element provided with sensible defaults', async function(assert) {
    const form = find(selectors.formElement);
    assert.equal(form.getAttribute('autocomplete'), 'off');
    assert.equal(form.getAttribute('autocorrect'), 'off');
    assert.equal(form.getAttribute('autocapitalize'), 'off');
    assert.equal(form.getAttribute('spellcheck'), 'false');
  });

  test('form element provides both input and select elements', async function(assert) {
    assert.expect(8);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), 'baz');
    assert.equal(input.value, 'y');

    ({ label, selectedItem, options } = await formGroup(2));
    assert.equal(label.textContent.trim(), 'zip');

    assert.equal(selectedItem.textContent.trim(), 'x');
    assert.equal(options.length, 3);
    assert.equal(options[0].textContent.trim(), '');
    assert.equal(options[1].textContent.trim(), 'x');
    assert.equal(options[2].textContent.trim(), 'y');
  });

  test('form element provides radio button element under fieldset', async function(assert) {
    assert.expect(11);

    ({ legend, labelOne, radioOne, radioShapeOne, labelTwo, radioTwo, radioShapeTwo } = fieldsetSync(1));
    assert.equal(legend.textContent.trim(), 'legenndd');
    assert.equal(labelOne.textContent.trim(), 'enabled');
    assert.equal(radioOne.value, 'true');
    assert.equal(radioOne.getAttribute('aria-checked'), null);
    assert.equal(radioShapeOne.getAttribute('focusable'), 'false');

    assert.equal(labelTwo.textContent.trim(), 'disabled');
    assert.equal(radioTwo.value, 'false');
    assert.equal(radioTwo.getAttribute('aria-checked'), '');
    assert.equal(radioShapeTwo.getAttribute('focusable'), 'false');

    await click(radioOne);

    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);
  });

  test('form provided save and reset buttons update when locale is changed', async function(assert) {
    assert.expect(4);

    const saveInGerman = 'sparen';
    const resetInGerman = 'Zrücksetzen';

    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'de-de', { 'forms.save': saveInGerman });
    run(i18n, 'addTranslations', 'de-de', { 'forms.reset': resetInGerman });

    assert.equal(find(selectors.saveButton).textContent.trim(), 'Save');
    assert.equal(find(selectors.resetButton).textContent.trim(), 'Reset');

    set(i18n, 'locale', 'de-de');

    return settled().then(async() => {
      assert.equal(find(selectors.saveButton).textContent.trim(), saveInGerman);
      assert.equal(find(selectors.resetButton).textContent.trim(), resetInGerman);
    });
  });

  test('reset button will revert the form back to original state', async function(assert) {
    assert.expect(8);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label, input } = await formGroup(1));
    await fillIn(input, 'w');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.resetButton);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), 'baz');
    assert.equal(input.value, 'y');
  });

  test('when input changes back to the original value reset and save buttons disable', async function(assert) {
    assert.expect(6);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label, input } = await formGroup(1));
    await fillIn(input, 'w');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    ({ label, input } = await formGroup(1));
    await fillIn(input, 'y');

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);
  });

  test('when select changes back to the original value reset and save buttons disable', async function(assert) {
    assert.expect(6);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    let { select } = await formGroup(2);
    await selectChoose(select, 'y');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    ({ select } = await formGroup(2));
    await selectChoose(select, 'x');

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);
  });

  test('when radio changes back to the original value reset and save buttons disable', async function(assert) {
    assert.expect(6);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ legend, labelOne, radioOne, labelTwo, radioTwo } = fieldsetSync(1));
    await click(radioOne);

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    ({ legend, labelOne, radioOne, labelTwo, radioTwo } = fieldsetSync(1));
    await click(radioTwo);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);
  });

  test('after input changed submit action includes changes without side effecting original formModel', async function(assert) {
    assert.expect(4);

    ({ label, input } = await formGroup(1));
    await fillIn(input, 'w');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.saveButton);

    await settled().then(() => {
      assert.deepEqual(submitted, {
        enabled: false,
        zip: 'x',
        wat: 'm',
        optional: 'xor',
        foo: {
          bar: {
            baz: 'w'
          }
        }
      });
      assert.deepEqual(formModel, {
        enabled: false,
        zip: 'x',
        wat: 'm',
        optional: 'xor',
        foo: {
          bar: {
            baz: 'y'
          }
        }
      });
    });
  });

  test('after select changed submit action includes changes without side effecting original formModel', async function(assert) {
    assert.expect(4);

    const { select } = await formGroup(2);
    await selectChoose(select, 'y');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.saveButton);

    await settled().then(() => {
      assert.deepEqual(submitted, {
        enabled: false,
        zip: 'y',
        wat: 'm',
        optional: 'xor',
        foo: {
          bar: {
            baz: 'y'
          }
        }
      });
      assert.deepEqual(formModel, {
        enabled: false,
        zip: 'x',
        wat: 'm',
        optional: 'xor',
        foo: {
          bar: {
            baz: 'y'
          }
        }
      });
    });
  });

  test('after radio changed submit action includes changes without side effecting original formModel', async function(assert) {
    assert.expect(4);

    ({ radioOne } = fieldsetSync(1));
    await click(radioOne);

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.saveButton);

    await settled().then(() => {
      assert.deepEqual(submitted, {
        enabled: true,
        zip: 'x',
        wat: 'm',
        optional: 'xor',
        foo: {
          bar: {
            baz: 'y'
          }
        }
      });
      assert.deepEqual(formModel, {
        enabled: false,
        zip: 'x',
        wat: 'm',
        optional: 'xor',
        foo: {
          bar: {
            baz: 'y'
          }
        }
      });
    });
  });

  test('when new model is pushed down the form is set to clean state with new values', async function(assert) {
    assert.expect(16);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label, input } = await formGroup(1));
    await fillIn(input, 'w');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    this.set('formModel', anotherModel);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), 'baz');
    assert.equal(input.value, 'b');

    ({ label, selectedItem } = await formGroup(2));
    assert.equal(label.textContent.trim(), 'zip');
    assert.equal(selectedItem.textContent.trim(), 'a');

    ({ label, input } = await formGroup(3));
    assert.equal(label.textContent.trim(), 'wat');
    assert.equal(input.value, 'u');

    ({ radioOne, radioTwo } = fieldsetSync(1));
    assert.equal(radioOne.value, 'true');
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(radioTwo.value, 'false');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);
  });

  test('form group will set label input and validation ids and aria attributes', async function(assert) {
    assert.expect(8);

    ({ group, label, input, groupValidation } = await formGroup(1));
    const inputGuid = group.getAttribute('id');
    assert.equal(label.getAttribute('for'), `${inputGuid}-input`);
    assert.equal(input.getAttribute('id'), `${inputGuid}-input`);
    assert.equal(input.getAttribute('aria-describedby'), `${inputGuid}-description`);
    assert.equal(groupValidation.getAttribute('id'), `${inputGuid}-description`);

    ({ group, label, select, groupValidation } = await formGroup(2));
    const selectGuid = group.getAttribute('id');
    assert.equal(label.getAttribute('for'), `${selectGuid}-input`);
    assert.equal(select.getAttribute('id'), `${selectGuid}-input`);
    assert.equal(select.getAttribute('aria-describedby'), `${selectGuid}-description`);
    assert.equal(groupValidation.getAttribute('id'), `${selectGuid}-description`);
  });

  test('fieldset with radio buttons will set label input and aria attributes', async function(assert) {
    assert.expect(7);

    ({ inputGroupOne, labelOne, radioOne, inputGroupTwo, labelTwo, radioTwo } = fieldsetSync(1));
    const radioOneGuid = inputGroupOne.getAttribute('id');
    assert.equal(labelOne.getAttribute('for'), `${radioOneGuid}-input`);
    assert.equal(radioOne.getAttribute('id'), `${radioOneGuid}-input`);
    assert.equal(radioOne.getAttribute('aria-describedby'), `${radioOneGuid}-description`);

    const radioTwoGuid = inputGroupTwo.getAttribute('id');
    assert.equal(labelTwo.getAttribute('for'), `${radioTwoGuid}-input`);
    assert.equal(radioTwo.getAttribute('id'), `${radioTwoGuid}-input`);
    assert.equal(radioTwo.getAttribute('aria-describedby'), `${radioTwoGuid}-description`);

    assert.notEqual(radioOneGuid, radioTwoGuid);
  });

  test('inline validation messages show up when form is invalid after input changed', async function(assert) {
    assert.expect(28);

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, 'y');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await blur(input);

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages && inlineMessages[0].textContent.trim() !== '';
    }, { timeout });

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);

    await focus(input);

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);

    await fillIn(input, 'w');

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages && inlineMessages[0].textContent.trim() === '';
    }, { timeout });

    assert.equal(input.value, 'w');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await blur(input);

    assert.equal(input.value, 'w');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages[0].textContent.trim() !== '';
    }, { timeout });

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);
  });

  test('inline validation messages show up when form is invalid after select changed', async function(assert) {
    assert.expect(10);

    ({ select, validation, selectedItem, inlineMessages } = await formGroup(2));

    assert.equal(selectedItem.textContent.trim(), 'x');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await selectChoose(select, '');
    await blur(select);

    await waitUntil(() => {
      ({ validation, inlineMessages } = formGroupSync(2));
      const inlineMatch = inlineMessages && inlineMessages[0].textContent.trim() !== '';
      const invalidMatch = validation && validation.classList.contains('is-invalid') === true;
      return inlineMatch && invalidMatch;
    }, { timeout });

    ({ validation, inlineMessages } = await formGroup(2));
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Zip can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);

    await selectChoose(select, 'y');

    await waitUntil(() => {
      ({ validation, inlineMessages } = formGroupSync(2));
      const inlineMatch = inlineMessages && inlineMessages[0].textContent.trim() === '';
      const invalidMatch = validation && validation.classList.contains('is-invalid') === false;
      return inlineMatch && invalidMatch;
    }, { timeout });

    ({ validation, inlineMessages } = await formGroup(2));
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);
  });

  test('changes to the formModel are still notified after reset button clicked', async function(assert) {
    assert.expect(16);

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, 'y');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');
    await blur(input);

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages && inlineMessages[0].textContent.trim() !== '';
    }, { timeout });

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);

    await click(selectors.resetButton);

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages && inlineMessages[0].textContent.trim() === '';
    }, { timeout });

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, 'y');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    ({ input, validation, inlineMessages } = await formGroup(1));

    await fillIn(input, '');
    await blur(input);

    await waitUntil(() => {
      ({ validation, inlineMessages } = formGroupSync(1));
      const inlineMatch = inlineMessages && inlineMessages[0].textContent.trim() !== '';
      const invalidMatch = validation && validation.classList.contains('is-invalid') === true;
      return inlineMatch && invalidMatch;
    }, { timeout });

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);
  });

  test('inline validation messages show up after user strikes enter to submit the form', async function(assert) {
    assert.expect(24);

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, 'y');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await triggerKeyEvent(input, 'keydown', ENTER_KEY);

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages && inlineMessages[0].textContent.trim() !== '';
    }, { timeout });

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);

    ({ input, validation, inlineMessages } = await formGroup(3));

    assert.equal(input.value, 'm');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await triggerKeyEvent(input, 'keydown', ENTER_KEY);

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(3));
      return inlineMessages && inlineMessages[0].textContent.trim() !== '';
    }, { timeout });

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Wat can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);
  });

  test('optional validation works for elements that depend on radio button selection', async function(assert) {
    assert.expect(22);

    ({ radioOne, radioTwo } = fieldsetSync(1));

    await click(radioOne);

    ({ input, validation, inlineMessages } = formGroupSync(4));

    assert.equal(input.value, 'xor');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    ({ input, validation, inlineMessages } = formGroupSync(4));

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await click(selectors.saveButton);

    await Promise.resolve();
    assert.deepEqual(submitted, undefined);

    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(4));
      return inlineMessages && inlineMessages[0].textContent.trim() !== '';
    }, { timeout });

    ({ input, validation, inlineMessages } = formGroupSync(4));

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Optional can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);

    await click(radioTwo);

    ({ input, validation, inlineMessages } = formGroupSync(4));

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Optional can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);
    // note: this^ UX is likely to evolve as more use cases are discovered

    await click(selectors.saveButton);

    await settled();

    assert.deepEqual(submitted, {
      enabled: false,
      zip: 'x',
      wat: 'm',
      optional: '',
      foo: {
        bar: {
          baz: 'y'
        }
      }
    });

    ({ input, validation, inlineMessages } = formGroupSync(4));

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);
  });

  test('inline validation not shown when reset is clicked before focusOut fires on behalf of form validation', async function(assert) {
    assert.expect(14);

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, 'y');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    // simulate blur event like a real user would experience
    // like clicking the reset button after typing into the input
    await new Promise((resolve) => {
      blur(input);
      later(() => {
        resolve();
      }, PROPAGATE - 10);
    });
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    // allow the blur enough time to propagate and trip the inline validation
    await waitUntil(() => {
      ({ inlineMessages } = formGroupSync(1));
      return inlineMessages && inlineMessages[0].textContent.trim() !== '';
    }, { timeout: (PROPAGATE / 10) });

    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), 'Foo.bar.baz can\'t be blank');
    assert.equal(validation.classList.contains('is-invalid'), true);
  });

  test('blur event will be runloop safe', async function(assert) {
    assert.expect(9);

    ({ input, validation, inlineMessages } = await formGroup(1));

    assert.equal(input.value, 'y');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    await fillIn(input, '');

    assert.equal(input.value, '');
    assert.equal(inlineMessages.length, 1);
    assert.equal(inlineMessages[0].textContent.trim(), '');
    assert.equal(validation.classList.contains('is-invalid'), false);

    // will destory the component but not until blur has fired
    scheduleOnce('render', this, async function() {
      clearRender();
    });

    // will run just before the clearRender is invoked
    scheduleOnce('actions', this, async function() {
      await new Promise((resolve) => {
        blur(input);
        later(() => {
          resolve();
        }, PROPAGATE - 10);
      });
    });

    await waitUntil(() => {
      return findAll(selectors.resetButton).length === 0;
    }, { timeout });

    assert.equal(findAll(selectors.resetButton).length, 0);
  });
});
