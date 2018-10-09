import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import {
  hasError,
  hasWarning,
  isComplete,
  decoratedDevices,
  warningsWithServiceName,
  errorsWithServiceName
} from 'investigate-events/reducers/investigate/query-stats/selectors';
import { queriedService } from 'investigate-events/reducers/investigate/services/selectors';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';

const stateToComputed = (state) => ({
  startTime: state.investigate.queryNode.previousQueryParams.startTime,
  endTime: state.investigate.queryNode.previousQueryParams.endTime,
  description: state.investigate.queryStats.description,
  warnings: warningsWithServiceName(state),
  errors: errorsWithServiceName(state),
  isComplete: isComplete(state),
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  devices: decoratedDevices(state),
  filters: encodeMetaFilterConditions(state.investigate.queryNode.previousQueryParams.metaFilter),
  queriedService: queriedService(state)
});

const ConsolePanel = Component.extend({
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  classNames: ['console-panel'],
  classNameBindings: [
    'hasError',
    'hasWarning'
  ],

  @computed('endTime') formattedEndDate: (endTime) => endTime * 1000,
  @computed('startTime') formattedStartDate: (startTime) => startTime * 1000,

  @computed('dateFormat.selected.format', 'timeFormat.selected.format')
  format: (dateFormat, timeFormat) => {
    if (!dateFormat || !timeFormat) {
      return;
    }
    return `${dateFormat} ${timeFormat.replace(/.SSS/, '')}`;
  }
});

export default connect(stateToComputed)(ConsolePanel);
