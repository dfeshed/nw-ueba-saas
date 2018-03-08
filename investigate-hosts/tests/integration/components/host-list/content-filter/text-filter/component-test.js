import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import $ from 'jquery';
import { patchSocket } from '../../../../../helpers/patch-socket';

import wait from 'ember-test-helpers/wait';

const filterConfig = {
  'propertyName': 'machine.agentVersion',
  'label': 'investigateHosts.hosts.column.machine.agentVersion',
  'filterControl': 'host-list/content-filter/text-filter',
  'panelId': 'agentVersion',
  'selected': true,
  'isDefault': true
};

moduleForComponent('host-list/content-filter/text-filter', 'Integration | Component | host list/content filter/text filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
  }
});

test('Text-filter button renders', function(assert) {
 // setting the configuration
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  const textFilterComponent = this.$().find('.text-filter');
  assert.equal(this.$(textFilterComponent).length, 1);
});

test('Text-filter click on the tirgger filter', function(assert) {
  // setting the configuration
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    assert.equal($('.text-filter__content').length, 1);
  });
});

test('Text-filter validating invalid text entered', function(assert) {
  // setting the configuration
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    $('.ember-text-field').val('').change();
    $('.rsa-form-button')[1].click();
    return wait().then(() => {
      const textIndex = $('.input-error').text().indexOf('Invalid');
      assert.notEqual(textIndex, -1, 'Update text filter with empty value validated');
    });
  });
});

test('Text-filter validating 257 characters text entered', function(assert) {
  // setting the configuration
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    const char257 = 'The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown' +
    'fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.' +
    'The quick brown fox jumps over the lazy dog';

    $('.ember-text-field').val(char257).change();
    $('.rsa-form-button')[1].click();
    return wait().then(() => {
      const textIndex = $('.input-error').text().indexOf('Filter input longer than 256 characters');
      assert.notEqual(textIndex, -1, 'Update text filter with 257 characters validated');
    });
  });
});

test('Text-filter validating invalid charecters text entered', function(assert) {
  // setting the configuration
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    const char257 = 'The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown' +
    'fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.' +
    'The quick brown ';

    $('.ember-text-field').val(char257).change();
    $('.rsa-form-button')[1].click();
    return wait().then(() => {
      const textIndex = $('.input-error').text().indexOf('Can contain alphanumeric or special characters');
      assert.notEqual(textIndex, -1, 'Update text filter with invalid chars validated');
    });
  });
});


test('Text-filter updating filter', function(assert) {

  assert.expect(3);

  const expression = {
    propertyName: 'machine.agentVersion',
    propertyValues: [{ value: 'C1C6F9' }],
    restrictionType: 'LIKE'
  };
  // setting the configuration
  this.set('config', { ...filterConfig, expression });
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');

  patchSocket((method, model, query) => {
    assert.equal(method, 'machines');
    assert.deepEqual(query.data.criteria.expressionList, [{
      propertyName: 'machine.agentVersion',
      propertyValues: [{ value: 'C1C6F9' }],
      restrictionType: 'LIKE'
    }]);
  });
  return wait().then(() => {
    $('.ember-text-field').val('C1C6F9').change();
    $('.footer button').trigger('click');
    return wait().then(() => {
      assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Agent Version: Contains C1C6F9', 'UpdatING text filter with value is working.');
    });
  });
});