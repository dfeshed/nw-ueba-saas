import { computed } from '@ember/object';
import Service, { inject as service } from '@ember/service';
import { alias } from '@ember/object/computed';
import { connect } from 'ember-redux';
import { isRiacEnabled, getAdminRoles } from 'respond/selectors/riac';

const stateToComputed = (state) => {
  return {
    riacEnabled: isRiacEnabled(state),
    adminRoles: getAdminRoles(state)
  };
};

const RiacService = Service.extend({

  accessControl: service(),
  authorities: alias('accessControl.authorities.[]'),
  _rbacAlertsAccess: alias('accessControl.hasRespondAlertsAccess'),

  _riacAlertsAccess: computed('authorities.[]', 'adminRoles', function() {
    return this.authorities.some((authority) => this.adminRoles.includes(authority));
  }),

  _rbacTasksAccess: alias('accessControl.hasRespondRemediationAccess'),

  _riacTasksAccess: computed('authorities.[]', 'adminRoles', function() {
    return this.authorities.some((authority) => this.adminRoles.includes(authority));
  }),

  _riacChangeAssigneeAccess: computed('authorities.[]', 'adminRoles', function() {
    return this.authorities.some((authority) => this.adminRoles.includes(authority));
  }),

  hasAlertsAccess: computed('riacEnabled', '_riacAlertsAccess', '_rbacAlertsAccess', function() {
    switch (this.riacEnabled) {
      case true:
        return this._riacAlertsAccess;
      case false:
        return this._rbacAlertsAccess;
      default:
        return false;
    }
  }),

  hasTasksAccess: computed('riacEnabled', '_riacTasksAccess', '_rbacTasksAccess', function() {
    switch (this.riacEnabled) {
      case true:
        return this._riacTasksAccess;
      case false:
        return this._rbacTasksAccess;
      default:
        return false;
    }
  }),

  canChangeAssignee: computed('riacEnabled', '_riacChangeAssigneeAccess', function() {
    if (this.riacEnabled) {
      return this._riacChangeAssigneeAccess;
    } else {
      return true;
    }
  }),

  hasIncidentsAccess: alias('accessControl.hasRespondIncidentsAccess')
});

export default connect(stateToComputed)(RiacService);