import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

export const ruleNormalizer = {
  conditionCounter: 0,
  groupCounter: 0,
  // The processRuleConfiguration func flattens out the nested/hierarchical rule condition data structure to a set of groups and
  // conditions objects for easy updating via reducer. It also uses group/condition counters to add de facto
  // IDs to each group and condition, and groupId references for the nested relationship
  processRuleConfiguration(config) {
    const normalizedRules = {
      groups: {},
      conditions: {}
    };
    const process = (config, groupId) => {
      Object.keys(config).forEach((key) => {
        const filter = config[key];
        // If it's a grouping
        if (key === 'alertRuleFilterGroup') {
          const id = this.getNewGroupId();
          filter.id = id;
          if (groupId !== undefined) {
            filter.groupId = groupId;
          }
          normalizedRules.groups[id] = filter;
          if (filter.filters && filter.filters.length) {
            filter.filters.forEach((condition) => {
              process(condition, id);
              delete filter.filters;
            });
          }
        } else {
          // Otherwise it's a condition
          const conditionId = this.getNewConditionId();
          filter.groupId = groupId;
          filter.id = conditionId;
          normalizedRules.conditions[conditionId] = filter;
        }
      });
    };
    process(config);
    return normalizedRules;
  },
  getNewGroupId() {
    return this.groupCounter++;
  },
  getNewConditionId() {
    return this.conditionCounter++;
  },
  resetCounters() {
    this.conditionCounter = 0;
    this.groupCounter = 0;
  }
};

const initialState = {
  rule: null,
  ruleStatus: null,
  conditionGroups: null,
  conditions: null,
  fields: [],
  fieldsStatus: null
};

const reducer = reduxActions.handleActions({
  [ACTION_TYPES.FETCH_AGGREGATION_RULE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('ruleStatus', 'wait'),
      success: (s) => {
        const { payload: { data } } = action;
        const normalizedConditions = ruleNormalizer.processRuleConfiguration(JSON.parse(data.uiFilterConditions));
        return s.merge({
          ruleStatus: 'complete',
          rule: data,
          conditionGroups: normalizedConditions.groups,
          conditions: normalizedConditions.conditions
        });
      },
      failure: (s) => s.set('ruleStatus', 'error')
    });
  },
  [ACTION_TYPES.FETCH_AGGREGATION_FIELDS]: (state, action) => {
    const { payload } = action;
    return handle(state, action, {
      start: (s) => s.set('fieldsStatus', 'wait'),
      success: (s) => s.merge({
        fields: payload.data,
        fieldsStatus: 'complete'
      }),
      failure: (s) => s.merge({
        fields: [],
        fieldsStatus: 'error'
      })
    });
  },
  [ACTION_TYPES.AGGREGATION_RULES_ADD_CONDITION]: (state, { payload }) => {
    const id = ruleNormalizer.getNewConditionId();
    const condition = {
      [id]: { id, groupId: payload }
    };
    return state.set('conditions', Immutable.merge(state.conditions, condition));
  },
  [ACTION_TYPES.AGGREGATION_RULES_REMOVE_CONDITION]: (state, { payload }) => {
    return state.set('conditions', Immutable.without(state.conditions, payload));
  },
  [ACTION_TYPES.AGGREGATION_RULES_ADD_GROUP]: (state) => {
    const id = ruleNormalizer.getNewGroupId();
    const group = {
      [id]: { id, groupId: 0, logicalOperator: 'and' } // all additional groups are added under the root level group
    };
    return state.set('conditionGroups', Immutable.merge(state.conditionGroups, group));
  },
  [ACTION_TYPES.AGGREGATION_RULES_REMOVE_GROUP]: (state, { payload }) => {
    return state.set('conditionGroups', Immutable.without(state.conditionGroups, payload));
  },
  [ACTION_TYPES.AGGREGATION_RULES_UPDATE_CONDITION]: (state, { payload: { conditionId, changes } }) => {
    const condition = state.conditions[conditionId];
    const updatedCondition = {
      [conditionId]: condition.merge(changes)
    };
    return state.set('conditions', Immutable.merge(state.conditions, updatedCondition));
  },
  [ACTION_TYPES.AGGREGATION_RULES_UPDATE_GROUP]: (state, { payload: { groupId, changes } }) => {
    const group = state.conditionGroups[groupId];
    const updatedGroup = {
      [groupId]: group.merge(changes)
    };
    return state.set('conditionGroups', Immutable.merge(state.conditionGroups, updatedGroup));
  }
}, Immutable.from(initialState));

export default reducer;