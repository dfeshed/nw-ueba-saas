import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';

export default Component.extend({

  layout,

  classNames: ['rsa-preferences-panel'],
  classNameBindings: ['isExpanded'],
  launchFor: null,

  eventBus: service(),

  init() {
    this._super(arguments);

    this.get('eventBus').on('toggle-rsa-preferences-panel', (launchFor) => {
      this.set('launchFor', launchFor);
      this.toggleProperty('isExpanded');
    });
  },

  actions: {
    closePreferencesPanel() {
      this.set('isExpanded', false);
    }
  }
});
