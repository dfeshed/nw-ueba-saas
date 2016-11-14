import Ember from 'ember';
import layout from '../templates/components/rsa-content-tooltip';

const {
  Component,
  $,
  String: {
    htmlSafe
  },
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

  isPopover: false,

  classNames: ['rsa-content-tooltip'],

  style: 'standard', // ['standard', 'error', 'primary']

  position: 'right',

  isDisplayed: false,

  tooltipId: null,

  target: null,

  isHovering: false,

  hideDelay: 500,

  displayCloseButton: true,

  anchorHeight: 0,

  anchorWidth: 0,

  verticalModifier: computed('anchorHeight', function() {
    const halfAnchorHeight = `${this.get('anchorHeight') / 2}px`;
    let styleString;

    if (!this.get('isPopover')) {
      if (this.get('position') == 'left-bottom') {
        styleString = `margin-top: calc(-1rem - 7px - ${halfAnchorHeight})`;
      } else if (this.get('position') === 'left-top') {
        styleString = `margin-bottom: calc(-1rem - 7px - ${halfAnchorHeight})`;
      } else if (this.get('position') === 'right-bottom') {
        styleString = `margin-bottom: calc(-1rem - 7px - ${halfAnchorHeight})`;
      } else if (this.get('position') === 'right-top') {
        styleString = `margin-top: calc(-1rem - 7px - ${halfAnchorHeight})`;
      }

      return styleString;
    }
  }),

  horizontalModifier: computed('anchorWidth', 'anchorHeight', function() {
    const anchorWidth = `${this.get('anchorWidth')}px`;
    const anchorHeight = `${this.get('anchorHeight')}px`;
    let styleString;

    if (this.get('isPopover')) {
      if (this.get('position') === 'left-bottom') {
        styleString = `top: -${anchorHeight}`;
      } else if (this.get('position') === 'left-top') {
        styleString = `top: ${anchorHeight}`;
      } else if (this.get('position') === 'right-bottom') {
        styleString = `top: -${anchorHeight}`;
      } else if (this.get('position') === 'right-top') {
        styleString = `top: ${anchorHeight}`;
      } else if (this.get('position') === 'top-left') {
        styleString = `left: ${anchorWidth}`;
      } else if (this.get('position') === 'top-right') {
        styleString = `left: -${anchorWidth}`;
      } else if (this.get('position') === 'bottom-left') {
        styleString = `left: ${anchorWidth}`;
      } else if (this.get('position') === 'bottom-right') {
        styleString = `left: -${anchorWidth}`;
      }

      return styleString;
    }
  }),

  styleModifiers: computed('horizontalModifier', 'verticalModifier', function() {
    return htmlSafe(`${this.get('horizontalModifier')}; ${this.get('verticalModifier')};`);
  }),

  targetClass: computed('tooltipId', function() {
    return `.${this.get('tooltipId')}`;
  }),

  attachment: computed('position', function() {
    let position = null;
    switch (this.get('position')) {
      case 'top':
        position = 'bottom center';
        break;

      case 'top-left':
        if (this.get('isPopover')) {
          position = 'bottom right';
        } else {
          position = 'bottom center';
        }
        break;

      case 'top-right':
        if (this.get('isPopover')) {
          position = 'bottom left';
        } else {
          position = 'bottom center';
        }
        break;

      case 'bottom':
        position = 'top center';
        break;

      case 'bottom-left':
        if (this.get('isPopover')) {
          position = 'top right';
        } else {
          position = 'top center';
        }
        break;

      case 'bottom-right':
        if (this.get('isPopover')) {
          position = 'top left';
        } else {
          position = 'top center';
        }
        break;

      case 'left':
        position = 'middle right';
        break;

      case 'left-top':
        if (this.get('isPopover')) {
          position = 'bottom right';
        } else {
          position = 'bottom right';
        }
        break;

      case 'left-bottom':
        if (this.get('isPopover')) {
          position = 'top right';
        } else {
          position = 'top right';
        }
        break;

      case 'right':
        position = 'middle left';
        break;

      case 'right-top':
        if (this.get('isPopover')) {
          position = 'bottom left';
        } else {
          position = 'middle left';
        }
        break;

      case 'right-bottom':
        if (this.get('isPopover')) {
          position = 'top left';
        } else {
          position = 'middle left';
        }
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
      this.get('eventBus').on(`rsa-content-tooltip-display-${this.get('tooltipId')}`, (height, width, elId) => {
        run.next(() => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            if ($(this.get('targetClass')).length > 1) {
              this.set('target', `#${elId}`);
            } else {
              this.set('target', this.get('targetClass'));
            }

            this.set('anchorHeight', height);
            this.set('anchorWidth', width);
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
          }
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-hide-${this.get('tooltipId')}`, () => {
        run.next(() => {
          if (!this.get('isHovering')) {
            if (!this.get('isDestroyed') && !this.get('isDestroying')) {
              $(`.${this.get('elementId')}`).off('mouseenter');
              $(`.${this.get('elementId')}`).off('mouseleave');
              this.set('isDisplayed', false);
            }
          }
        });
      });

      this.get('eventBus').on(`rsa-content-tooltip-toggle-${this.get('tooltipId')}`, (height, width, elId) => {
        run.next(() => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            if ($(this.get('targetClass')).length > 1) {
              this.set('target', `#${elId}`);
            } else {
              this.set('target', this.get('targetClass'));
            }

            this.toggleProperty('isDisplayed');

            if (height) {
              this.set('anchorHeight', height);
            }
            if (width) {
              this.set('anchorWidth', width);
            }
          }
        });
      });

      this.get('eventBus').on('rsa-application-click', (target) => {
        if (!$(target).closest(this.get('targetClass')).length > 0) {
          run.next(() => {
            if (!this.get('isDestroyed') && !this.get('isDestroying')) {
              this.set('isDisplayed', false);
            }
          });
        }
      });
    });
  },

  actions: {
    hideTooltip() {
      $(`.${this.get('elementId')}`).off('mouseenter');
      $(`.${this.get('elementId')}`).off('mouseleave');
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('isDisplayed', false);
      }
    }
  }
});
