import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { eventsData } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { eventsTableConfig } from '../../../../reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  eventsData: eventsData(state),
  config: eventsTableConfig()
});

const processEventsTable = Component.extend({
  layout,
  classNames: ['process-events-table']
});
export default connect(stateToComputed)(processEventsTable);