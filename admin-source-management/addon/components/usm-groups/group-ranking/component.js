import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  rankingSteps,
  isGroupLoading
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  rankingSteps: rankingSteps(state),
  isGroupLoading: isGroupLoading(state)
});

const GroupRanking = Component.extend({
  tagName: 'hbox',
  classNames: ['group-ranking'],

  // closure action expected to be passed in
  transitionToGroups: null

});

export default connect(stateToComputed)(GroupRanking);