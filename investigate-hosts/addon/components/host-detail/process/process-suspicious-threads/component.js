import Component from '@ember/component';
import { connect } from 'ember-redux';
import { suspiciousThreadsData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-suspicious-threads-columns';
import { toggleSelectedProcessDllRow } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleSelectedProcessDllRow
};
const stateToComputed = (state) => ({
  threadList: suspiciousThreadsData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const suspiciousThreads = Component.extend({

  tagName: 'box',

  classNames: ['process-suspicious-threads-list'],

  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.send('toggleSelectedProcessDllRow', item);
      this.openProperties();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(suspiciousThreads);