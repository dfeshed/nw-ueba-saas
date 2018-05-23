import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isPolicyLoading,
  hasMissingRequiredData,
  currentPolicy
} from 'admin-source-management/reducers/policy/selector';

import {
  savePolicy,
  editPolicy
} from 'admin-source-management/actions/data-creators/policy';

const stateToComputed = (state) => ({
  policy: currentPolicy(state),
  isPolicyLoading: isPolicyLoading(state),
  hasMissingRequiredData: hasMissingRequiredData(state)
});

const dispatchToActions = (dispatch) => ({
  edit(field, value) {
    if (field && value !== undefined) {
      dispatch(editPolicy(field, value));
    }
  },
  save() {
    const onSuccess = () => {
      const transitionToPolicies = this.get('transitionToPolicies');
      transitionToPolicies();
    };
    dispatch(savePolicy(this.get('policy'), { onSuccess }));
  }
});

const UsmPolicy = Component.extend({
  tagName: 'hbox',
  classNames: ['usm-policy', 'flexi-fit'],

  actions: {
    handleNameChange(value) {
      this.send('edit', 'policy.name', value);
    },
    handleDescriptionChange(value) {
      this.send('edit', 'policy.description', value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPolicy);