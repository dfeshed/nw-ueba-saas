import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/filters/filters-reducers';

const policiesFilterInitialState = new ReduxDataHelper().policiesFilter().build().usm.policiesFilter;

module('Unit | Reducers | Filters Reducers', function() {

  test('should return the initial policiesFilter state', function(assert) {
    const expectedEndState = new ReduxDataHelper().policiesFilter().build().usm.policiesFilter;
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, expectedEndState);
  });

  test('on APPLY_FILTER for POLICIES, expressionList & selectedFilter are properly set', function(assert) {
    const expectedExpressionList = [
      {
        restrictionType: 'IN',
        propertyValues: [
          {
            value: 'published'
          }
        ],
        propertyName: 'publishStatus'
      }
    ];
    const expectedEndState = new ReduxDataHelper()
      .policiesFilter()
      .policiesFilterExpressionList(expectedExpressionList)
      .policiesFilterSelectedFilter({ id: 1, criteria: { expressionList: expectedExpressionList } })
      .build().usm.policiesFilter;
    const action = {
      type: ACTION_TYPES.APPLY_FILTER,
      payload: [
        {
          restrictionType: 'IN',
          propertyValues: [
            {
              value: 'published'
            }
          ],
          propertyName: 'publishStatus'
        }
      ],
      meta: {
        belongsTo: 'POLICIES'
      }
    };
    const endState = reducers(Immutable.from(policiesFilterInitialState), action);
    assert.deepEqual(endState, expectedEndState, 'expressionList is properly set');
  });

  test('on RESET_FILTER for POLICIES, selectedFilter is reset', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .policiesFilter()
      .policiesFilterSelectedFilter({ id: 1, criteria: { expressionList: [] } })
      .build().usm.policiesFilter;
    const action = {
      type: ACTION_TYPES.RESET_FILTER,
      meta: {
        belongsTo: 'POLICIES'
      }
    };
    const endState = reducers(Immutable.from(policiesFilterInitialState), action);
    assert.deepEqual(endState, expectedEndState, 'selectedFilter is reset');
  });

});
