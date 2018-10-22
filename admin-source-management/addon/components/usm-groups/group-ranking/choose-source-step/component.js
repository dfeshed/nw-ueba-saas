import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  availablePolicySourceTypes,
  selectedSourceType,
  groupRankingStatus
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  fetchGroupRanking
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  availablePolicySourceTypes: availablePolicySourceTypes(state),
  selectedSourceType: selectedSourceType(state),
  groupRankingStatus: groupRankingStatus(state)
});
const dispatchToActions = {
  fetchGroupRanking
};

const ChooseSourceStep = Component.extend({
  tagName: 'vbox',
  classNames: 'choose-source-step'
});
export default connect(stateToComputed, dispatchToActions)(ChooseSourceStep);
