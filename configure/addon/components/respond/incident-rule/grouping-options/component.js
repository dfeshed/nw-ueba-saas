import Component from '@ember/component';
import { TIME_UNITS as timeWindowOptions, createDuration } from 'configure/utils/date/duration';
import computed from 'ember-computed-decorators';
import {
  updateRule
} from 'configure/actions/creators/respond/incident-rule-creators';
import {
  getVisited,
  getFields as getGroupByFields,
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
} from 'configure/reducers/respond/incident-rules/rule/selectors';
import {
  getGroupedCategories
} from 'configure/reducers/respond/dictionaries/selectors';
import Confirmable from 'configure/mixins/confirmable';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';

const stateToComputed = (state) => {
  return {
    visited: getVisited(state),
    fields: getGroupByFields(state),
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

const dispatchToActions = function(dispatch) {
  return {
    // update the rule information using fully qualified field name (e.g., 'ruleInfo.incidentScoringOptions.type')
    update(field, value) {
      if (field && value !== undefined) {
        dispatch(updateRule(field, value));
      }
    }
  };
};

const IncidentRuleGroupingOptions = Component.extend(Confirmable, {
  classNames: ['incident-rule-grouping-options'],
  i18n: inject(),

  timeWindowOptions,

  @computed('timeWindowUnit')
  maxTimeWindowValue(timeWindowUnit) {
    // There is a maximu of 25 days allowed for the time window, otherwise a maximum of 100 hours/minutes
    return timeWindowUnit === 'DAY' ? 24 : 100;
  },

  actions: {
    handleGroupByChange(value) {
      this.send('update', 'ruleInfo.groupByFields', value.map((field) => field.groupByField || field.value));
    },
    handleTimeWindowUnitChange(value) {
      const duration = createDuration(this.get('timeWindowValue'), value);
      this.send('update', 'ruleInfo.timeWindow', duration);
    },
    handleTimeWindowValueChange(value) {
      const duration = createDuration(value, this.get('timeWindowUnit'));
      this.send('update', 'ruleInfo.timeWindow', duration);
    },
    handleIncidentTitleChange(value) {
      this.send('update', 'ruleInfo.incidentCreationOptions.ruleTitle', value);
    },
    handleIncidentSummaryChange(value) {
      this.send('update', 'ruleInfo.incidentCreationOptions.ruleSummary', value);
    },
    handleCategoriesChange(value) {
      this.send('update', 'ruleInfo.incidentCreationOptions.categories', value);
    },
    handleAssigneeChange({ id, name, emailAddress }) {
      const assignee = id === null ? null : { id, name, emailAddress };
      this.send('update', 'ruleInfo.incidentCreationOptions.assignee', assignee);
    },
    handlePriorityScoringChange(value) {
      this.send('update', 'ruleInfo.incidentScoringOptions.type', value);
    },
    handlePriorityScoreChange(field, value) {
      this.send('update', field, parseInt(value, 10));
    },
    update() {
      this.send('update', ...arguments);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentRuleGroupingOptions);