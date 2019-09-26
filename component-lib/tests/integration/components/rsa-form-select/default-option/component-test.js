import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | rsa-form-select/default-option', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders default option label', async function(assert) {
    const expectedOptionLabel = 'My Option Label';
    this.set('optionLabel', expectedOptionLabel);
    await render(hbs `{{rsa-form-select/default-option optionLabel=optionLabel}}`);
    assert.equal(this.element.innerText.trim(), expectedOptionLabel, `optionLabel is ${expectedOptionLabel}`);
  });

  test('it renders default option label when disabled=true but has no disabledTooltip', async function(assert) {
    const expectedOptionLabel = 'My Option Label';
    this.set('disabled', true);
    this.set('optionLabel', expectedOptionLabel);
    await render(hbs `{{rsa-form-select/default-option disabled=disabled optionLabel=optionLabel}}`);
    assert.equal(findAll('.tooltip-text').length, 0, 'NO rsa-text-with-tooltip');
    assert.equal(this.element.innerText.trim(), expectedOptionLabel, `optionLabel is ${expectedOptionLabel}`);
  });

  test('it renders default option label when disabled=false and has a disabledTooltip', async function(assert) {
    const expectedOptionLabel = 'My Option Label';
    this.set('disabled', false);
    this.set('disabledTooltip', 'My disabled tooltip!');
    this.set('optionLabel', expectedOptionLabel);
    await render(hbs `{{rsa-form-select/default-option disabled=disabled disabledTooltip=disabledTooltip optionLabel=optionLabel}}`);
    assert.equal(findAll('.tooltip-text').length, 0, 'NO rsa-text-with-tooltip');
    assert.equal(this.element.innerText.trim(), expectedOptionLabel, `optionLabel is ${expectedOptionLabel}`);
  });

  test('it renders option label with tooltip when disabled=true and has a disabledTooltip', async function(assert) {
    const expectedOptionLabel = 'My Option Label';
    const expectedDisabledTooltip = 'My disabled tooltip!';
    this.set('disabled', true);
    this.set('disabledTooltip', expectedDisabledTooltip);
    this.set('optionLabel', expectedOptionLabel);
    await render(hbs `{{rsa-form-select/default-option disabled=disabled disabledTooltip=disabledTooltip optionLabel=optionLabel}}`);
    assert.equal(findAll('.tooltip-text').length, 1, 'rendered with rsa-text-with-tooltip');
    assert.equal(find('.tooltip-text .disabled-option-in-tootlip').innerText.trim(), expectedOptionLabel, `optionLabel is ${expectedOptionLabel}`);
    // check tooltip
    await triggerEvent('.tooltip-text', 'mouseover');
    const actualDisabledTooltip = document.querySelectorAll('.tool-tip-value')[0].innerText.trim();
    assert.equal(actualDisabledTooltip, expectedDisabledTooltip, `disabled option tooltip is ${expectedDisabledTooltip}`);
  });

  test('disabled option tooltip works when rendered in the power-select', async function(assert) {
    const options = [
      { label: 'Option 1', disabled: false, disabledTooltip: '' },
      { label: 'Option 2', disabled: true, disabledTooltip: 'Option 2 Disabled Tooltip!' },
      { label: 'Option 3', disabled: false, disabledTooltip: '' }
    ];
    const [selectedOption] = options;

    this.set('options', options);
    this.set('selectedOption', selectedOption);
    this.set('handleChange', () => {});

    await render(hbs `
      {{#power-select
        options=options
        selected=selectedOption
        searchField='label'
        onchange=handleChange as |option|
      }}
        {{rsa-form-select/default-option
          disabled=option.disabled
          disabledTooltip=option.disabledTooltip
          optionLabel=option.label
        }}
      {{/power-select}}
    `);
    await click('.ember-power-select-trigger');
    const optionsAll = findAll('.ember-power-select-option');
    const optionsDisabled = findAll('.ember-power-select-option[aria-disabled=true]');
    assert.equal(optionsAll.length, 3, 'All options rendered');
    assert.equal(optionsDisabled.length, 1, '2 options enabled, and 1 option disabled');
    // the 2nd option should have a tooltip to show why it is disabled
    const expectedDisabledTooltip = options[1].disabledTooltip;
    await triggerEvent('.ember-power-select-option[aria-disabled=true] .tooltip-text', 'mouseover');
    const actualDisabledTooltip = document.querySelectorAll('.tool-tip-value')[0].innerText.trim();
    assert.equal(actualDisabledTooltip, expectedDisabledTooltip, `disabled option tooltip is '${expectedDisabledTooltip}' as expected`);
  });

});
