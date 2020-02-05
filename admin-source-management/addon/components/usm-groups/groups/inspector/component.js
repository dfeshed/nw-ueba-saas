import Component from '@ember/component';
import { connect } from 'ember-redux';
import { computed } from '@ember/object';
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

  srcCountTooltip: computed(
    'focusedGroup.sourceCount',
    'focusedGroup.dirty',
    'focusedGroup.lastPublishedOn',
    function() {
      const i18n = this.get('i18n');
      return sourceCountTooltip(i18n, this.focusedGroup?.dirty, this.focusedGroup?.sourceCount, this.focusedGroup?.lastPublishedOn);
    }
  ),

  srcCount: computed('focusedGroup.sourceCount', function() {
    return getSourceCount(this.focusedGroup?.sourceCount);
  })
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsInspector);