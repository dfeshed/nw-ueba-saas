import Component from '@ember/component';
import layout from './template';
import lcColumnList from 'context/config/liveconnect-columns';
import computed, { or } from 'ember-computed-decorators';

const liveConnectTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
export default Component.extend({
  layout,
  lcColumnList,
  classNames: 'rsa-context-panel__liveconnect',

  @computed('activeTabName')
  showContextPanel: (activeTabName) => liveConnectTabs.includes(activeTabName),

  @or('model.contextData.LiveConnect-Ip_ERROR', 'model.contextData.LiveConnect-Domain_ERROR', 'model.contextData.LiveConnect-File_ERROR')
  liveConnectError: null,

  @computed('activeTabName')
  liveConnectDsDetails: (activeTabName) => {
    return { dataSourceGroup: activeTabName };
  }

});
