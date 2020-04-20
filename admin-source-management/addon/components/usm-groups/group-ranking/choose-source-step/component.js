import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  availablePolicySourceTypes,
  enabledPolicySourceTypesAsObjs,
  selectedSourceType,
  selectedSourceTypeAsObj,
  groupRankingStatus
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  fetchGroupRanking
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => {
  const policySourceTypesAsObjs = enabledPolicySourceTypesAsObjs(availablePolicySourceTypes(state));
  return {
    availablePolicySourceTypes: policySourceTypesAsObjs,
    selectedSourceType: selectedSourceTypeAsObj(policySourceTypesAsObjs, selectedSourceType(state)),
    groupRankingStatus: groupRankingStatus(state)
  };
};

const dispatchToActions = {
  fetchGroupRanking
};

const ChooseSourceStep = Component.extend({
  tagName: 'vbox',
  classNames: 'choose-source-step',

  actions: {
    handleSourceTypeChange(value) {
      // power-select passes the whole object, we only want the policy type
      this.send('fetchGroupRanking', value.policyType);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ChooseSourceStep);
