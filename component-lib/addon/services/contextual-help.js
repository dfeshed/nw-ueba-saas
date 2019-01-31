import Service, { inject as service } from '@ember/service';
import $ from 'jquery';
import computed, { alias, readOnly } from 'ember-computed-decorators';

export default Service.extend({
  appVersion: service(),

  @alias('i18n.locale') locale: null,
  @readOnly @alias('appVersion.version') version: null,

  investigateModule: 'investigation',
  invEventAnalysis: 'invEventAnalysis',
  invEventPreferences: 'investigateEventPreferences',
  invFileAnalysis: 'invFileAnalysis',
  invPacketAnalysis: 'invPacketAnalysis',
  invTextAnalysis: 'invTextAnalysis',
  invEndpointFileAnalysis: 'invAnalysis',
  invEndpointCertificates: 'invCertificates',

  invHosts: 'hosts',
  invFiles: 'files',
  invHostsOverview: 'Overviewhosts',
  invHostsProcess: 'Processhosts',
  invHostsAutoruns: 'Autorunshosts',
  invHostsFiles: 'Fileshosts',
  invHostsDrivers: 'Drivershosts',
  invHostsLibraries: 'Librarieshosts',
  invHostsAnomalies: 'Anomalieshosts',
  invHostsSysInfo: 'SysInfohosts',

  respondModule: 'respond',
  respAlrtListVw: 'respAlrtListVw',
  respAlrtDetailVw: 'respAlrtDetailVw',
  respIncListVw: 'respIncListVw',
  respIncDetailVw: 'respIncDetailVw',
  respRemTasksVw: 'respRemTasksVw',
  incRulesListVw: 'incRulesListVw',
  incRulesDetailVw: 'incRulesDetailVw',
  respNotifSetVw: 'respNotifSetVw',

  contentModule: 'configure',
  contentOverview: 'logDeviceParserRulePage',

  usmModule: 'configure',
  usmGroups: 'usmGroupsTab',
  usmGroupsWizard: 'usmCreateEPGroups',
  usmGroupsWizardRanking: 'usmEditRanking',
  usmPolicies: 'usmPoliciesTab',
  usmPoliciesWizard: 'usmCreateEPPolicies',

  module: null,
  topic: null,

  urlBase: 'https://cms.netwitness.com/sadocs',

  buildURL(params) {
    const queryStr = $.param(params);
    return encodeURI(`${this.get('urlBase')}?${queryStr}`);
  },

  /* eslint-disable object-shorthand */
  @computed('locale', 'version', 'module', 'topic')
  globalHelpUrl: function(locale, version, module, topic) {
    return this.buildURL({
      locale,
      version: version.split('+')[0],
      module,
      topic
    });
  },
  /* eslint-enable object-shorthand */

  generateUrl(module, topic) {
    return this.buildURL({
      locale: this.get('locale'),
      version: this.get('version').split('+')[0],
      module,
      topic
    });
  },

  goToGlobalHelp() {
    window.open(this.get('globalHelpUrl'), '_blank').focus();
  },

  goToHelp(module, topic) {
    window.open(this.generateUrl(module, topic), '_blank').focus();
  }

});
