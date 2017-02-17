import Ember from 'ember';
import layout from './template';

import endpointColumns from 'context/config/endpoint-columns';
import imColumns from 'context/config/im-columns';
import machineData from 'context/config/machines';
import userData from 'context/config/users';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel',
  datasourceList: endpointColumns.concat(imColumns),
  machineData,
  userData,

  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  }
});
