import Component from '@ember/component';
import { connect } from 'ember-redux';
import { enrichedDllData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-dll-columns';
import { toggleSelectedProcessDllRow, setDllRowSelectedId } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleSelectedProcessDllRow,
  setDllRowSelectedId
};

const stateToComputed = (state) => ({
  dllList: enrichedDllData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig),
  selectedIndex: state.endpoint.process.selectedDllRowIndex
});

const dllList = Component.extend({

  tagName: 'box',

  classNames: ['process-dll-list'],

  actions: {
    toggleSelectedRow(item, index, e) {
      this.send('toggleSelectedProcessDllRow', item);
      this.send('setDllRowSelectedId', index);
      this.openProperties();
      e.stopPropagation();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(dllList);
