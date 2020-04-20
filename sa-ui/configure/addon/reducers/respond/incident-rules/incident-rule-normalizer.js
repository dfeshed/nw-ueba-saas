import _ from 'lodash';

// Configuration that defines one group with one empty condition
const emptyConditionsConfig = '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and","filters":[{"alertRuleFilter":{"filterType":"FILTER","property": null,"operator": null,"value": null}}]}}';

/**
 * Provides functionality for parsing a rule's filter conditions into flat data structures (conditions, groups) with
 * automaticaly generated IDs, as well as reconstructing the original stringified JSON format
 * @class AggregationRuleNormalizer
 * @public
 */
export default {
  /**
   * The counter that keeps track of a unique value used for IDs in conditions
   * @property conditionCounter
   * @private
   */
  conditionCounter: 0,
  /**
   * The counter that keeps track of a unique value used for IDs in groups
   * @property groupCounter
   * @private
   */
  groupCounter: 0,
  /**
   * Clears out all rule configuration and creates a new group with one empty condition
   * @public
   */
  emptyConditions() {
    return this.processRuleConfiguration(emptyConditionsConfig);
  },

  /**
   * The processRuleConfiguration func flattens out the nested/hierarchical rule condition data structure to a set of groups and
   * conditions objects for easy updating via reducer. It also uses group/condition counters to add de facto
   * IDs to each group and condition, and groupId references for the nested relationship
   * @param config
   * @returns {{groups: {}, conditions: {}}}
   * @public
   */
  processRuleConfiguration(config) {
    this.resetCounters();
    if (typeof config === 'string') {
      config = JSON.parse(config);
    }
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
  /**
   * Reconstructs the original format of the rule match conditions (as stringified JSON) for persisitng to the server
   * @param conditionGroups
   * @param conditions
   * @public
   */
  toJSON(conditionGroups, conditions) {
    const groups = _.values(conditionGroups);
    const matchConditions = _.values(conditions);

    function getConditionsForGroup(conditions, id) {
      return conditions.filter((condition) => condition.groupId === id).map((condition) => {
        const matchCondition = { ...condition };
        return {
          alertRuleFilter: _.omit(matchCondition, ['id', 'groupId'])
        };
      });
    }

    const payload = groups.reduce((rules, group, index) => {
      const { id } = group;
      const filters = getConditionsForGroup(matchConditions, id);
      group = _.omit(group, ['id', 'groupId']);

      if (index === 0) { // if we're evaluating the root group
        const rootGroup = { ...group, filters };
        rules.alertRuleFilterGroup = _.omit(rootGroup, ['id']);
      } else {
        const rootGroup = rules.alertRuleFilterGroup;
        rootGroup.filters = rootGroup.filters || [];
        rootGroup.filters.push({
          alertRuleFilterGroup: {
            ...group,
            filters
          }
        });
      }
      return rules;
    }, {});

    return JSON.stringify(payload);
  },
  /**
   * Returns the next ID available for a new group
   * @returns {number}
   * @public
   */
  getNewGroupId() {
    return this.groupCounter++;
  },
  /**
   * Returns the next ID available for a new condition
   * @returns {number}
   * @public
   */
  getNewConditionId() {
    return this.conditionCounter++;
  },
  /**
   * Resets all of the ID generation counters to zero
   * @public
   */
  resetCounters() {
    this.conditionCounter = 0;
    this.groupCounter = 0;
  }
};