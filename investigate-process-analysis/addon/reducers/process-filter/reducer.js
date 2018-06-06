import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import filterConfig from './process-filter-config';

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
    const { filter } = state;
    if (isSelected) {
      selectedItems = [...filter[filterName], optionSelected];
    } else {
      selectedItems = [...filter[filterName]];
      _.pull(selectedItems, optionSelected);
    }
    return state.setIn(['filter', filterName], selectedItems);
  },

  [ACTION_TYPES.RESET_FILTER_ITEMS]: (state) => {
    return state.merge({ ...processFilterInitialState });
  }

}, processFilterInitialState);

export default processFilter;
