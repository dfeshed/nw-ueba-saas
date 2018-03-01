import Component from '@ember/component';
import { connect } from 'ember-redux';
import { enrichedDllData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-dll-columns';

const stateToComputed = (state) => ({
  dllList: enrichedDllData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dllList = Component.extend({

  tagName: 'box',

  classNames: ['process-dll-list']
});

export default connect(stateToComputed)(dllList);
