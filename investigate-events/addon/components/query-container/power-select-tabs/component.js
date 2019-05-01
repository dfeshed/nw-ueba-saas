import Component from '@ember/component';

export default Component.extend({
  classNames: ['power-select-tabs'],

  activeTab: 'meta',
  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  actions: {
    switchTabs(tab) {
      this.set('activeTab', tab);
    }
  }
});