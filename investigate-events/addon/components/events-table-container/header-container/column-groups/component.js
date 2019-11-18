import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed, { not } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import _ from 'lodash';

import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { profiles } from 'investigate-events/reducers/investigate/profile/selectors';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { selectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import {
  COLUMN_GROUPS_STATE_LOCATION as stateLocation,
  COLUMN_GROUPS_MODEL_NAME as modelName,
  COLUMN_GROUPS_LIST_NAME as listName,
  COLUMN_GROUPS_TOPIC_ID as topicId,
  COLUMN_GROUPS_MODULE_ID as moduleId
} from 'investigate-events/constants/columnGroups';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';
import { BASE_COLUMNS } from 'investigate-events/constants/OOTBColumnGroups';
import { isColumnGroupValid } from 'investigate-events/util/validations';

const dispatchToActions = {
  setColumnGroup,
  mapColumnGroupsForEventTable
};

const stateToComputed = (state) => ({
  selectedColumnGroupId: selectedColumnGroup(state),
  columnGroups: columnGroups(state),
  profiles: profiles(state)
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
      moduleId,
      topicId
    };
  },

  actions: {

    selectColumnGroup(columnGroup) {
      this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
      this.send('setColumnGroup', columnGroup);
    },

    columnGroupResponseMapping(columnGroup) {
      // add base columns but avoid duplicates if base columns included in response for `set columnGroup` from server
      columnGroup.columns = _.uniqBy([...BASE_COLUMNS, ...columnGroup.columns ], 'metaName');
      return mapColumnGroupsForEventTable([columnGroup])[0];
    },

    validateEditedColumnGroup(editedGroup) {
      const columnGroups = this.get('columnGroups') || [];
      return isColumnGroupValid(editedGroup, columnGroups);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ColumnGroups);
