import Component from '@ember/component';
import { connect } from 'ember-redux';
import moment from 'moment';
import { hasError, hasWarning, warningsWithServiceName, isComplete } from 'investigate-events/reducers/investigate/query-stats/selectors';
import { selectedService } from 'investigate-events/reducers/investigate/services/selectors';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';

const stateToComputed = (state) => ({
  filters: encodeMetaFilterConditions(state.investigate.queryNode.pillsData),
  serviceId: state.investigate.queryNode.serviceId,
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  description: state.investigate.queryStats.description,
  warnings: warningsWithServiceName(state),
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  selectedService: selectedService(state),
  isComplete: isComplete(state)
});

const ConsolePanel = Component.extend({
  dateFormat: service(),
  timeFormat: service(),

  classNames: ['console-panel'],
  classNameBindings: [
    'hasError',
    'hasWarning'
  ],

  @computed('endTime', 'dateFormat.selected.format', 'timeFormat.selected.format')
  formattedEndDate: (endTime, dateFormat, timeFormat) => {
    return moment(endTime).format(`${dateFormat} ${timeFormat}`);
  },

  @computed('startTime', 'dateFormat.selected.format', 'timeFormat.selected.format')
  formattedStartDate: (startTime, dateFormat, timeFormat) => {
    return moment(startTime).format(`${dateFormat} ${timeFormat}`);
  }

});

export default connect(stateToComputed)(ConsolePanel);
