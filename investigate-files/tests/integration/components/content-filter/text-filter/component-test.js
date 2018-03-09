import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import $ from 'jquery';
import { patchSocket } from '../../../../helpers/patch-socket';
import wait from 'ember-test-helpers/wait';

const configValue = {
  'propertyName': 'firstFileName',
  'label': 'investigateFiles.fields.firstFileName',
  'panelId': 'firstFileName',
  'selected': true
};

moduleForComponent('content-filter/text-filter', 'Integration | Component | content filter/text filter', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.set('config', configValue);
  }
});

test('Text-filter button renders', function(assert) {
  this.render(hbs`{{content-filter/text-filter config=config}}`);
  const textFilterComponent = this.$().find('.text-filter');
  assert.equal(this.$(textFilterComponent).length, 1);
});

test('Text-filter click on the tirgger filter', function(assert) {
  this.render(hbs`{{content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    assert.equal($('.text-filter__content').length, 1);
  });
});

test('Text-filter validating invalid text entered', function(assert) {
  this.render(hbs`{{content-filter/text-filter config=config}}`);
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
  this.render(hbs`{{content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    const char257 = `The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown
    fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.
    The quick brown fox jumps over the lazy dog`;

    $('.ember-text-field').val(char257).change();
    $('.rsa-form-button')[1].click();
    return wait().then(() => {
      const textIndex = $('.input-error').text().indexOf('Filter input longer than 256 characters');
      assert.notEqual(textIndex, -1, 'Update text filter with 257 characters validated');
    });
  });
});

test('Text-filter validating invalid charecters text entered', function(assert) {
  this.render(hbs`{{content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    const fileName = 'file123❄@Name';

    $('.ember-text-field').val(fileName).change();
    $('.rsa-form-button')[1].click();
    return wait().then(() => {
      const textIndex = $('.input-error').text().indexOf('Can contain alphanumeric or special characters');
      assert.notEqual(textIndex, -1, 'Text filter can contain alphanumeric or special characters');
    });
  });
});

test('Text-filter request query test', function(assert) {

  assert.expect(2);

  this.render(hbs`{{content-filter/text-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');

  patchSocket((method, model, query) => {
    assert.equal(method, 'search');
    assert.deepEqual(query.data.criteria.expressionList, [{
      propertyName: 'firstFileName',
      propertyValues: [{ value: 'app' }],
      restrictionType: 'LIKE'
    }]);
  });
  return wait().then(() => {
    $('.ember-text-field').val('app').change();
    $('.footer button').trigger('click');
  });
});

test('Text-filter updating filter label', function(assert) {

  assert.expect(1);

  const expression = {
    propertyName: 'firstFileName',
    propertyValues: [{ value: 'app' }],
    restrictionType: 'LIKE'
  };

  this.set('config', { ...configValue, expression });
  this.render(hbs`{{content-filter/text-filter config=config}}`);
  return wait().then(() => {
    assert.equal(this.$('.filter-trigger-button span').text().trim(), 'FileName: Contains app', 'Filter label text displayed according to filter value');
  });
});