import Component from '@ember/component';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import getEventsTableColumnForGivenIndicator from 'entity-details/utils/column-config';
import { getIndicatorEntity, indicatorEvents, areAllEventsReceived, indicatorEventError, getIncidentData } from 'entity-details/reducers/indicators/selectors';
import computed from 'ember-computed-decorators';
import { getEvents } from 'entity-details/actions/indicator-details';
import { navigateToInvestigate } from 'entity-details/utils/pivot-utils';

const stateToComputed = (state) => ({
  indicatorKey: getIndicatorEntity(state),
  events: indicatorEvents(state),
  areAllEventsReceived: areAllEventsReceived(state),
  indicatorEventError: indicatorEventError(state),
  incidentDetails: getIncidentData(state)
});

const dispatchToActions = {
  getEvents
};


const EventsListComponent = Component.extend({
  scrolling: false,
  classNames: ['entity-details-container-body-indicator-details_events-table'],
  @computed('indicatorKey')
  columnConfig(indicatorKey) {
    if (indicatorKey) {
      return getEventsTableColumnForGivenIndicator(indicatorKey);
    }
  },

  didInsertElement() {
    this._super(...arguments);
    const tableElement = this.element.querySelector('.rsa-data-table-body');
    tableElement.addEventListener('scroll', ({ target }) => {
      // This logic to avoid multiple server calls when user is scrolling.
      if (false === this.get('scrolling')) {
        this.set('scrolling', true);
        later(() => {
          if (target.scrollHeight - (target.scrollTop + target.offsetHeight) < 30) {
            if (!this.get('areAllEventsReceived')) {
              this.send('getEvents');
            }
          }
          this.set('scrolling', false);
        }, 500);
      }
    });
  },
  actions: {
    pivotToInvestigate(item, column) {
      const { entityType, entityName, dataEntitiesIds } = this.get('incidentDetails');
      const eventTime = item.eventDate.epochSecond;
      navigateToInvestigate(entityType, entityName, dataEntitiesIds[0], eventTime, item, column);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsListComponent);