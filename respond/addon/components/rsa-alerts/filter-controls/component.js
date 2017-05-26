import Component from 'ember-component';
import connect from 'ember-redux/components/connect';

const defaultSeverityRange = [0, 100];

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries: { alertTypes, alertSources },
      alerts: { itemsFilters }
    }
  } = state;

  return {
    alertTypeFilters: itemsFilters['alert.groupby_type'] || [],
    alertSourceFilters: itemsFilters['alert.source'] || [],
    severityFilter: itemsFilters['alert.severity'] || defaultSeverityRange,
    partOfIncidentFilters: itemsFilters.partOfIncident || [],
    alertTypes,
    alertSources
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
  actions: {
    toggleTypeFilter(type) {
      const alertTypeFilters = this.get('alertTypeFilters');
      this.get('updateFilter')({
        'alert.groupby_type': alertTypeFilters.includes(type) ? alertTypeFilters.without(type) : [...alertTypeFilters, type]
      });
    },
    toggleSourceFilter(source) {
      const alertSourceFilters = this.get('alertSourceFilters');
      this.get('updateFilter')({
        'alert.source': alertSourceFilters.includes(source) ? alertSourceFilters.without(source) : [...alertSourceFilters, source]
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
          type: 'range',
          start: range[0],
          end: range[1]
        }
      });
    }
  }
});

export default connect(stateToComputed, undefined)(AlertFilters);