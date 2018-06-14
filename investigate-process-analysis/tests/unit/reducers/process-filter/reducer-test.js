import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/process-filter/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import filterConfig from './process-filter-config';

const filterInitialState = {
  action: [],
  category: []
};
const initialState = Immutable.from({
  schema: [...filterConfig],
  filter: { ...filterInitialState }
});


module('Unit | Reducers | process-filter', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('UPDATE_FILTER_ITEMS updates the State with the filter item by adding the item', function(assert) {
    const payload = { filterName: 'action', optionSelected: 'createProcess', isSelected: true };
    const result = reducer(initialState, { type: ACTION_TYPES.UPDATE_FILTER_ITEMS, payload });
    assert.equal(result.filter.action[0], 'createProcess', 'Selected filter has been added to the State');
  });

  test('UPDATE_FILTER_ITEMS updates the State by removing the item', function(assert) {
    const payload = { filterName: 'action', optionSelected: 'createProcess', isSelected: false };
    const updatedState = Immutable.from({
      schema: [...filterConfig],
      filter: {
        action: ['createProcess'],
        category: []
      }
    });
    const result = reducer(updatedState, { type: ACTION_TYPES.UPDATE_FILTER_ITEMS, payload });
    assert.equal(result.filter.action.length, 0, 'Selected filter item has been removed from the state');
  });

  test('RESET_FILTER_ITEMS updates the State by returning the filters back to the initial state', function(assert) {
    const updatedState = Immutable.from({
      schema: [...filterConfig],
      filter: {
        action: ['createProcess'],
        category: ['File Event']
      }
    });
    const result = reducer(updatedState, { type: ACTION_TYPES.RESET_FILTER_ITEMS });
    assert.deepEqual(result, initialState, 'Filters have been set to initial state');
  });

  test('UPDATE_ACTION_FILTER_ITEMS Updates the schema with action config as [] when more than 1 category is selected', function(assert) {
    const updatedState = Immutable.from({
      schema: [...filterConfig],
      filter: {
        action: [],
        category: ['File Event']
      }
    });
    const result = reducer(updatedState, { type: ACTION_TYPES.UPDATE_ACTION_FILTER_ITEMS, payload: { isSelected: true, optionSelected: 'Registry Event' } });
    assert.equal(result.schema[1].options.length, 0, 'Action filter list is updated to [] when more than 1 category is selected');
  });

  test('UPDATE_ACTION_FILTER_ITEMS Updates the schema with relevent action config for the selected categories', function(assert) {
    const updatedState = Immutable.from({
      schema: [...filterConfig],
      filter: {
        action: [],
        category: ['File Event', 'Registry Event']
      }
    });
    const result = reducer(updatedState, { type: ACTION_TYPES.UPDATE_ACTION_FILTER_ITEMS, payload: { isSelected: false, optionSelected: 'Registry Event' } });
    assert.equal(result.schema[1].options.length, 10, 'Action filter list is updated with relevent actions if only 1 Category is selected');
  });

});