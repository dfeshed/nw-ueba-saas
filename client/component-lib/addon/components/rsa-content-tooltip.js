import Ember from 'ember';
import layout from '../templates/components/rsa-content-tooltip';

export default Ember.Component.extend({

  layout,

  eventBus: Ember.inject.service(),

  classNames: ['rsa-content-tooltip'],

  style: 'standard', // ['standard', 'error', 'primary']

  position: 'right', // ['top', 'left', 'right', 'bottom']

  isDisplayed: false,

  triggerEvent: null,

  tooltipId: null,

  displayClose: Ember.computed.equal('triggerEvent', 'click'),

  targetClass: Ember.computed('tooltipId', function() {
    return `.${this.get('tooltipId')}`;
  }),

  attachment: Ember.computed('position', function() {
    switch (this.get('position')) {
      case 'top':
        return 'bottom center';
      case 'bottom':
        return 'top center';
      case 'left':
        return 'middle right';
      case 'right':
        return 'middle left';
    }
  }),

  ensureOnlyOneTether: Ember.observer('isDisplayed', function() {
    Ember.run.schedule('afterRender', this, function() {
      if (Ember.$('.ember-tether').length > 1) {
        Ember.$('.ember-tether').first().remove();
      }
    });
  }),

  didInsertElement() {
    Ember.run.schedule('afterRender', () => {
      this.get('eventBus').on(`rsa-content-tooltip-display-${this.get('tooltipId')}`, (triggerEvent) => {
        Ember.run.next(() => {
          if (triggerEvent) {
            this.set('triggerEvent', triggerEvent);
          }
          this.set('isDisplayed', true);
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-hide-${this.get('tooltipId')}`, (triggerEvent) => {
        Ember.run.next(() => {
          if (triggerEvent) {
            this.set('triggerEvent', triggerEvent);
          }
          this.set('isDisplayed', false);
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-toggle-${this.get('tooltipId')}`, (triggerEvent) => {
        Ember.run.next(() => {
          if (triggerEvent) {
            this.set('triggerEvent', triggerEvent);
          }
          this.toggleProperty('isDisplayed');
        });
      });
    });
  },

  actions: {
    toggleTooltip() {
      this.set('isDisplayed', false);
    }
  }
});
