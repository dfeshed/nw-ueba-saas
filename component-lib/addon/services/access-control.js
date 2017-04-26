import computed, { intersect, gt } from 'ember-computed-decorators';
import config from 'ember-get-config';
import Service from 'ember-service';

export default Service.extend({

  // all roles returned by admin server
  roles: config.roles || [],

  // static permissions

  hasMonitorAccess: true,

  // role permission lists

  adminRoles: ['*', 'accessAdminModule', 'viewAppliances', 'viewServices', 'viewEventSources', 'accessHealthWellness', 'manageSystemSettings', 'manageSASecurity'],
  configRoles: ['*', 'searchLiveResources', 'accessManageAlertHandlingRules', 'accessViewRules', 'manageLiveResources', 'manageLiveFeeds'],
  investigationRoles: ['*', 'accessInvestigationModule', 'investigate-server.*'],
  respondRoles: ['*', 'accessIncidentModule', 'response-server.*'],

  // computed intersections between roles and role groups

  @intersect('adminRoles', 'roles') adminAccessIntersections: null,
  @intersect('configRoles', 'roles') configAccessIntersections: null,
  @intersect('investigationRoles', 'roles') investigateAccessIntersections: null,
  @intersect('respondRoles', 'roles') respondAccessIntersections: null,

  // permissions derived from roles returned by admin server

  @gt('adminAccessIntersections.length', 0) hasAdminAccess: null,
  @gt('configAccessIntersections.length', 0) hasConfigAccess: null,
  @gt('investigateAccessIntersections.length', 0) hasInvestigateAccess: null,
  @gt('respondAccessIntersections.length', 0) hasRespondAccess: null,

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
  }

});
