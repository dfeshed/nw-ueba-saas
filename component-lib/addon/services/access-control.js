import computed, { intersect, gt } from 'ember-computed-decorators';
import config from 'ember-get-config';
import Service from 'ember-service';

export default Service.extend({
  roles: config.roles || [],
  // static permissions
  hasMonitorAccess: true,

  // role permission lists

  adminRoles: ['*', 'accessAdminModule', 'viewAppliances', 'viewServices', 'viewEventSources', 'accessHealthWellness', 'manageSystemSettings', 'manageSASecurity'],
  configRoles: ['*', 'searchLiveResources', 'accessManageAlertHandlingRules', 'accessViewRules', 'manageLiveResources', 'manageLiveFeeds'],
  investigationRoles: ['*', 'accessInvestigationModule', 'investigate-server.*'],

  // computed intersections between roles and role groups

  @intersect('adminRoles', 'roles') adminAccessIntersections: null,
  @intersect('configRoles', 'roles') configAccessIntersections: null,
  @intersect('investigationRoles', 'roles') investigateAccessIntersections: null,

  // permissions derived from roles returned by admin server

  @gt('adminAccessIntersections.length', 0) hasAdminAccess: null,
  @gt('configAccessIntersections.length', 0) hasConfigAccess: null,
  @gt('investigateAccessIntersections.length', 0) hasInvestigateAccess: null,

  // Begin respond access permissions

  @computed('roles.[]')
  hasRespondAccess(roles) {
    return this._hasPermission(roles, 'respond-server');
  },

  @computed('roles.[]')
  hasRespondAlertsAccess(roles) {
    return this._hasPermission(roles, 'respond-server.alert');
  },

  @computed('roles.[]')
  hasRespondIncidentsAccess(roles) {
    return this._hasPermission(roles, 'respond-server.incident');
  },

  @computed('roles.[]')
  hasRespondRemediationAccess(roles) {
    return this._hasPermission(roles, 'respond-server.remediation');
  },

  @computed('roles.[]')
  hasRespondConfigureAccess(roles) {
    return this._hasPermission(roles, 'respond-server.alertrule');
  },

  @computed('roles.[]')
  respondCanManageIncidents(roles) {
    return this._hasPermission(roles, 'respond-server.incident.manage');
  },

  @computed('roles.[]')
  respondCanDeleteIncidents(roles) {
    return this._hasPermission(roles, 'respond-server.incident.delete');
  },

  @computed('roles.[]')
  respondCanManageAlerts(roles) {
    return this._hasPermission(roles, 'respond-server.alert.manage');
  },

  @computed('roles.[]')
  respondCanManageAlertRules(roles) {
    return this._hasPermission(roles, 'respond-server.alertrule.manage');
  },

  @computed('roles.[]')
  respondCanDeleteAlerts(roles) {
    return this._hasPermission(roles, 'respond-server.alert.delete');
  },

  @computed('roles.[]')
  respondCanManageRemediation(roles) {
    return this._hasPermission(roles, 'respond-server.remediation.manage');
  },

  @computed('roles.[]')
  hasRespondJournalAccess(roles) {
    return this._hasPermission(roles, 'respond-server.journal');
  },

  @computed('roles.[]')
  respondCanManageJournal(roles) {
    return this._hasPermission(roles, 'respond-server.journal.manage');
  },

  // End respond access permissions

  @computed('adminAccessIntersections.[]')
  adminUrl: (intersections) => {
    let url = null;

    if (intersections.includes('viewServices') || intersections.includes('*')) {
      url = '/admin/services';
    } else if (intersections.includes('viewAppliances')) {
      url = '/admin/appliances';
    } else if (intersections.includes('viewEventSources')) {
      url = '/admin/eventsources';
    } else if (intersections.includes('accessHealthWellness')) {
      url = '/admin/monitoring';
    } else if (intersections.includes('manageSystemSettings')) {
      url = '/admin/system';
    } else if (intersections.includes('manageSASecurity') || intersections.includes('security-server')) {
      url = '/admin/security';
    }

    return url;
  },

  @computed('configAccessIntersections.[]')
  configUrl: (intersections) => {
    let url = null;

    if (intersections.includes('searchLiveResources') || intersections.includes('*')) {
      url = '/live/search';
    } else if (intersections.includes('accessManageAlertHandlingRules')) {
      url = '/incident/configuration';
    } else if (intersections.includes('accessViewRules')) {
      url = '/alerting/configure';
    } else if (intersections.includes('manageLiveResources')) {
      url = '/live/manage';
    } else if (intersections.includes('manageLiveFeeds')) {
      url = '/live/manage';
    }

    return url;
  },

  /**
   * Check if permissions match a given string
   * @param roles The permissions to check against
   * @param {string} stringToMatch The string to match
   * @param {boolean} initialRun This is true on the first runthrough, and false afterward
   * @returns {boolean}
   * @private
   */
  _hasPermission(roles, stringToMatch) {
    // If you have the global '*' permission, you should have all permissions, so return true
    if (roles.includes('*')) {
      return true;
    }

    const hasPermission = roles.find(
      (role) => {
        return role.includes(stringToMatch);
      });

    // Check for exact permission matches
    if (hasPermission) {
      return true;
    } else {
      stringToMatch = stringToMatch.replace('.*', '');
    }

    // Check for match to parent '.*'
    if (stringToMatch.includes('.')) {
      stringToMatch = `${stringToMatch.substr(0, stringToMatch.lastIndexOf('.'))}.*`;
      return this._hasPermission(roles, stringToMatch);
    }

    return false;
  }
});
