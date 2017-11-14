import Component from '@ember/component';
import { TIME_UNITS as timeWindowOptions, createDuration } from 'respond/utils/date/duration';
import computed from 'ember-computed-decorators';
import {
  getVisited,
  getSelectedGroupByFields,
  hasInvalidGroupByFields,
  getTimeWindowValue,
  getTimeWindowUnit,
  getSelectedCategories,
  getAssigneeOptions,
  getSelectedAssignee,
  getIncidentCreationOptions,
  hasInvalidTimeValue,
  getPriorityScale,
  hasInvalidPriorityScale,
  getIncidentScoringOptions,
  getIncidentTitle
} from 'respond/selectors/aggregation-rule';
import {
  getGroupedCategories
} from 'respond/selectors/dictionaries';
import Confirmable from 'respond/mixins/confirmable';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';

const stateToComputed = (state) => {
  return {
    visited: getVisited(state),
    incidentCreationOptions: getIncidentCreationOptions(state),
    incidentScoringOptions: getIncidentScoringOptions(state),
    selectedGroupByFields: getSelectedGroupByFields(state),
    hasInvalidGroupByFields: hasInvalidGroupByFields(state),
    timeWindowUnit: getTimeWindowUnit(state),
    timeWindowValue: getTimeWindowValue(state),
    groupedCategories: getGroupedCategories(state),
    selectedCategories: getSelectedCategories(state),
    assigneeOptions: getAssigneeOptions(state),
    selectedAssignee: getSelectedAssignee(state),
    hasInvalidTimeValue: hasInvalidTimeValue(state),
    priorityScale: getPriorityScale(state),
    hasInvalidPriorityScale: hasInvalidPriorityScale(state),
    incidentRuleTitle: getIncidentTitle(state)
  };
};

const AggregationRuleGroupingOptions = Component.extend(Confirmable, {
  classNames: ['aggregation-rule-grouping-options'],
  i18n: inject(),

  timeWindowOptions,

  @computed('timeWindowUnit')
  maxTimeWindowValue(timeWindowUnit) {
    // There is a maximu of 25 days allowed for the time window, otherwise a maximum of 100 hours/minutes
    return timeWindowUnit === 'DAY' ? 25 : 100;
  },

  actions: {
    handleGroupByChange(value) {
      this.get('update')('ruleInfo.groupByFields', value.map((field) => field.groupByField || field.value));
    },
    handleTimeWindowUnitChange(value) {
      const duration = createDuration(this.get('timeWindowValue'), value);
      this.get('update')('ruleInfo.timeWindow', duration);
    },
    handleTimeWindowValueChange(value) {
      const duration = createDuration(value, this.get('timeWindowUnit'));
      this.get('update')('ruleInfo.timeWindow', duration);
    },
    handleIncidentTitleChange(value) {
      this.get('update')('ruleInfo.incidentCreationOptions.ruleTitle', value);
    },
    handleIncidentSummaryChange(value) {
      this.get('update')('ruleInfo.incidentCreationOptions.ruleSummary', value);
    },
    handleCategoriesChange(value) {
      this.get('update')('ruleInfo.incidentCreationOptions.categories', value);
    },
    handleAssigneeChange({ id, name, emailAddress }) {
      const assignee = id === null ? null : { id, name, emailAddress };
      this.get('update')('ruleInfo.incidentCreationOptions.assignee', assignee);
    },
    handlePriorityScoringChange(value) {
      this.get('update')('ruleInfo.incidentScoringOptions.type', value);
    },
    handlePriorityScoreChange(field, value) {
      this.get('update')(field, parseInt(value, 10));
    },
    update() {
      this.get('update')(...arguments);
    }
  }
});

export default connect(stateToComputed)(AggregationRuleGroupingOptions);