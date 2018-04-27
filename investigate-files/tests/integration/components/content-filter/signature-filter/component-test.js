import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, find, findAll } from '@ember/test-helpers';
import { patchSocket } from '../../../../helpers/patch-socket';

const configValue = {
  'panelId': 'signature',
  'propertyName': 'signature.features',
  'showRemoveButton': true,
  'selected': true,
  'expression': { propertyName: 'signature.features', propertyValues: null },
  'options': ['signed', 'unsigned', 'valid', 'invalid', 'catalog', 'microsoft', 'apple']
};

module('content-filter/signature-filter', 'Integration | Component | content filter/signature filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Signature filter test for signed', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'signature.features',
              'propertyValues': [{ value: 'valid' }],
              'restrictionType': 'IN'
            }
          ],
          'predicateType': 'AND'
        },
        'pageNumber': 0,
        'pageSize': 100,
        'sort': [
          {
            'descending': true,
            'key': 'firstSeenTime'
          }
        ]
      }
    };

    assert.expect(13);
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/signature-filter config=config}}`);

    await click('.filter-trigger-button');

    assert.equal(findAll('.radio-filter__content').length, 1, 'Signature filter present');
    assert.equal(findAll('.radio-filter__content .body__radio .rsa-form-radio-label').length, 2, 'Two radio buttons present');
    assert.equal(find('.radio-filter__content .body__radio .rsa-form-radio-label:nth-of-type(1)').textContent.trim(), 'unsigned', 'First radio button option is unsigned');
    assert.equal(find('.radio-filter__content .body__radio .rsa-form-radio-label:nth-of-type(2)').textContent.trim(), 'signed', 'Second radio button option is signed');

    await click('.signed input');
    assert.equal(findAll('.list-filter__content li').length, 5, 'Number of signed options');
    assert.equal(find('.list-filter__content li:nth-child(1)').textContent.trim(), 'valid', 'valid Option');
    assert.equal(find('.list-filter__content li:nth-child(2)').textContent.trim(), 'invalid', 'invalid Option');
    assert.equal(find('.list-filter__content li:nth-child(3)').textContent.trim(), 'catalog', 'catalog Option');
    assert.equal(find('.list-filter__content li:nth-child(4)').textContent.trim(), 'microsoft', 'microsoft Option');
    assert.equal(find('.list-filter__content li:nth-child(5)').textContent.trim(), 'apple', 'apple Option');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(query, expectedQuery);
    });

    await click('.list-filter__content li:nth-child(1) input');
  });

  test('Signature filter test for unsigned', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'signature.features',
              'propertyValues': [{ value: 'unsigned' }],
              'restrictionType': 'IS_NULL'
            }
          ],
          'predicateType': 'AND'
        },
        'pageNumber': 0,
        'pageSize': 100,
        'sort': [
          {
            'descending': true,
            'key': 'firstSeenTime'
          }
        ]
      }
    };

    assert.expect(4);
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/signature-filter config=config}}`);

    await click('.filter-trigger-button');
    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(query, expectedQuery);
    });

    await click('.unsigned input');
    assert.equal(findAll('.list-filter__content li').length, 0, 'Signed options should not be available for unsigned');
  });

  test('Filter label text updated for signature filter', async function(assert) {
    const configValue = {
      'panelId': 'signature',
      'propertyName': 'signature.features',
      'label': 'investigateFiles.fields.signature.features',
      'showRemoveButton': true,
      'selected': true,
      'expression': {
        'propertyName': 'signature.features',
        'propertyValues': [{ value: 'unsigned' }],
        'restrictionType': 'IS_NULL'
      }
    };
    this.set('config', { ...configValue });
    assert.expect(1);
    await render(hbs`{{content-filter/signature-filter config=config}}`);
    assert.equal(find('.filter-trigger-button span').textContent.trim(), 'Signature: unsigned', 'Filter label text displayed according to filter value');
  });
});