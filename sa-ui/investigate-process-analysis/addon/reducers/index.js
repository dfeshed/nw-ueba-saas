import { combineReducers } from 'redux';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';
import processTree from './process-tree/reducer';
import processProperties from './process-properties/reducer';
import processVisuals from './process-visuals/reducer';
import services from './services/reducer';
import query from './query/reducer';
import processFilter from './process-filter/reducer';
import filterPopup from './filter-popup/reducer';
import hostContext from './host-context/reducer';
import risk from 'investigate-shared/reducers/risk/reducer';

const reducerPredicate = (type) => {
  return (action) => {
    return action.meta && action.meta.belongsTo === type;
  };
};

export default combineReducers({
  processAnalysis: combineReducers({
    processTree,
    processProperties,
    processVisuals,
    services,
    query,
    processFilter,
    filterPopup,
    hostContext,
    risk: createFilteredReducer(risk, reducerPredicate('FILE'))
  }),
  investigate: query
});
