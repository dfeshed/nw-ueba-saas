import Ember from 'ember';
import layout from '../templates/components/rsa-content-tooltip';

const {
  Component,
  $,
  inject: {
    service
  },
  run: {
    later
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

  tooltipId: null,

  isHovering: false,

  hideDelay: 500,

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
      this.get('eventBus').on(`rsa-content-tooltip-display-${this.get('tooltipId')}`, () => {
        run.next(() => {
          this.set('isDisplayed', true);

          run.schedule('afterRender', () => {
            $(`.${this.get('elementId')}`).on('mouseenter', () => {
              this.set('isHovering', true);
            });
            $(`.${this.get('elementId')}`).on('mouseleave', () => {
              this.set('isHovering', false);
              later(() => {
                if (!this.get('isHovering')) {
                  this.set('isDisplayed', false);
                }
              }, this.get('hideDelay'));
            });
          });
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-hide-${this.get('tooltipId')}`, () => {
        run.next(() => {
          if (!this.get('isHovering')) {
            $(`.${this.get('elementId')}`).off('mouseenter');
            $(`.${this.get('elementId')}`).off('mouseleave');
            this.set('isDisplayed', false);
          }
        });
      });

    });
  },

  actions: {
    hideTooltip() {
      $(`.${this.get('elementId')}`).off('mouseenter');
      $(`.${this.get('elementId')}`).off('mouseleave');
      this.set('isDisplayed', false);
    }
  }
});
