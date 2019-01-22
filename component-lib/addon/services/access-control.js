import computed, { intersect, gt, or } from 'ember-computed-decorators';
import config from 'ember-get-config';
import Service from '@ember/service';

export default Service.extend({
  roles: config.roles || [],
  authorities: config.authorities || [], // jwt decoded netwitness user "roles" (as opposed to permissions)
  // static permissions
  hasMonitorAccess: true,

  // role permission lists

  adminRoles: [
    '*',
    'accessAdminModule',
    'viewAppliances',
    'viewServices',
    'viewEventSources',
    'viewUnifiedSources',
    'accessHealthWellness',
    'manageSystemSettings',
    'manageSASecurity'
  ],
  configRoles: [
    '*',
    'searchLiveResources',
    'accessManageAlertHandlingRules',
    'accessViewRules',
    'manageLiveResources',
    'manageLiveFeeds'
  ],
  investigationEmberRoles: [
    '*',
    'investigate-server.configuration.manage',
    'investigate-server.logs.manage',
    'investigate-server.security.read',
    'investigate-server.process.manage',
    'investigate-server.health.read',
    'investigate-server.*',
    'investigate-server.security.manage',
    'investigate-server.metrics.read',
    'investigate-server.event.read',
    'investigate-server.content.export',
    'investigate-server.content.reconstruct',
    'investigate-server.predicate.read',
    'endpoint-server.agent.read'
  ],
  investigationClassicRoles: [
    '*',
    'accessInvestigationModule',
    'manageContextList',
    'contextLookup',
    'navigateDevices',
    'navigateCreateIncidents',
    'navigateEvents'
  ],

  // computed intersections between roles and role groups

  @intersect('adminRoles', 'roles') adminAccessIntersections: null,
  @intersect('configRoles', 'roles') configAccessIntersections: null,
  @intersect('investigationClassicRoles', 'roles') investigateClassicAccessIntersections: null,
  @intersect('investigationEmberRoles', 'roles') investigateEmberAccessIntersections: null,

  // permissions derived from roles returned by admin server

  @gt('investigateClassicAccessIntersections.length', 0) hasInvestigateClassicAccess: null,
  @gt('investigateEmberAccessIntersections.length', 0) hasInvestigateEmberAccess: null,
  @gt('adminAccessIntersections.length', 0) hasAdminAccess: null,
  @gt('configAccessIntersections.length', 0) hasConfigAccess: null,
  @or('hasInvestigateClassicAccess', 'hasInvestigateEmberAccess') hasInvestigateAccess: null,

  @computed('roles.[]')
  hasInvestigateEventsAccess(roles) {
    // This isn't specifically whether or not they can submit queries, but it is
    // whether or not they can read/write predicates (aka hashes) for queries
    // into mongo. If they cannot read/write predicates, they can't really use
    // investigate properly, so we simply do not allow them access.
    const canSubmitQueries = this._hasPermission(roles, 'investigate-server.predicate.read');
    const canSeeEvents = this._hasPermission(roles, 'investigate-server.event.read');
    const hasInvestigationModule = this._hasPermission(roles, 'accessInvestigationModule');
    return canSeeEvents && canSubmitQueries && hasInvestigationModule;
  },

  @computed('roles.[]')
  hasInvestigateContentExportAccess(roles) {
    return this._hasPermission(roles, 'investigate-server.content.export');
  },

  @computed('roles.[]')
  hasReconAccess(roles) {
    return this._hasPermission(roles, 'investigate-server.content.reconstruct');
  },

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
  hasRespondAlertRulesAccess(roles) {
    return this._hasPermission(roles, 'respond-server.alertrule');
  },

  @computed('roles.[]')
  hasRespondNotificationsAccess(roles) {
    return this._hasPermission(roles, 'integration-server.notification') &&
      this._hasPermission(roles, 'respond-server.notification');
  },

  @computed('roles.[]')
  respondCanManageNotifications(roles) {
    return this._hasPermission(roles, 'integration-server.notification.manage') &&
      this._hasPermission(roles, 'respond-server.notification.manage');
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

  // Begin Endpoint permissions

  @computed('roles.[]')
  hasInvestigateHostsAccess(roles) {
    // this permission is same for both hosts and files
    const hasEndpointMachineRead = this._hasPermission(roles, 'endpoint-server.agent.read');
    const hasInvestigationModule = this._hasPermission(roles, 'accessInvestigationModule');
    return hasInvestigationModule && hasEndpointMachineRead;
  },

  @computed('roles.[]')
  endpointCanManageFiles(roles) {
    return this._hasPermission(roles, 'endpoint-server.agent.manage');
  },

  @computed('roles.[]')
  endpointCanManageFilter(roles) {
    return this._hasPermission(roles, 'endpoint-server.filter.manage');
  },
  // Begin Configure Permissions

  @computed('roles.[]')
  hasLiveSearchAccess(roles) {
    return this._hasPermission(roles, 'searchLiveResources');
  },

  @computed('roles.[]')
  hasESARulesAccess(roles) {
    return this._hasPermission(roles, 'accessViewRules');
  },

  @computed('roles.[]')
  hasLiveResourcesAccess(roles) {
    return this._hasPermission(roles, 'manageLiveResources');
  },

  @computed('roles.[]')
  hasLiveFeedsAccess(roles) {
    return this._hasPermission(roles, 'manageLiveFeeds');
  },

  @computed('roles.[]')
  hasLogParsersAccess(roles) {
    return this._hasPermission(roles, 'content-server.logparser');
  },

  @computed('roles.[]')
  canManageLogParsers(roles) {
    return this._hasPermission(roles, 'content-server.logparser.manage');
  },

  @computed('roles.[]')
  hasSourceServerGroupAccess(roles) {
    return this._hasPermission(roles, 'source-server.group');
  },

  @computed('roles.[]')
  canManageSourceServerGroups(roles) {
    return this._hasPermission(roles, 'source-server.group.manage');
  },

  @computed('roles.[]')
  hasSourceServerPolicyAccess(roles) {
    return this._hasPermission(roles, 'source-server.policy');
  },

  @computed('roles.[]')
  canManageSourceServerPolicies(roles) {
    return this._hasPermission(roles, 'source-server.policy.manage');
  },

  // End Configure Permissions

  // Begin Admin Permissions

  @computed('roles.[]')
  hasAdminViewAppliancesAccess(roles) {
    return this._hasPermission(roles, 'viewAppliances');
  },

  @computed('roles.[]')
  hasAdminViewServicesAccess(roles) {
    return this._hasPermission(roles, 'viewServices');
  },

  @computed('roles.[]')
  hasAdminViewEventSourcesAccess(roles) {
    return this._hasPermission(roles, 'viewEventSources');
  },

  @computed('roles.[]')
  hasAdminViewUnifiedSourcesAccess(roles) {
    return this._hasPermission(roles, 'viewUnifiedSources');
  },

  @computed('roles.[]')
  hasAdminHealthWellnessAccess(roles) {
    return this._hasPermission(roles, 'accessHealthWellness');
  },

  @computed('roles.[]')
  hasAdminSystemSettingsAccess(roles) {
    return this._hasPermission(roles, 'manageSystemSettings');
  },

  @computed('roles.[]')
  hasAdminSASecurityAccess(roles) {
    return this._hasPermission(roles, 'manageSASecurity');
  },

  // End Admin Permissions

  @computed('authorities.[]')
  hasUEBAAccess(authorities = []) {
    return authorities.includes('Administrators') || authorities.includes('UEBA_Analysts');
  },

  @computed('hasInvestigateAccess', 'hasInvestigateEmberAccess', 'hasInvestigateClassicAccess')
  investigateUrl: (hasInvestigateAccess, hasInvestigateEmberAccess, hasInvestigateClassicAccess) => {
    let url = null;

    if (hasInvestigateAccess) {
      if (hasInvestigateEmberAccess) {
        url = '/investigate';
      } else if (hasInvestigateClassicAccess) {
        url = '/investigation';
      }
    }

    return url;
  },

  @computed('adminAccessIntersections.[]')
  adminUrl: (intersections) => {
    let url = null;

    if (intersections.includes('viewServices') || intersections.includes('*')) {
      url = '/admin/services';
    } else if (intersections.includes('viewAppliances')) {
      url = '/admin/appliances';
    } else if (intersections.includes('viewEventSources')) {
      url = '/admin/eventsources';
    } else if (intersections.includes('viewUnifiedSources')) {
      url = '/admin/usm';
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
      url = '/configure/respond/incident-rules';
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
