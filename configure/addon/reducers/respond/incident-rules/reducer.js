import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/respond';
import reduxActions from 'redux-actions';

const initialState = {
  rules: [],
  rulesStatus: null,
  selectedRule: null,
  isTransactionUnderway: false
};

const reducer = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_INCIDENT_RULES_STARTED]: (state) => {
    return state.merge({
      rules: [],
      rulesStatus: 'wait'
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_RULES]: (state, action) => {
    const { payload: { data } } = action;
    return state.merge({
      rules: data,
      rulesStatus: 'complete'
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_RULES_FAILED]: (state) => {
    return state.set('rulesStatus', 'error');
  },

  [ACTION_TYPES.INCIDENT_RULES_SELECT_RULE]: (state, { payload }) =>
    state.set('selectedRule', payload === state.selectedRule ? null : payload),

  [ACTION_TYPES.INCIDENT_RULES_DELETE_STARTED]: (state) => state.set('isTransactionUnderway', true),

  [ACTION_TYPES.INCIDENT_RULES_DELETE_FAILED]: (state) => state.set('isTransactionUnderway', false),

  [ACTION_TYPES.INCIDENT_RULES_DELETE]: (state, { payload: { data } }) => {
    const { rules } = state;
    // Filter out newly deleted items from the main items array
    const filteredRules = rules.filter((item) => (item.id !== data.id));
    // Update the order on the remaining rules
    const updatedRules = filteredRules.map((rule, index) => {
      return { ...rule, order: index + 1 };
    });

    return state.merge({
      rules: updatedRules,
      isTransactionUnderway: false,
      selectedRule: null
    });
  },
  [ACTION_TYPES.INCIDENT_RULES_REORDER_STARTED]: (state) => state.set('isTransactionUnderway', true),
  [ACTION_TYPES.INCIDENT_RULES_REORDER_FAILED]: (state) => state.set('isTransactionUnderway', false),
  [ACTION_TYPES.INCIDENT_RULES_REORDER]: (state, { payload: { data } }) => {
    return state.merge({
      isTransactionUnderway: false,
      rules: data
    });
  },
  [ACTION_TYPES.INCIDENT_RULES_CLONE_STARTED]: (state) => state.set('isTransactionUnderway', true),
  [ACTION_TYPES.INCIDENT_RULES_CLONE]: (state) => state.set('isTransactionUnderway', false),
  [ACTION_TYPES.INCIDENT_RULES_CLONE_FAILED]: (state) => state.set('isTransactionUnderway', false)
}, Immutable.from(initialState));

export default reducer;