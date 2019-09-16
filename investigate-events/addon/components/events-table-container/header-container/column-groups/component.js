import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { createColumnGroup,
  updateColumnGroup,
  deleteColumnGroup
} from 'investigate-events/actions/column-group';
import { inject as service } from '@ember/service';
import {
  COLUMN_GROUPS_LIST_LOCATION as listLocation,
  COLUMN_GROUPS_LIST_NAME as listName
} from 'investigate-events/constants/columnGroups';

const dispatchToActions = {
  setColumnGroup,
  createColumnGroup,
  deleteColumnGroup,
  updateColumnGroup
};

const stateToComputed = (state) => ({
  selectedColumnGroup: getSelectedColumnGroup(state),
  columnGroups: state.investigate.columnGroup.columnGroups
});

const ColumnGroups = Component.extend({
  classNames: ['rsa-investigate-events-table__header__columnGroups'],
  eventBus: service(),
  listName,
  listLocation,

  @computed()
  helpId() {
    return {
      moduleId: 'investigation',
      topicId: 'eaColumnGroups'
    };
  },

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
      const columns = [{
        metaName: 'time',
        displayName: 'Collection Time',
        position: 0,
        width: 175
      },
      {
        metaName: 'service',
        displayName: 'Service Name',
        position: 1,
        width: 100
      }];
      this.send('createColumnGroup', { name, columns });
    },
    deleteOneColumnGroup(columnGroup) {
      if (!columnGroup.ootb) {
        this.send('deleteColumnGroup', columnGroup.id);
      }
    },
    updateOneColumnGroup(columnGroup) {
      /*
        hardcoded values for column group properties
        Nehal to replace with values set by user
      */
      const name = `UPDATED-${Date.now().toString().substring(5)}`;
      const columns = [{
        metaName: 'time',
        displayName: 'Collection Time',
        position: 0,
        width: 175
      },
      {
        metaName: 'service',
        displayName: 'Service Name',
        position: 1,
        width: 100
      }];

      this.send('updateColumnGroup', { name, columns, id: columnGroup.id });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ColumnGroups);
