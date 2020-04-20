import Component from '@ember/component';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['power-select-tabs'],

  queryCounter: service(),

  /**
   * Currently active tab
   */
  activePillTab: undefined,

  @computed('queryCounter.recentQueryTabCount')
  recentTabCount(queryCount) {
    if (queryCount >= 100) {
      return '100+';
    }
    return `${queryCount}`;
  },

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