import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import {
  savedFilter,
  expressionList,
  filters
} from 'admin-source-management/reducers/usm/filters/filters-selectors';

module('Unit | Selectors | filters-selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('savedFilter selector', function(assert) {
    // No ReduxDataHelper because we're selecting from computed component state
    const expectedFilter = {
      criteria: {
        expressionList: new Array(2)
      }
    };
    const state = Immutable.from({
      filterState: {
        selectedFilter: expectedFilter
      }
    });
    const filter = savedFilter(state);
    assert.deepEqual(filter, expectedFilter, 'savedFilter() returned the expected value');
  });

  test('expressionList selector', function(assert) {
    // No ReduxDataHelper because we're selecting from computed component state
    let state = Immutable.from({
      filterState: {
        selectedFilter: {
          criteria: {
            expressionList: new Array(2)
          }
        }
      }
    });
    let expressions = expressionList(state);
    assert.equal(expressions.length, 2, '2 expressions as expected');

    // No ReduxDataHelper because we're selecting from computed component state
    state = Immutable.from({
      filterState: {
        selectedFilter: null
      }
    });
    expressions = expressionList(state);
    assert.equal(expressions.length, 0, '0 expressions as expected');
  });

  test('filters selector - no filter values', function(assert) {
    // No ReduxDataHelper because we're selecting from computed component state
    const state = Immutable.from({
      filterState: {
        selectedFilter: {
          criteria: {
            expressionList: []
          }
        }
      },
      filterTypes: [
        {
          name: 'publishStatus',
          label: 'adminUsm.policies.list.publishStatus',
          listOptions: [],
          type: 'list'
        }
      ]
    });
    const filterz = filters(state);
    assert.equal(filterz[0].filterValue, undefined, 'no filter values as expected');
  });

  test('filters selector - with filter values', function(assert) {
    // No ReduxDataHelper because we're selecting from computed component state
    const state = Immutable.from({
      filterState: {
        selectedFilter: {
          criteria: {
            expressionList: [
              /* {
                propertyValues: [{ value: 'test' }],
                restrictionType: 'IN',
                propertyName: 'machineName'
              }, */
              /* {
                propertyValues: [{ value: 5, relativeValueType: 'Minutes' }],
                restrictionType: 'IN',
                propertyName: 'scanTime'
              }, */
              {
                propertyValues: [{ value: 'published' }, { value: 'unpublished' }],
                restrictionType: 'IN',
                propertyName: 'publishStatus'
              }
              /* {
                propertyValues: [{ value: 204800 }],
                restrictionType: 'IN',
                propertyName: 'size'
              } */
            ]
          }
        }
      },
      filterTypes: [
        /* {
          type: 'text',
          label: 'Test',
          name: 'machineName'
        }, */
        /* {
          type: 'date',
          label: 'Test',
          name: 'scanTime'
        }, */
        {
          name: 'publishStatus',
          label: 'adminUsm.policies.list.publishStatus',
          listOptions: [],
          type: 'list'
        }
        /* {
          type: 'number',
          label: 'Test',
          name: 'size'
        } */
      ]
    });
    const filterz = filters(state);
    assert.equal(filterz.length, 1, '1 filter as expected');

    // publishStatus list (checkbox) filter
    // const publishStatusExpected = { name: 'publishStatus', label: 'Publication Status', listOptions: [], type: 'list', filterValue: ['published', 'unpublished'] };
    const pubStatFilterValueExpected = ['published', 'unpublished'];
    const [publishStatus] = filterz.filterBy('name', 'publishStatus');
    assert.deepEqual(publishStatus.filterValue, pubStatFilterValueExpected, 'list (checkbox) filter values are as expected');
  });

});
