import Component from '@ember/component';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import layout from './template';

export default Component.extend({

  layout,

  tagName: 'page',

  eventBus: service(),

  layoutService: service('layout'),

  classNames: ['rsa-application-content'],

  classNameBindings: ['hasBlur'],

  hasBlur: false,

  addModalBlur() {
    run.next(() => {
      this.set('hasBlur', true);
    });
  },

  removeModalBlur() {
    run.next(() => {
      this.set('hasBlur', false);
    });
  },

  togglePanelBlur() {
    run.next(() => {
      const userPreferencesActive = this.get('layoutService.userPreferencesActive');
      const incidentQueueActive = this.get('layoutService.incidentQueueActive');

      if (userPreferencesActive || incidentQueueActive) {
        this.set('hasBlur', true);
      } else if (!userPreferencesActive && !incidentQueueActive) {
        this.set('hasBlur', false);
      }
    });
  },

  listen() {
    this.get('eventBus').on('rsa-application-modal-did-open', this, 'addModalBlur');
    this.get('eventBus').on('rsa-application-modal-did-close', this, 'removeModalBlur');
    this.get('eventBus').on('rsa-application-user-preferences-panel-will-toggle', this, 'togglePanelBlur');
    this.get('eventBus').on('rsa-application-incident-queue-panel-will-toggle', this, 'togglePanelBlur');
  },

  click(event) {
    this.get('eventBus').trigger('rsa-application-click', event.target);
  },

  init() {
    this.listen();
    this._super(arguments);
  }

});
