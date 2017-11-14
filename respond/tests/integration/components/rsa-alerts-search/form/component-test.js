import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';

moduleForComponent('rsa-alerts-search-form', 'Integration | Component | Alerts Search Form', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const selectedTimeFrameName = SINCE_WHEN_TYPES[1].name;

test('it renders the form with correct default state based on its inputs', function(assert) {
  const selectedEntityTypeName = 'IP';
  const inputText = '10.20.30.40';
  this.setProperties({
    selectedTimeFrameName,
    selectedEntityTypeName,
    inputText
  });

  this.render(hbs`{{rsa-alerts-search/form 
    selectedTimeFrameName=selectedTimeFrameName
    inputText=inputText
    selectedEntityTypeName=selectedEntityTypeName
    }}`);

  return wait()
    .then(() => {
      const $el = this.$('.rsa-alerts-search-form');
      assert.equal($el.length, 1, 'Expected to find root DOM node');
      assert.ok($el.find('.ember-power-select-trigger').text().trim(), 'Expected time frame picker to display default');
      assert.ok($el.find('.rsa-alerts-search-form__text:enabled').length, 'Expected to find text input field enabled');
      assert.equal($el.find('.rsa-alerts-search-form__text').val(), inputText, 'Expected input text field to display default');
      assert.ok($el.find('.rsa-alerts-search-form__submit button:enabled').length, 'Expected submit button to be enabled');
    });
});

test('it fires the onSubmit callback when the Search button is clicked', function(assert) {
  assert.expect(1);
  this.setProperties({
    selectedTimeFrameName,
    selectedEntityTypeName: 'IP',
    inputText: '10.20.30.40',
    onSubmit() {
      assert.ok(true, 'onSubmit was invoked');
    }
  });
  this.render(hbs`{{rsa-alerts-search/form 
    selectedTimeFrameName=selectedTimeFrameName
    selectedEntityTypeName=selectedEntityTypeName
    isSearchUnderway=false
    inputText=inputText
    onSubmit=onSubmit
    }}`);

  return wait()
    .then(() => {
      this.$('.rsa-alerts-search-form__submit button').trigger('click');
      return wait();
    });
});

test('it disables the onSubmit button when empty inputText is given', function(assert) {
  assert.expect(1);
  const done = assert.async();
  this.setProperties({
    selectedTimeFrameName,
    selectedEntityTypeName: 'IP',
    inputText: '',
    onSubmit() {
      assert.ok(true, 'onSubmit was invoked');
    }
  });
  this.render(hbs`{{rsa-alerts-search/form 
    selectedTimeFrameName=selectedTimeFrameName
    selectedEntityTypeName=selectedEntityTypeName
    isSearchUnderway=false
    inputText=inputText
    onSubmit=onSubmit
    }}`);

  return wait()
    .then(() => {
      this.$('.rsa-alerts-search-form__submit button').trigger('click');
      return wait();
    })
    .then(() => {
      assert.ok(true, 'Finished waiting for callback');
      done();
    });
});

test('it disables all inputs and wires a Cancel button to a callback when a search is underway', function(assert) {
  assert.expect(6);

  this.setProperties({
    selectedTimeFrameName,
    selectedEntityTypeName: 'IP',
    onCancel() {
      assert.ok(true, 'onCancel was invoked');
    }
  });

  this.render(hbs`{{rsa-alerts-search/form 
    selectedTimeFrameName=selectedTimeFrameName
    selectedEntityTypeName=selectedEntityTypeName
    isSearchUnderway=true
    onCancel=onCancel
    }}`);

  return wait()
    .then(() => {
      assert.notOk(this.$('.rsa-alerts-search-form__text:enabled').length, 'Expected not to find text input field enabled');
      assert.notOk(this.$('.rsa-alerts-search-form__submit button:enabled').length, 'Expected not to find submit button enabled');
      assert.notOk(this.$('.rsa-alerts-search-form__submit button:enabled').length, 'Expected not to find submit button enabled');
      assert.notOk(this.$('.rsa-alerts-search-form__device:enabled').length, 'Expected not to find device picker options enabled');

      const $clickBtn = this.$('.rsa-alerts-search-form__cancel button:enabled');
      assert.ok($clickBtn.length, 'Expected to find cancel button enabled');

      $clickBtn.trigger('click');
      return wait();
    });
});
