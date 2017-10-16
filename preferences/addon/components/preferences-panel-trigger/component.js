import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';

export default Component.extend({
  layout,
  classNames: ['rsa-preferences-panel-trigger'],
  eventBus: service(),

  launchFor: null,

  actions: {
    /**
     * Opens/Closes the preferences panel
     * @public
     */
    toggleSettingsPanel() {
      this.get('eventBus').trigger('toggle-rsa-preferences-panel', this.get('launchFor'));
    }
  }
});
