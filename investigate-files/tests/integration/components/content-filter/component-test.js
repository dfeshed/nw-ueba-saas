import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import files from '../../state/files';
import wait from 'ember-test-helpers/wait';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { patchSocket } from '../../../helpers/patch-socket';
import $ from 'jquery';

let setState;

moduleForComponent('content-filter', 'Integration | Component | content filter', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      applyPatch(state);
    };
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
    revertPatch();
  }
});

test('content-filter renders', function(assert) {
  const { files: { schema: { schema }, filter: { expressionList } } } = files;

  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).build();

  this.render(hbs`{{content-filter}}`);

  assert.equal(this.$('.filter-search-icon').text().trim(), 'Filter:', 'Filter:, text present');
  assert.equal(this.$('.text-filter').length, 1, 'Text filter added');

  assert.equal(this.$('.filter-trigger-button').length, 2, 'Add filter button present');
  assert.equal(this.$('.filter-trigger-button:last').text().trim(), 'Add Filter', 'Add filter button text present');

  assert.equal(this.$('.save-button').length, 1, 'Filter save button present');
  assert.equal(this.$('.save-button').text().trim(), 'Save', 'Filter save button present');

  assert.equal(this.$('.save-button + .rsa-form-button-wrapper button').text().trim(), 'Reset', 'Filter reset button present');

  this.$('.filter-trigger-button:last').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('.ember-tether-enabled').length, 1, 'Column chooser dropdown enabled');
    assert.equal($('.column-chooser-input').length, 1, 'Column chooser dropdown present');
    assert.equal($('.column-chooser-input input').attr('placeholder').trim(), 'Type to filter the list', 'Type to filter the list, Place holder text present in column chooser');
    assert.equal($('.filter-options input').length, 8, 'Number of filters present in the dropdown is 8');
    assert.equal($('.filter-options input:checked').length, 1, 'Number of filters checked in the dropdown is 1');

    assert.equal($('.files-content-filter > div').length, 6, 'Number of buttons present before a new filter has been added.');
    $('.filter-options input:first').prop('checked', true).change();

    return wait().then(() => {
      assert.equal($('.files-content-filter > div').length, 7, 'Count of buttons increased after New filter has been added.');
    });
  });
});

test('Save filter functionality', function(assert) {
  const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.save-button button').trigger('click');

  return wait().then(() => {
    assert.notEqual($('#modalDestination').children().length, 0, 'Modal popup present');
    assert.equal($('#modalDestination .modal-content').length, 1, 'modal-content present');
    assert.equal($('#modalDestination .rsa-form-label:first').text().trim(),
    'Provide a name to the search to be saved. This name will appear in the search box list.',
    'modal-content label provided');
    assert.equal($('#modalDestination .rsa-form-label:last').text().trim(), 'Name *', 'modal-content label provided');
    assert.equal($('#modalDestination .rsa-form-label:last + input').attr('maxLength'), 255, 'Length limit on input field');

    assert.equal($('#modalDestination .name + label').length, 0, 'Name field is empty. text not present');

    assert.equal($('#modalDestination .rsa-btn-group button').length, 2, 'Two buttons present');
    assert.equal($('#modalDestination .rsa-btn-group button:first').text().trim(), 'Save', 'Save button present');
    assert.equal($('#modalDestination .rsa-btn-group button:last').text().trim(), 'Cancel', 'Cancel button present');

    this.$('.save-filter').trigger('click');
    this.$('.save-filter').trigger('click');
    return wait().then(() => {
      assert.equal($('#modalDestination .name + label').text().trim(), 'Name field is empty.', 'Name field is empty. Error text present');
    });
  });
});

test('Cancel filter functionality', function(assert) {
  const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.save-button button').trigger('click');

  return wait().then(() => {
    this.$('.cancel-filter').trigger('click');
    return wait().then(() => {
      assert.equal($('#modalDestination').children().length, 0, 'Save popup has been closed');
    });
  });

});

skip('Save filter functionality', function(assert) {
  const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.save-button button').trigger('click');

  return wait().then(() => {
    $('#modalDestination .rsa-form-label:last + input').val('TestFilter');
    this.set('saveFilterName', 'TestFilter');
    return wait().then(() => {
      this.$('.save-filter button').trigger('click');
      return wait().then(() => {
        assert.equal($('#modalDestination').children().length, 0, 'Save popup has been closed');
      });
    });
  });
});

test('Reset filter functionality', function(assert) {
  const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

  this.render(hbs`{{content-filter}}`);

  assert.equal($('.files-content-filter > div').length, 6, 'Number of buttons present before filter reset.');
  this.$('.save-button + .rsa-form-button-wrapper button').trigger('click');

  return wait().then(() => {
    assert.equal($('.files-content-filter > div').length, 5, 'Number of buttons present after filter reset.');
  });
});

/* Number Filter */
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
  const { files: { schema: { schema } } } = files;

  new ReduxDataHelper(setState).schema(schema).expressionList([]).build();

  this.render(hbs`{{content-filter}}`);

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

/* Signature Filter */

test('Signature filter test for signed', function(assert) {

  const expectedQuery = {
    'data': {
      'criteria': {
        'expressionList': [
          {
            'propertyName': 'signature.features',
            'propertyValues': [{ value: 'valid' }],
            'restrictionType': 'IN'
          },
          {
            'propertyName': 'firstFileName',
            'propertyValues': [{ value: 'm' }],
            'restrictionType': 'LIKE'
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
  const { files: { schema: { schema }, filter: { expressionList } } } = files;

  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options li:nth-child(7) input').prop('checked', true).change();

    return wait().then(() => {
      assert.equal($('.radio-filter__content').length, 1, 'Number filter present');
      assert.equal($('.radio-filter__content .body__radio').children().length, 2, 'Options and value field present');
      assert.equal($($('.radio-filter__content .body__radio').children()[0]).text().trim(), 'unsigned', 'Options place holder text is set to Select');
      assert.equal($($('.radio-filter__content .body__radio').children()[1]).text().trim(), 'signed', 'Value placeholder is empty');

      $('.radio-filter__content .body__radio label:last input').attr('checked', true).change();

      return wait().then(() => {
        assert.equal($('.list-filter__content li').length, 5, 'Number of signed options');
        assert.equal($('.list-filter__content li:nth-child(1)').text().trim(), 'valid', 'valid Option');
        assert.equal($('.list-filter__content li:nth-child(2)').text().trim(), 'invalid', 'invalid Option');
        assert.equal($('.list-filter__content li:nth-child(3)').text().trim(), 'catalog', 'catalog Option');
        assert.equal($('.list-filter__content li:nth-child(4)').text().trim(), 'microsoft', 'microsoft Option');
        assert.equal($('.list-filter__content li:nth-child(5)').text().trim(), 'apple', 'apple Option');

        patchSocket((method, model, query) => {
          assert.equal(method, 'search');
          assert.equal(model, 'files');
          assert.deepEqual(query, expectedQuery);
        });

        $('.list-filter__content li:nth-child(1) input').prop('checked', true).change();

      });
    });
  });
});

test('Signature filter test for unsigned', function(assert) {

  const expectedQuery = {
    'data': {
      'criteria': {
        'expressionList': [
          {
            'propertyName': 'signature.features',
            'propertyValues': [{ value: 'unsigned' }],
            'restrictionType': 'IS_NULL'
          },
          {
            'propertyName': 'firstFileName',
            'propertyValues': [{ value: 'm' }],
            'restrictionType': 'LIKE'
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
  const { files: { schema: { schema }, filter: { expressionList } } } = files;

  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options li:nth-child(7) input').prop('checked', true).change();

    return wait().then(() => {
      patchSocket((method, model, query) => {
        assert.equal(method, 'search');
        assert.equal(model, 'files');
        assert.deepEqual(query, expectedQuery);
      });

      $('.radio-filter__content .body__radio label:first input').attr('checked', true).change();
    });
  });
});

/* List Filter */

test('List filter test', function(assert) {

  const expectedQuery = {
    'data': {
      'criteria': {
        'expressionList': [
          {
            'propertyName': 'format',
            'propertyValues': [{ value: 'unknown' }],
            'restrictionType': 'IN'
          },
          {
            'propertyName': 'firstFileName',
            'propertyValues': [{ value: 'm' }],
            'restrictionType': 'LIKE'
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

  assert.expect(10);
  const { files: { schema: { schema }, filter: { expressionList } } } = files;

  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options li:nth-child(3) input').prop('checked', true).change();

    return wait().then(() => {
      assert.equal($('.list-filter__content').length, 1, 'Number filter present');
      assert.equal($('.list-filter__content .list-filter-lists').children().length, 5, 'Options and value field present');
      assert.equal($($('.list-filter__content .list-filter-lists').children()[0]).text().trim(), 'pe', 'File format pe');
      assert.equal($($('.list-filter__content .list-filter-lists').children()[1]).text().trim(), 'elf', 'File format elf');
      assert.equal($($('.list-filter__content .list-filter-lists').children()[2]).text().trim(), 'macho', 'File format macho');
      assert.equal($($('.list-filter__content .list-filter-lists').children()[3]).text().trim(), 'script', 'File format script');
      assert.equal($($('.list-filter__content .list-filter-lists').children()[4]).text().trim(), 'unknown', 'File format unknown');

      patchSocket((method, model, query) => {
        assert.equal(method, 'search');
        assert.equal(model, 'files');
        assert.deepEqual(query, expectedQuery);
      });

      $('.list-filter__content .list-filter-lists li:last input').attr('checked', true).change();
    });
  });
});

test('List filter test, additional item selected', function(assert) {

  const expectedQueryOne = {
    'data': {
      'criteria': {
        'expressionList': [
          {
            'propertyName': 'format',
            'propertyValues': [{ value: 'unknown' }],
            'restrictionType': 'IN'
          },
          {
            'propertyName': 'firstFileName',
            'propertyValues': [{ value: 'm' }],
            'restrictionType': 'LIKE'
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

  const expectedQueryTwo = {
    'data': {
      'criteria': {
        'expressionList': [
          {
            'propertyName': 'format',
            'propertyValues': [{ value: 'pe' },
                               { value: 'unknown' }],
            'restrictionType': 'IN'
          },
          {
            'propertyName': 'firstFileName',
            'propertyValues': [{ value: 'm' }],
            'restrictionType': 'LIKE'
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

  assert.expect(6);
  const { files: { schema: { schema }, filter: { expressionList } } } = files;

  new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).build();

  this.render(hbs`{{content-filter}}`);

  this.$('.filter-trigger-button:last').trigger('click');

  return wait().then(() => {
    $('.filter-options li:nth-child(3) input').prop('checked', true).change();

    return wait().then(() => {

      patchSocket((method, model, query) => {
        assert.equal(method, 'search');
        assert.equal(model, 'files');
        assert.deepEqual(query, expectedQueryOne);
      });

      $('.list-filter__content .list-filter-lists li:last input').attr('checked', true).change();

      return wait().then(() => {

        $('.files-content-filter > div:nth-child(3) button').click();

        return wait().then(() => {
          patchSocket((method, model, query) => {
            assert.equal(method, 'search');
            assert.equal(model, 'files');
            assert.deepEqual(query, expectedQueryTwo);
          });

          $('.list-filter__content .list-filter-lists li:first input').attr('checked', true).change();
        });
      });
    });
  });
});