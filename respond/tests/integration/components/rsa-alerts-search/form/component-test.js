import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll, find, click } from '@ember/test-helpers';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';

module('rsa-alerts-search-form', 'Integration | Component | Alerts Search Form', function(hooks) {

  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });
  const selectedTimeFrameName = SINCE_WHEN_TYPES[1].name;

  test('it renders the form with correct default state based on its inputs', async function(assert) {
    const selectedEntityTypeName = 'IP';
    const inputText = '10.20.30.40';
    this.setProperties({
      selectedTimeFrameName,
      selectedEntityTypeName,
      inputText
    });

    await render(hbs`{{rsa-alerts-search/form
    selectedTimeFrameName=selectedTimeFrameName
    inputText=inputText
    selectedEntityTypeName=selectedEntityTypeName
    }}`);

    let element = findAll('.rsa-alerts-search-form');
    assert.equal(element.length, 1, 'Expected to find root DOM node');
    element = find('.rsa-alerts-search-form');
    assert.ok(element.querySelector('.ember-power-select-trigger').textContent.trim(), 'Expected time frame picker to display default');
    assert.ok(element.querySelectorAll('.rsa-alerts-search-form__text:enabled').length, 'Expected to find text input field enabled');
    assert.equal(element.querySelector('.rsa-alerts-search-form__text').value, inputText, 'Expected input text field to display default');
    assert.ok(element.querySelectorAll('.rsa-alerts-search-form__submit button:enabled').length, 'Expected submit button to be enabled');
  });

  test('it fires the onSubmit callback when the Search button is clicked', async function(assert) {
    assert.expect(1);
    this.setProperties({
      selectedTimeFrameName,
      selectedEntityTypeName: 'IP',
      inputText: '10.20.30.40',
      onSubmit() {
        assert.ok(true, 'onSubmit was invoked');
      }
    });
    await render(hbs`{{rsa-alerts-search/form
    selectedTimeFrameName=selectedTimeFrameName
    selectedEntityTypeName=selectedEntityTypeName
    isSearchUnderway=false
    inputText=inputText
    onSubmit=onSubmit
    }}`);

    await click(find('.rsa-alerts-search-form__submit button'));
  });

  test('it disables the onSubmit button when empty inputText is given', async function(assert) {
    assert.expect(1);
    this.setProperties({
      selectedTimeFrameName,
      selectedEntityTypeName: 'IP',
      inputText: '',
      onSubmit() {
        assert.ok(true, 'onSubmit was invoked');
      }
    });
    await render(hbs`{{rsa-alerts-search/form
    selectedTimeFrameName=selectedTimeFrameName
    selectedEntityTypeName=selectedEntityTypeName
    isSearchUnderway=false
    inputText=inputText
    onSubmit=onSubmit
    }}`);

    await click(find('.rsa-alerts-search-form__submit button'));
    assert.ok(true, 'Finished waiting for callback');
  });

  test('it disables all inputs and wires a Cancel button to a callback when a search is underway', async function(assert) {
    assert.expect(6);

    this.setProperties({
      selectedTimeFrameName,
      selectedEntityTypeName: 'IP',
      onCancel() {
        assert.ok(true, 'onCancel was invoked');
      }
    });

    await render(hbs`{{rsa-alerts-search/form
    selectedTimeFrameName=selectedTimeFrameName
    selectedEntityTypeName=selectedEntityTypeName
    isSearchUnderway=true
    onCancel=onCancel
    }}`);

    assert.notOk(findAll('.rsa-alerts-search-form__text:enabled').length, 'Expected not to find text input field enabled');
    assert.notOk(findAll('.rsa-alerts-search-form__submit button:enabled').length, 'Expected not to find submit button enabled');
    assert.notOk(findAll('.rsa-alerts-search-form__submit button:enabled').length, 'Expected not to find submit button enabled');
    assert.notOk(findAll('.rsa-alerts-search-form__device:enabled').length, 'Expected not to find device picker options enabled');
    const clickBtn = findAll('.rsa-alerts-search-form__cancel button:enabled');
    assert.ok(clickBtn.length, 'Expected to find cancel button enabled');
    await click(clickBtn[0]);
  });
});
