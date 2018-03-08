import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { patchSocket } from '../../../../helpers/patch-socket';
import $ from 'jquery';

const configValue = {
  'panelId': 'signature',
  'propertyName': 'signature.features',
  'showRemoveButton': true,
  'selected': true,
  'expression': { propertyName: 'signature.features', propertyValues: null },
  'options': ['signed', 'unsigned', 'valid', 'invalid', 'catalog', 'microsoft', 'apple']
};

moduleForComponent('content-filter/signature-filter', 'Integration | Component | content filter/signature filter', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.set('config', configValue);
  }
});

test('Signature filter test for signed', function(assert) {
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
  this.render(hbs`{{content-filter/signature-filter config=config}}`);

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

  this.render(hbs`{{content-filter/signature-filter config=config}}`);

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
      assert.equal($('.list-filter__content li').length, 0, 'Signed options should not be available for unsigned');
    });
  });
});

test('Filter label text updated for signature filter', function(assert) {
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
  this.set('config', configValue);
  assert.expect(1);
  this.render(hbs`{{content-filter/signature-filter config=config}}`);

  return wait().then(() => {
    assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Signature: unsigned', 'Filter label text displayed according to filter value');
  });
});