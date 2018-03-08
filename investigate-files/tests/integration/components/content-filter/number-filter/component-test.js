import { moduleForComponent, test } from 'ember-qunit';
import QUnit from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { patchSocket } from '../../../../helpers/patch-socket';
import $ from 'jquery';

const configValue = {
  'panelId': 'entropy',
  'propertyName': 'entropy',
  'label': 'investigateFiles.fields.entropy',
  'showRemoveButton': true,
  'selected': true,
  'expression': { propertyName: 'entropy', propertyValues: null }
};

moduleForComponent('content-filter/number-filter', 'Integration | Component | content filter/number filter', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.set('config', configValue);
  }
});

test('Number filter test', function(assert) {
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

  this.render(hbs`{{content-filter/number-filter config=config}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options input:first').prop('checked', true).change();

    return wait().then(() => {
      assert.equal($('.number-filter__content').length, 1, 'Number filter present');
      assert.equal($('.number-filter__body').children().length, 2, 'Options and value field present');
      assert.equal($($('.number-filter__body').children()[0]).text().trim(), 'Select', 'Options place holder text is set to Select');
      assert.equal($($('.number-filter__body').children()[1]).text().trim(), '', 'Value placeholder is empty');
      assert.equal($('.number-filter__content .rsa-form-button').length, 1, 'Button present');
      assert.equal($('.number-filter__content .rsa-form-button').text().trim(), 'Update', 'Update button present');

      clickTrigger();

      return wait().then(() => {
        assert.equal($('.filter-file-size ul li').length, 4, 'Number of filter options present is 4');
        assert.equal($('.filter-file-size ul li:nth-child(1)').text().trim(), 'Equals', 'Option 1 present is Equals than');
        assert.equal($('.filter-file-size ul li:nth-child(2)').text().trim(), 'Greater than', 'Option 2 present is Greater than');
        assert.equal($('.filter-file-size ul li:nth-child(3)').text().trim(), 'Less than', 'Option 3 present is Less than');
        assert.equal($('.filter-file-size ul li:nth-child(4)').text().trim(), 'Between', 'Option 4 present is Between');

        selectChoose('.number-filter__content', '.ember-power-select-option', 1);
        $('.number-filter__body__input input').val('10').change();

        patchSocket((method, model, query) => {
          assert.equal(method, 'search');
          assert.equal(model, 'files');
          assert.deepEqual(query, expectedQuery);
        });

        $('.number-filter__content .rsa-form-button').click();
      });
    });
  });
});

test('Number filter test Equals', function(assert) {
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

  this.render(hbs`{{content-filter/number-filter config=config}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options input:first').prop('checked', true).change();

    return wait().then(() => {
      clickTrigger();

      return wait().then(() => {
        selectChoose('.number-filter__content', '.ember-power-select-option', 0);
        $('.number-filter__body__input input').val('10').change();

        patchSocket((method, model, query) => {
          assert.equal(method, 'search');
          assert.equal(model, 'files');
          assert.deepEqual(query, expectedQuery);
        });

        $('.number-filter__content .rsa-form-button').click();
      });
    });
  });
});

test('Number filter test Less than', function(assert) {
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

  this.render(hbs`{{content-filter/number-filter config=config}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options input:first').prop('checked', true).change();

    return wait().then(() => {
      clickTrigger();

      return wait().then(() => {
        selectChoose('.number-filter__content', '.ember-power-select-option', 2);
        $('.number-filter__body__input input').val('10').change();

        patchSocket((method, model, query) => {
          assert.equal(method, 'search');
          assert.equal(model, 'files');
          assert.deepEqual(query, expectedQuery);
        });

        $('.number-filter__content .rsa-form-button').click();
      });
    });
  });
});

test('Number filter test Between', function(assert) {
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
  QUnit.dump.maxDepth = 6;
  this.render(hbs`{{content-filter/number-filter config=config}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options input:first').prop('checked', true).change();

    return wait().then(() => {
      clickTrigger();

      return wait().then(() => {
        selectChoose('.number-filter__content', '.ember-power-select-option', 3);
        assert.equal($('.number-filter__body__input').length, 2, 'Between has 2 input fields');
        $('.number-filter__body__input:first input').val('10').change();
        $('.number-filter__body__input:last input').val('20').change();

        patchSocket((method, model, query) => {
          assert.equal(method, 'search');
          assert.equal(model, 'files');
          assert.deepEqual(query, expectedQuery);
        });

        $('.number-filter__content .rsa-form-button').click();
      });
    });
  });
});

test('Filter label text updated for Between filter', function(assert) {
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
  this.set('config', configValue);
  assert.expect(1);
  this.render(hbs`{{content-filter/number-filter config=config}}`);

  return wait().then(() => {
    assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Entropy: 10-20', 'Filter label text displayed according to filter value');
  });
});

test('Additional radio buttons present when showMemUnit is true', function(assert) {
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
      propertyValues: [{ value: 10 }, { value: 20 }],
      restrictionType: 'BETWEEN'
    },
    'showMemUnit': true
  };
  this.set('config', configValue);
  assert.expect(8);
  QUnit.dump.maxDepth = 7;

  this.render(hbs`{{content-filter/number-filter config=config}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    assert.equal($('.number-filter__content .rsa-form-radio-group .rsa-form-radio-label').length, 4, '4 radio buttons present');
    assert.equal($('.number-filter__content .rsa-form-radio-label:nth-child(1)').text().trim(), 'bytes', 'Radio button 1, bytes');
    assert.equal($('.number-filter__content .rsa-form-radio-label:nth-child(2)').text().trim(), 'KB', 'Radio button 2, KB');
    assert.equal($('.number-filter__content .rsa-form-radio-label:nth-child(3)').text().trim(), 'MB', 'Radio button 3, MB');
    assert.equal($('.number-filter__content .rsa-form-radio-label:nth-child(4)').text().trim(), 'GB', 'Radio button 4, GB');

    $('.number-filter__content .rsa-form-radio-label:last input').attr('checked', true).change();

    return wait().then(() => {
      patchSocket((method, model, query) => {
        assert.equal(method, 'search');
        assert.equal(model, 'files');
        assert.deepEqual(query, expectedQuery);
      });

      $('.number-filter__content .rsa-form-button').click();
    });
  });
});