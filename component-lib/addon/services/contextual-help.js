import Ember from 'ember';

const {
  Service,
  inject: {
    service
  },
  computed,
  $
} = Ember;

export default Service.extend({
  appVersion: service(),
  i18n: service(),

  locale: computed.alias('i18n.locale'),
  version: computed.readOnly('appVersion.version'),

  urlBase: 'http://cms.netwitness.com/sadocs',

  respondModule: 'Respond',
  respondCardViewTopic: 'RespCardVw',
  respondListViewTopic: 'RespListView',
  respondDetailsViewTopic: 'RespDetView',
  respondPreferencesViewTopic: 'RespPrefView',

  investigateModule: 'Investigate',
  investigateQueryTopic: 'InvSubQry',
  investigateMetaPanelTopic: 'InvMetPnl',
  investigateEventsPanelTopic: 'InvEvtPnl',
  investigateEventReconTopic: 'InvEvtRec',
  investigateContextPanelTopic: 'InvCtxPnl',

  module: null,

  topic: null,

  buildURL(params) {
    const queryStr = $.param(params);
    return encodeURI(`${this.get('urlBase')}?${queryStr}`);
  },

  globalHelpUrl: computed('locale', 'version', 'module', 'topic', function() {
    return this.buildURL({
      locale: this.get('locale'),
      version: this.get('version'),
      module: this.get('module'),
      topic: this.get('topic')
    });
  }),

  generateUrl(module, topic) {
    return this.buildURL({
      locale: this.get('locale'),
      version: this.get('version'),
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
