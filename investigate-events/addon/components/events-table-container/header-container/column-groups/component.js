import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed, { not } from 'ember-computed-decorators';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { selectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { inject as service } from '@ember/service';
import {
  COLUMN_GROUPS_STATE_LOCATION as stateLocation,
  COLUMN_GROUPS_MODEL_NAME as modelName,
  COLUMN_GROUPS_LIST_NAME as listName
} from 'investigate-events/constants/columnGroups';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';
import { BASE_COLUMNS } from 'investigate-events/constants/OOTBColumnGroups';

const dispatchToActions = {
  setColumnGroup,
  mapColumnGroupsForEventTable
};

const stateToComputed = (state) => ({
  selectedColumnGroupId: selectedColumnGroup(state),
  columnGroups: columnGroups(state)
});

const ColumnGroups = Component.extend({
  classNames: ['rsa-investigate-events-table__header__columnGroups'],
  eventBus: service(),
  accessControl: service(),
  modelName,
  listName,
  stateLocation,
  @not('accessControl.hasInvestigateColumnGroupsAccess') isDisabled: true,
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

    columnGroupResponseMapping(columnGroup) {
      columnGroup.columns = [...BASE_COLUMNS, ...columnGroup.columns ];
      return mapColumnGroupsForEventTable([columnGroup])[0];
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(ColumnGroups);
