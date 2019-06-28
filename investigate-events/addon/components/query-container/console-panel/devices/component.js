import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { actualEventCount, SORT_ORDER } from 'investigate-events/reducers/investigate/event-results/selectors';
import { thousandFormat } from 'component-lib/utils/numberFormats';
import { filterElements, getHeight } from 'component-lib/utils/jquery-replacement';

import {
  hasWarning,
  hasError,
  offlineServices,
  decoratedDevices,
  warningsWithServiceName,
  slowestInQuery,
  offlineServicesPath,
  warningsPath,
  queryTimeElapsed,
  streamingTimeElapsed
} from 'investigate-events/reducers/investigate/query-stats/selectors';
import { run } from '@ember/runloop';

const stateToComputed = (state) => ({
  queryTimeElapsed: queryTimeElapsed(state),
  streamingTimeElapsed: streamingTimeElapsed(state),
  warningsPath: warningsPath(state),
  offlineServicesPath: offlineServicesPath(state),
  warnings: warningsWithServiceName(state),
  slowestInQuery: slowestInQuery(state),
  offlineServices: offlineServices(state),
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  devices: decoratedDevices(state),
  eventCount: state.investigate.eventCount.data,
  formattedEventCount: thousandFormat(state.investigate.eventCount.data),
  eventTimeSortOrderPreferenceWhenQueried: state.investigate.eventResults.eventTimeSortOrderPreferenceWhenQueried,
  resultCountAtThreshold: resultCountAtThreshold(state),
  formattedActualEventCount: thousandFormat(actualEventCount(state)),
  actualEventCount: actualEventCount(state)
});

const DevicesStatus = Component.extend({
  classNames: ['devices-status'],
  classNameBindings: ['hasError', 'hasWarning'],
  isExpanded: false,
  height: 0,

  @computed('resultCountAtThreshold', 'eventTimeSortOrderPreferenceWhenQueried')
  eventAge: (resultCountAtThreshold, eventTimeSortOrder) => {
    if (resultCountAtThreshold && eventTimeSortOrder) {
      return (eventTimeSortOrder === SORT_ORDER.ASC) ? 'oldest' : 'newest';
    }
  },

  updateHeight() {
    run.schedule('afterRender', () => {
      const { element } = this;
      const unfilteredDeviceHierarchy = element.querySelectorAll('.device-hierarchy li');
      const allItems = filterElements(unfilteredDeviceHierarchy, '.device-hierarchy .device-hierarchy .device-hierarchy li');
      const fullHeight = getHeight(allItems[0]);
      const lastItemHeight = getHeight(allItems[allItems.length - 1]);
      const whitespace = 5;
      this.set('height', (fullHeight - lastItemHeight) + whitespace);
    });
  },

  actions: {
    expandDevices() {
      this.toggleProperty('isExpanded');
      this.updateHeight();
    },

    devicesExpanded() {
      this.updateHeight();
    }
  }
});

export default connect(stateToComputed)(DevicesStatus);
