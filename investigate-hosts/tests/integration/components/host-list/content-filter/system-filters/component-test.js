import { moduleForComponent, test } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';

moduleForComponent('host-list/content-filter/system-filters', 'Integration | Component | System Filters', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    const initState = Immutable.from({
      endpoint: {
        filter: {
          filters: [
            {
              'createdBy': 'admin',
              'createdOn': 1495517131585,
              'lastModifiedBy': 'admin',
              'lastModifiedOn': 1495517131585,
              'id': '5923c7cbd8d4ae128db98c98',
              'name': 'JAZZ_NWE_5_AGENTS',
              'filterType': 'MACHINE',
              'criteria': {
                'criteriaList': [],
                'expressionList': [
                  {
                    'propertyName': 'machine.agentVersion',
                    'restrictionType': 'IN',
                    'propertyValues': [
                      {
                        'value': '5.0.0.0'
                      }
                    ]
                  }
                ],
                'predicateType': 'AND'
              },
              'systemFilter': false
            },
            {
              'createdBy': 'admin',
              'createdOn': 1495517131585,
              'lastModifiedBy': 'admin',
              'lastModifiedOn': 1495517131585,
              'id': '5923c7cbd8d4ae128db98c99',
              'name': 'JAZZ_NWE_5_AGENTS_10',
              'filterType': 'MACHINE',
              'criteria': {
                'criteriaList': [],
                'expressionList': [
                  {
                    'propertyName': 'machine.agentVersion',
                    'restrictionType': 'IN',
                    'propertyValues': [
                      {
                        'value': '5.0.0.0'
                      }
                    ]
                  }
                ],
                'predicateType': 'AND'
              },
              'systemFilter': false
            }
          ]
        }
      }
    });
    applyPatch(initState);
    this.inject.service('redux');
  },
  afterEach() {
    revertPatch();
  }
});

test('should list of all saved search', function(assert) {
  this.render(hbs`{{host-list/content-filter/system-filters}}`);
  return wait().then(() => {
    assert.equal(this.$('.filter-list').length, 1);
    assert.equal(this.$('.filter-list__item').length, 2, 'Expected to display 2 saved search');
  });
});

test('should show confirmation on clicking the delete button', function(assert) {
  this.render(hbs`{{host-list/content-filter/system-filters}}`);
  return wait().then(() => {
    assert.equal(this.$('.filter-list').length, 1);
    assert.equal(this.$('.filter-list__item').length, 2, 'Expected to display 2 saved search');
    $('.delete-filter button:eq(0)').trigger('click');
    return wait().then(() => {
      assert.equal($('#modalDestination .confirmation-modal').length, 1);
    });
  });
});
