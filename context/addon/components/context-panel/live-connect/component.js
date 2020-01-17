import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import lcColumnList from 'context/config/liveconnect-columns';
import { or } from '@ember/object/computed';

const liveConnectTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
export default Component.extend({
  layout,
  lcColumnList,
  classNames: 'rsa-context-panel__liveconnect',

  showContextPanel: computed('activeTabName', function() {
    return liveConnectTabs.includes(this.activeTabName);
  }),

  liveConnectError: or(
    'model.contextData.LiveConnect-Ip_ERROR',
    'model.contextData.LiveConnect-Domain_ERROR',
    'model.contextData.LiveConnect-File_ERROR'
  ),

  liveConnectDsDetails: computed('activeTabName', function() {
    return { dataSourceGroup: this.activeTabName };
  })

});
