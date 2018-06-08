import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import filterConfig from './process-filter-config';
import actionConfig from './action-config';
import { updatefilterActionArray } from './utils';


const filterInitialState = {
  action: [],
  category: []
};
const processFilterInitialState = Immutable.from({
  schema: [...filterConfig],
  filter: { ...filterInitialState }
});

const processFilter = reduxActions.handleActions({

  [ACTION_TYPES.UPDATE_FILTER_ITEMS]: (state, { payload: { filterName, optionSelected, isSelected } }) => {

    let selectedItems;
    const { schema, filter, filter: { action } } = state;

    if (isSelected) {
      selectedItems = [...filter[filterName], optionSelected];
    } else {
      selectedItems = [...filter[filterName]];
      _.pull(selectedItems, optionSelected);

      // To remove the actions from the filter that are no longer valid when a category is unselected.
      if (filterName === 'category') {
        const filter = { category: selectedItems, action: updatefilterActionArray(action, schema[1].options) };
        return state.set('filter', filter);
      }
    }

    return state.setIn(['filter', filterName], selectedItems);
  },

  [ACTION_TYPES.RESET_FILTER_ITEMS]: (state) => {
    return state.merge({ ...processFilterInitialState });
  },

  /* Updates the schema with relevent action config for the selected categories*/

  [ACTION_TYPES.UPDATE_ACTION_FILTER_ITEMS]: (state, { payload: { isSelected, optionSelected } }) => {
    const { category } = state.filter;
    let categoryListUpdated = [...category];
    const actionObj = {
      name: 'action',
      options: []
    };
    if (isSelected) {
      categoryListUpdated = [...category, optionSelected];
    } else {
      _.pull(categoryListUpdated, optionSelected);
    }
    for (let i = 0; i < categoryListUpdated.length; i++) {
      actionObj.options.push(...actionConfig[categoryListUpdated[i]]);
    }

    return state.set('schema', [...filterConfig, actionObj]);
  }

}, processFilterInitialState);

export default processFilter;
