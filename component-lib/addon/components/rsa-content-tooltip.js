import Ember from 'ember';
import layout from '../templates/components/rsa-content-tooltip';

const {
  Component,
  $,
  inject: {
    service
  },
  computed,
  run
} = Ember;

export default Component.extend({

  layout,

  eventBus: service(),

  classNames: ['rsa-content-tooltip'],

  style: 'standard', // ['standard', 'error', 'primary']

  position: 'right', // ['top', 'left', 'right', 'bottom']

  isDisplayed: false,

  triggerEvent: null,

  tooltipId: null,

  displayClose: computed.equal('triggerEvent', 'click'),

  targetClass: computed('tooltipId', function() {
    return `.${this.get('tooltipId')}`;
  }),

  attachment: computed('position', function() {
    let position = null;
    switch (this.get('position')) {
      case 'top':
        position = 'bottom center';
        break;
      case 'bottom':
        position = 'top center';
        break;
      case 'left':
        position = 'middle right';
        break;
      case 'right':
        position = 'middle left';
        break;
    }
    return position;
  }),

  ensureOnlyOneTether: computed('isDisplayed', {
    get() {
      return this.get('isDisplayed');
    },
    set(key, value) {
      run.schedule('afterRender', this, function() {
        if ($('.ember-tether').length > 1) {
          $('.ember-tether').first().remove();
        }
      });

      this.set('isDisplayed', value);
      return value;
    }
  }),

  didInsertElement() {
    run.schedule('afterRender', () => {
      this.get('eventBus').on(`rsa-content-tooltip-display-${this.get('tooltipId')}`, (triggerEvent) => {
        run.next(() => {
          if (triggerEvent) {
            this.set('triggerEvent', triggerEvent);
          }
          this.set('isDisplayed', true);
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-hide-${this.get('tooltipId')}`, (triggerEvent) => {
        run.next(() => {
          if (triggerEvent) {
            this.set('triggerEvent', triggerEvent);
          }
          this.set('isDisplayed', false);
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-toggle-${this.get('tooltipId')}`, (triggerEvent) => {
        run.next(() => {
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
