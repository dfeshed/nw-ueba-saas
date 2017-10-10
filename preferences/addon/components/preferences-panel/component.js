import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';

export default Component.extend({

  layout,

  classNames: ['rsa-preferences-panel'],
  classNameBindings: ['isExpanded'],
  source: null,

  request: service(),
  eventBus: service(),

  init() {
    this._super(arguments);

    this.get('eventBus').on(`rsa-preferences-panel-${this.source}-toggled`, () => {
      this.toggleProperty('isExpanded');
    });
  },

  actions: {
    closePreferencesPanel() {
      this.set('isExpanded', false);
    }
  }
});
