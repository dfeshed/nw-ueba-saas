import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  getAlertNames,
  getAlertTypeFilters,
  getAlertSourceFilters,
  getAlertNameFilters,
  getSeverityFilters,
  getPartOfIncidentFilters
} from 'respond/selectors/alerts';

import {
  getAlertTypes,
  getAlertSources
} from 'respond/selectors/dictionaries';

const stateToComputed = (state) => {
  return {
    alertTypeFilters: getAlertTypeFilters(state),
    alertSourceFilters: getAlertSourceFilters(state),
    alertNameFilters: getAlertNameFilters(state),
    severityFilter: getSeverityFilters(state),
    partOfIncidentFilters: getPartOfIncidentFilters(state),
    alertTypes: getAlertTypes(state),
    alertSources: getAlertSources(state),
    alertNames: getAlertNames(state)
  };
};

/**
 * @class AlertFilters
 * Represents the alert filters for populating the explorer filters panel
 *
 * @public
 */
const AlertFilters = Component.extend({
  tagName: '',

  selectedNames: computed('alertNameFilters', function() {
    return [...this.alertNameFilters];
  }),

  init() {
    this._super(arguments);
    this.partOfIncidentTypes = this.partOfIncidentTypes || [true, false];
    this.alertNamesTableColumns = this.alertNamesTableColumns || [{
      field: 'name',
      width: '100%'
    }];
  },

  actions: {
    toggleTypeFilter(type) {
      const alertTypeFilters = this.get('alertTypeFilters');
      this.get('updateFilter')({
        'alert.type': alertTypeFilters.includes(type) ? alertTypeFilters.without(type) : [...alertTypeFilters, type]
      });
    },
    toggleSourceFilter(source) {
      const alertSourceFilters = this.get('alertSourceFilters');
      this.get('updateFilter')({
        'alert.source': alertSourceFilters.includes(source) ? alertSourceFilters.without(source) : [...alertSourceFilters, source]
      });
    },
    toggleAlertNameFilter(selectedNames) {
      this.get('updateFilter')({
        'alert.name': selectedNames
      });
    },
    toggleIsPartOfIncidentFilter(partOfIncident) {
      const partOfIncidentFilters = this.get('partOfIncidentFilters');
      this.get('updateFilter')({
        partOfIncident: partOfIncidentFilters.includes(partOfIncident) ? partOfIncidentFilters.without(partOfIncident) : [...partOfIncidentFilters, partOfIncident]
      });
    },
    severityRangeChanged(range) {
      this.get('updateFilter')({
        'alert.severity': {
          isRange: true,
          type: 'numeric',
          start: range[0],
          end: range[1]
        }
      });
    }
  }
});

export default connect(stateToComputed, undefined)(AlertFilters);
