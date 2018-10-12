import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  expressionList,
  isSystemFilter,
  filters
} from 'investigate-shared/selectors/endpoint-filters/selectors';

module('Unit | Selectors | endpoint filters', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Returns expression list for saved filter', function(assert) {
    let state = Immutable.from({
      filter: {
        selectedFilter: {
          criteria: {
            expressionList: new Array(2)
          }
        }
      }
    });
    let data = expressionList(state);
    assert.equal(data.length, 2);

    state = Immutable.from({
      filter: {
        selectedFilter: null
      }
    });
    data = expressionList(state);
    assert.equal(data.length, 0);
  });

  test('Returns systemFilter property value', function(assert) {
    const state = Immutable.from({
      filter: {
        selectedFilter: {
          systemFilter: true,
          criteria: {
            expressionList: []
          }
        }
      }
    });
    const data = isSystemFilter(state);
    assert.equal(data, true);
  });


  test('Returns filters object without any filter value', function(assert) {
    const state = Immutable.from({
      filter: {
        selectedFilter: {
          systemFilter: true,
          criteria: {
            expressionList: []
          }
        }
      },
      filterTypes: [
        {
          type: 'text',
          label: 'Test',
          name: 'machineName'
        }
      ]
    });
    const data = filters(state);
    assert.equal(data[0].filterValue, undefined);
  });

  test('Returns filters object with filter values based on expression', function(assert) {
    const state = Immutable.from({
      filter: {
        selectedFilter: {
          systemFilter: true,
          criteria: {
            expressionList: [
              {
                propertyValues: [{ value: 'test' }],
                restrictionType: 'IN',
                propertyName: 'machineName'
              },
              {
                propertyValues: [{ value: 5, relativeValueType: 'Minutes' }],
                restrictionType: 'IN',
                propertyName: 'scanTime'
              },
              {
                propertyValues: [{ value: 'blacklisted' }, { value: 'whitelisted' }],
                restrictionType: 'IN',
                propertyName: 'status'
              },
              {
                propertyValues: [{ value: 204800 }],
                restrictionType: 'IN',
                propertyName: 'size'
              }
            ]
          }
        }
      },
      filterTypes: [
        {
          type: 'text',
          label: 'Test',
          name: 'machineName'
        },
        {
          type: 'date',
          label: 'Test',
          name: 'scanTime'
        },
        {
          type: 'list',
          label: 'Test',
          name: 'status'
        },
        {
          type: 'number',
          label: 'Test',
          name: 'size'
        }
      ]
    });
    const data = filters(state);
    assert.equal(data.length, 4);
    const [size] = data.filterBy('name', 'size');
    const [date] = data.filterBy('name', 'scanTime');
    assert.equal(size.filterValue.unit, 'KB');
    assert.equal(date.filterValue.unit, 'Minutes');
  });


});
