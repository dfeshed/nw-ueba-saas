import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { createColumnGroup, deleteColumnGroup } from 'investigate-events/actions/column-group';
import { inject as service } from '@ember/service';

const dispatchToActions = {
  setColumnGroup,
  createColumnGroup,
  deleteColumnGroup
};

const stateToComputed = (state) => ({
  selectedColumnGroup: getSelectedColumnGroup(state),
  columnGroups: state.investigate.columnGroup.columnGroups
});

const ColumnGroups = Component.extend({
  classNames: ['rsa-investigate-events-table__header__columnGroups'],
  eventBus: service(),
  actions: {
    selectColumnGroup(columnGroup) {
      this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
      this.send('setColumnGroup', columnGroup);
    },
    createNewColumnGroup() {
      /*
        hardcoded values for column group properties
        Nehal to replace with values set by user
      */
      const name = `TEST-${Date.now().toString().substring(5)}`;
      const fields = [{
        field: 'time',
        title: 'Collection Time',
        position: 0,
        width: 175
      },
      {
        'field': 'service',
        'title': 'Service Name',
        'position': 1,
        'width': 100
      }];
      this.send('createColumnGroup', { name, fields });
    },
    deleteOneColumnGroup(columnGroup) {
      if (!columnGroup.ootb) {
        this.send('deleteColumnGroup', columnGroup.id);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ColumnGroups);
