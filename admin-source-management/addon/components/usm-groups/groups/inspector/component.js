import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import {
  focusedGroup,
  focusedGroupCriteria
} from 'admin-source-management/reducers/usm/group-details/group-selectors';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedGroup: focusedGroup(state),
  focusedGroupCriteria: focusedGroupCriteria(state)
});

const UsmGroupsInspector = Component.extend({
  classNames: ['usm-groups-inspector'],

  i18n: service(),

  @computed('focusedGroup.sourceCount', 'focusedGroup.dirty', 'focusedGroup.lastPublishedOn')
  srcCountTooltip(sourceCount, isDirty, lastPublishedOn) {
    const i18n = this.get('i18n');
    return sourceCountTooltip(i18n, isDirty, sourceCount, lastPublishedOn);
  },

  @computed('focusedGroup.sourceCount')
  srcCount(sourceCount) {
    return getSourceCount(sourceCount);
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsInspector);