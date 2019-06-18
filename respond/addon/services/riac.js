import Service, { inject as service } from '@ember/service';
import computed, { alias } from 'ember-computed-decorators';
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

  @alias('accessControl.authorities.[]')
  authorities: [],

  @alias('accessControl.hasRespondAlertsAccess')
  _rbacAlertsAccess: null,

  @computed('authorities.[]', 'adminRoles')
  _riacAlertsAccess(authorities, adminRoles) {
    return authorities.some((authority) => adminRoles.includes(authority));
  },

  @alias('accessControl.hasRespondRemediationAccess')
  _rbacTasksAccess: null,

  @computed('authorities.[]', 'adminRoles')
  _riacTasksAccess(authorities, adminRoles) {
    return authorities.some((authority) => adminRoles.includes(authority));
  },

  @computed('authorities.[]', 'adminRoles')
  _riacChangeAssigneeAccess(authorities, adminRoles) {
    return authorities.some((authority) => adminRoles.includes(authority));
  },

  @computed('riacEnabled', '_riacAlertsAccess', '_rbacAlertsAccess')
  hasAlertsAccess(riacEnabled, riacAlertsAccess, rbacAlertsAccess) {
    switch (riacEnabled) {
      case true:
        return riacAlertsAccess;
      case false:
        return rbacAlertsAccess;
      default:
        return false;
    }
  },

  @computed('riacEnabled', '_riacTasksAccess', '_rbacTasksAccess')
  hasTasksAccess(riacEnabled, riacTasksAccess, rbacTasksAccess) {
    switch (riacEnabled) {
      case true:
        return riacTasksAccess;
      case false:
        return rbacTasksAccess;
      default:
        return false;
    }
  },

  @computed('riacEnabled', '_riacChangeAssigneeAccess')
  canChangeAssignee(riacEnabled, riacAc) {
    if (riacEnabled) {
      return riacAc;
    } else {
      return true;
    }
  },

  @alias('accessControl.hasRespondIncidentsAccess')
  hasIncidentsAccess: null

});

export default connect(stateToComputed)(RiacService);