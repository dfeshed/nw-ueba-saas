import Component from '@ember/component';
import { connect } from 'ember-redux';
import { imageHooksData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-image-hooks-columns';
import { toggleSelectedProcessDllRow, setDllRowSelectedId } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleSelectedProcessDllRow,
  setDllRowSelectedId
};
const stateToComputed = (state) => ({
  hookList: imageHooksData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig),
  selectedIndex: state.endpoint.process.selectedDllRowIndex,
  sid: state.endpointQuery.serverId
});

const imageHooksList = Component.extend({

  tagName: 'box',

  classNames: ['process-image-hooks-list'],

  actions: {
    toggleSelectedRow(item, index, e) {
      this.send('toggleSelectedProcessDllRow', item);
      this.openPropertyPanel();
      this.send('setDllRowSelectedId', index);
      e.stopPropagation();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(imageHooksList);
