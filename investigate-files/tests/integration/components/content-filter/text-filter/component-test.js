import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, find, findAll, fillIn } from '@ember/test-helpers';

import { patchSocket } from '../../../../helpers/patch-socket';

const configValue = {
  'propertyName': 'firstFileName',
  'label': 'investigateFiles.fields.firstFileName',
  'panelId': 'firstFileName',
  'selected': true
};

module('content-filter/text-filter', 'Integration | Component | content filter/text filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Text-filter button renders', async function(assert) {
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    await click('.filter-trigger-button');
    assert.equal(findAll('.text-filter').length, 1);
  });

  test('Text-filter click on the tirgger filter', async function(assert) {
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    await click('.filter-trigger-button');
    assert.equal(findAll('.text-filter__content').length, 1);
  });

  test('Text-filter validating invalid text entered', async function(assert) {
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    await click('.filter-trigger-button');

    document.querySelector('.ember-text-field').value = '';
    await click('.footer .rsa-form-button');
    const textIndex = find('.input-error').textContent.trim().indexOf('Invalid');
    assert.notEqual(textIndex, -1, 'Update text filter with empty value validated');
  });

  test('Text-filter validating 257 characters text entered', async function(assert) {
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    await click('.filter-trigger-button');
    const char257 = `The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown
    fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.
    The quick brown fox jumps over the lazy dog`;

    await fillIn('.ember-text-field', char257);
    await click(findAll('.rsa-form-button')[1]);
    const textIndex = find('.input-error').textContent.trim().indexOf('Filter input longer than 256 characters');
    assert.notEqual(textIndex, -1, 'Update text filter with 257 characters validated');
  });

  test('Text-filter validating invalid charecters text entered', async function(assert) {
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    await click('.filter-trigger-button');
    const fileName = 'file123❄@Name';

    await fillIn('.ember-text-field', fileName);
    await click(findAll('.rsa-form-button')[1]);
    const textIndex = find('.input-error').textContent.trim().indexOf('Can contain alphanumeric or special characters');
    assert.notEqual(textIndex, -1, 'Text filter can contain alphanumeric or special characters');
  });

  test('Text-filter request query test', async function(assert) {

    assert.expect(2);
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    await click('.filter-trigger-button');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.deepEqual(query.data.criteria.expressionList, [{
        propertyName: 'firstFileName',
        propertyValues: [{ value: 'app' }],
        restrictionType: 'LIKE'
      }]);
    });
    await fillIn('.ember-text-field', 'app');
    await click('.footer button');
  });

  test('Text-filter updating filter label', async function(assert) {

    assert.expect(1);

    const expression = {
      propertyName: 'firstFileName',
      propertyValues: [{ value: 'app' }],
      restrictionType: 'LIKE'
    };
    this.set('config', { ...configValue, expression });
    await render(hbs`{{content-filter/text-filter config=config}}`);
    assert.equal(find('.filter-trigger-button span').textContent.trim(), 'FileName: Contains app', 'Filter label text displayed according to filter value');
  });
});