import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => {
  return {
    riacEnabled: state.respond.riac.isRiacEnabled
  };
};

const AlertsTabWrapper = Component.extend({

  accessControl: service(),

  classNames: ['alerts-tab-wrapper'],

  @computed(
    'riacEnabled',
    'accessControl.hasRiacRespondAlertsAccess',
    'accessControl.hasRespondAlertsAccess'
  )
  show(riacEnabled, riacAc, rbacAc) {
    return riacEnabled ? riacAc : rbacAc;
  }
});

export default connect(stateToComputed)(AlertsTabWrapper);