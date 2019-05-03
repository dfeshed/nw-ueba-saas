import Component from '@ember/component';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['power-select-tabs'],

  /**
   * Currently active tab
   */
  activePillTab: undefined,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  actions: {
    switchTabs(tab) {
      if (this.get('activePillTab') !== tab) {
        this.get('sendMessage')(MESSAGE_TYPES.AFTER_OPTIONS_TAB_CLICKED);
      }
    }
  }
});