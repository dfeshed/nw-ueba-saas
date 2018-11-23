import Component from '@ember/component';
import { connect } from 'ember-redux';
import { enrichedDllData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-dll-columns';
import { toggleSelectedProcessDllRow } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleSelectedProcessDllRow
};

const stateToComputed = (state) => ({
  dllList: enrichedDllData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dllList = Component.extend({

  tagName: 'box',

  classNames: ['process-dll-list'],

  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.send('toggleSelectedProcessDllRow', item);
      this.openProperties();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(dllList);
