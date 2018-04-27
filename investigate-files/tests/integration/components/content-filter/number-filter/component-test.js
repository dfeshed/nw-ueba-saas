import QUnit from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { click, render, find, findAll, fillIn } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { patchSocket } from '../../../../helpers/patch-socket';

const { module, test } = QUnit;
const configValue = {
  'panelId': 'entropy',
  'propertyName': 'entropy',
  'label': 'investigateFiles.fields.entropy',
  'showRemoveButton': true,
  'selected': true,
  'expression': { propertyName: 'entropy', propertyValues: null }
};

module('content-filter/number-filter', 'Integration | Component | content filter/number filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Number filter test', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'entropy',
              'propertyValues': [{ value: 10 }],
              'restrictionType': 'GREATER_THAN'
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
    assert.expect(14);
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/number-filter config=config}}`);

    await click('.filter-trigger-button');

    assert.equal(findAll('.number-filter__content').length, 1, 'Number filter present');
    assert.equal(findAll('.number-filter__body > div').length, 2, 'Options and value field present');
    assert.equal(find('.number-filter__body .select-option').textContent.trim(), 'Select', 'Options place holder text is set to Select');
    assert.equal(find('.number-filter__body .number-filter__body__input').textContent.trim(), '', 'Value placeholder is empty');
    assert.equal(findAll('.number-filter__content .rsa-form-button').length, 1, 'Button present');
    assert.equal(find('.number-filter__content .rsa-form-button').textContent.trim(), 'Update', 'Update button present');

    clickTrigger();

    assert.equal(findAll('.filter-file-size ul li').length, 4, 'Number of filter options present is 4');
    assert.equal(find('.filter-file-size ul li:nth-child(1)').textContent.trim(), 'Equals', 'Option 1 present is Equals than');
    assert.equal(find('.filter-file-size ul li:nth-child(2)').textContent.trim(), 'Greater than', 'Option 2 present is Greater than');
    assert.equal(find('.filter-file-size ul li:nth-child(3)').textContent.trim(), 'Less than', 'Option 3 present is Less than');
    assert.equal(find('.filter-file-size ul li:nth-child(4)').textContent.trim(), 'Between', 'Option 4 present is Between');

    selectChoose('.number-filter__content', '.ember-power-select-option', 1);

    await fillIn('.number-filter__body__input input', '10');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(query, expectedQuery);
    });

    await click('.number-filter__content .rsa-form-button');
  });

  test('Number filter test Equals', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'entropy',
              'propertyValues': [{ value: 10 }],
              'restrictionType': 'EQUAL'
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
    assert.expect(3);
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/number-filter config=config}}`);

    await click('.filter-trigger-button');

    clickTrigger();

    selectChoose('.number-filter__content', '.ember-power-select-option', 0);

    await fillIn('.number-filter__body__input input', '10');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(query, expectedQuery);
    });

    await click('.number-filter__content .rsa-form-button');
  });

  test('Number filter test Less than', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'entropy',
              'propertyValues': [{ value: 10 }],
              'restrictionType': 'LESS_THAN'
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
    assert.expect(3);
    this.set('config', { ...configValue });
    await render(hbs`{{content-filter/number-filter config=config}}`);

    await click('.filter-trigger-button');

    clickTrigger();

    selectChoose('.number-filter__content', '.ember-power-select-option', 2);

    await fillIn('.number-filter__body__input input', '10');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(query, expectedQuery);
    });

    await click('.number-filter__content .rsa-form-button');
  });

  test('Number filter test Between', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'entropy',
              'propertyValues': [{ value: 10 }, { value: 20 }],
              'restrictionType': 'BETWEEN'
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
    QUnit.dump.maxDepth = 7;
    this.set('start', 10);
    this.set('end', 20);
    await render(hbs`{{content-filter/number-filter config=config start=start end=end}}`);

    await click('.filter-trigger-button');
    clickTrigger();

    selectChoose('.number-filter__content', '.ember-power-select-option', 3);
    assert.equal(findAll('.number-filter__body__input').length, 2, 'Between has 2 input fields');

    await fillIn('.number-filter__body__input.between_input_start input', '10');

    await fillIn('.number-filter__body__input.between_input_end input', '20');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(QUnit.dump.parse(query), QUnit.dump.parse(expectedQuery));
    });

    await click('.number-filter__content .rsa-form-button');
  });

  test('Filter label text updated for Between filter', async function(assert) {
    const configValue = {
      'panelId': 'entropy',
      'propertyName': 'entropy',
      'label': 'investigateFiles.fields.entropy',
      'showRemoveButton': true,
      'selected': true,
      'expression': {
        propertyName: 'entropy',
        propertyValues: [{ value: 10 }, { value: 20 }],
        restrictionType: 'BETWEEN'
      }
    };
    this.set('config', { ...configValue });
    assert.expect(1);
    await render(hbs`{{content-filter/number-filter config=config}}`);
    assert.equal(find('.filter-trigger-button span').textContent.trim(), 'Entropy: 10-20', 'Filter label text displayed according to filter value');
  });

  test('Additional radio buttons present when showMemUnit is true', async function(assert) {
    const expectedQuery = {
      'data': {
        'criteria': {
          'expressionList': [
            {
              'propertyName': 'size',
              'propertyValues': [{ value: 10737418240 }, { value: 21474836480 }],
              'restrictionType': 'BETWEEN'
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
    const configValue = {
      'panelId': 'size',
      'propertyName': 'size',
      'label': 'investigateFiles.fields.size',
      'showRemoveButton': true,
      'selected': true,
      'expression': {
        propertyName: 'size',
        propertyValues: [{ value: 10737418240 }, { value: 21474836480 }],
        restrictionType: 'BETWEEN'
      },
      'showMemUnit': true
    };
    this.set('config', configValue);
    assert.expect(10);
    QUnit.dump.maxDepth = 7;

    await render(hbs`{{content-filter/number-filter config=config selectedUnit=selectedUnit}}`);

    await click('.filter-trigger-button');

    assert.equal(document.querySelector('.number-filter__body__input.between_input_start input').value, '10.0', '10737418240 Bytes converted into 10 GB');
    assert.equal(document.querySelector('.number-filter__body__input.between_input_end input').value, '20.0', '21474836480 Bytes converted into 20 GB');
    assert.equal(findAll('.number-filter__content .rsa-form-radio-group .rsa-form-radio-label').length, 4, '4 radio buttons present');
    assert.equal(find('.number-filter__content .rsa-form-radio-label:nth-child(1)').textContent.trim(), 'bytes', 'Radio button 1, bytes');
    assert.equal(find('.number-filter__content .rsa-form-radio-label:nth-child(2)').textContent.trim(), 'KB', 'Radio button 2, KB');
    assert.equal(find('.number-filter__content .rsa-form-radio-label:nth-child(3)').textContent.trim(), 'MB', 'Radio button 3, MB');
    assert.equal(find('.number-filter__content .rsa-form-radio-label:nth-child(4)').textContent.trim(), 'GB', 'Radio button 4, GB');

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.equal(model, 'files');
      assert.deepEqual(QUnit.dump.parse(query), QUnit.dump.parse(expectedQuery));
    });

    await click('.number-filter__content .rsa-form-button');
  });
});