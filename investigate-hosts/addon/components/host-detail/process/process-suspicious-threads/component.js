import Component from '@ember/component';
import { connect } from 'ember-redux';
import { suspiciousThreadsData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-suspicious-threads-columns';

const stateToComputed = (state) => ({
  threadList: suspiciousThreadsData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const suspiciousThreads = Component.extend({

  tagName: 'box',

  classNames: ['process-suspicious-threads-list']
});

export default connect(stateToComputed)(suspiciousThreads);