import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => {
  return {
    riacEnabled: state.respond.riac.isRiacEnabled
  };
};

const TasksTabWrapper = Component.extend({

  accessControl: service(),

  classNames: ['tasks-tab-wrapper'],

  @computed(
    'riacEnabled',
    'accessControl.hasRiacRespondTasksAccess',
    'accessControl.hasRespondRemediationAccess'
  )
  show(riacEnabled, riacAc, rbacAc) {
    switch (riacEnabled) {
      case true:
        return riacAc;
      case false:
        return rbacAc;
      default:
        return false;
    }
  }
});

export default connect(stateToComputed)(TasksTabWrapper);