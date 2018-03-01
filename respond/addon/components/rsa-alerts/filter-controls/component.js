import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAlertNames } from 'respond/selectors/alerts';

const defaultSeverityRange = [0, 100];

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries: { alertTypes, alertSources },
      alerts: { itemsFilters }
    }
  } = state;

  const severityFilter = itemsFilters['alert.severity'];
  const severityRange = severityFilter ? [severityFilter.start, severityFilter.end] : defaultSeverityRange;

  return {
    alertTypeFilters: itemsFilters['alert.type'] || [],
    alertSourceFilters: itemsFilters['alert.source'] || [],
    alertNameFilters: itemsFilters['alert.name'] || [],
    severityFilter: severityRange,
    partOfIncidentFilters: itemsFilters.partOfIncident || [],
    alertTypes,
    alertSources,
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
  partOfIncidentTypes: [true, false],
  alertNamesTableColumns: [{
    field: 'name',
    width: '100%'
  }],

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
    toggleAlertNameFilter(alertName) {
      const alertNameFilters = this.get('alertNameFilters');
      this.get('updateFilter')({
        'alert.name': alertNameFilters.includes(alertName) ? alertNameFilters.without(alertName) : [...alertNameFilters, alertName]
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
