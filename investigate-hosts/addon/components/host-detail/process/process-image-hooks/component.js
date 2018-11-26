import Component from '@ember/component';
import { connect } from 'ember-redux';
import { imageHooksData } from 'investigate-hosts/reducers/details/process/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './process-image-hooks-columns';
import { toggleSelectedProcessDllRow } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleSelectedProcessDllRow
};
const stateToComputed = (state) => ({
  hookList: imageHooksData(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const imageHooksList = Component.extend({

  tagName: 'box',

  classNames: ['process-image-hooks-list'],

  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.send('toggleSelectedProcessDllRow', item);
      this.openProperties();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(imageHooksList);
