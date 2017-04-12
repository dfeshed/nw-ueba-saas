import $ from 'jquery';
import Component from 'ember-component';
import computed from 'ember-computed';
import { htmlSafe } from 'ember-string';
import run from 'ember-runloop';
import service from 'ember-service/inject';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['rsa-content-tethered-panel'],

  eventBus: service(),

  anchorHeight: 0,
  anchorWidth: 0,
  displayCloseButton: true,
  hideDelay: 500,
  // optional The component that is using the tethered-panel can pass in true/false
  // for hideOnLeave. By default it is set to true, which means the tooltip will disappear
  // once the cursor enters the tooltip and then leaves
  hideOnLeave: true,
  isDisplayed: false,
  isHovering: false,
  isPopover: false,
  // optional arbitrary data, passed from trigger to here via eventBus
  // gets passed along in yield, treated as a black box here
  // useful when re-using 1 tooltip instance with multiple triggers
  model: null,
  panelId: null,
  position: 'right',
  style: 'standard', // ['standard', 'error', 'primary']
  target: null,

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
        position = 'bottom right';
        break;

      case 'left-bottom':
        position = 'top right';
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

  horizontalModifier: computed('anchorWidth', 'anchorHeight', function() {
    const anchorWidth = `${this.get('anchorWidth')}px`;
    const anchorHeight = `${this.get('anchorHeight')}px`;
    let styleString;

    if (this.get('isPopover')) {
      if (this.get('position').endsWith('top')) {
        styleString = `top: ${anchorHeight}`;
      } else if (this.get('position').endsWith('bottom')) {
        styleString = `top: -${anchorHeight}`;
      } else if (this.get('position').endsWith('left')) {
        styleString = `left: ${anchorWidth}`;
      } else if (this.get('position').endsWith('right')) {
        styleString = `left: -${anchorWidth}`;
      }

      return styleString;
    }
  }),

  verticalModifier: computed('anchorHeight', function() {
    const halfAnchorHeight = `${this.get('anchorHeight') / 2}px`;

    if (!this.get('isPopover')) {
      let topOrBottom;
      if (this.get('position') === 'left-bottom' || this.get('position') === 'right-top') {
        topOrBottom = 'top';
      } else if (this.get('position') === 'left-top' || this.get('position') === 'right-bottom') {
        topOrBottom = 'bottom';
      }

      return `margin-${topOrBottom}: calc(-1rem - 7px - ${halfAnchorHeight})`;
    }
  }),

  styleModifiers: computed('horizontalModifier', 'verticalModifier', function() {
    return htmlSafe(`${this.get('horizontalModifier')}; ${this.get('verticalModifier')};`);
  }),

  targetClass: computed('panelId', function() {
    return `.${this.get('panelId')}`;
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
      this.get('eventBus').on(`rsa-content-tethered-panel-display-${this.get('panelId')}`, (anchorHeight, anchorWidth, elId, model) => {
        run.next(() => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            if ($(this.get('targetClass')).length > 1) {
              this.set('target', `#${elId}`);
            } else {
              this.set('target', this.get('targetClass'));
            }

            this.setProperties({
              anchorHeight,
              anchorWidth,
              model,
              isDisplayed: true
            });

            run.schedule('afterRender', () => {
              $(`.${this.get('elementId')}`).on('mouseenter', () => {
                this.set('isHovering', true);
              });

              $(`.${this.get('elementId')}`).on('mouseleave', () => {
                this.set('isHovering', false);
                run.later(() => {
                  if (!this.get('isHovering') && this.get('hideOnLeave')) {
                    this.set('isDisplayed', false);
                  }
                }, this.get('hideDelay'));
              });
            });
          }
        });
      });

      this.get('eventBus').on(`rsa-content-tethered-panel-hide-${this.get('panelId')}`, () => {
        run.next(() => {
          if (!this.get('isHovering')) {
            this._hidepanel();
          }
        });
      });

      this.get('eventBus').on(`rsa-content-tethered-panel-toggle-${this.get('panelId')}`, (height, width, elId, model) => {
        run.next(() => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            if ($(this.get('targetClass')).length > 1) {
              this.set('target', `#${elId}`);
            } else {
              this.set('target', this.get('targetClass'));
            }

            this.set('model', model);
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
    hidepanel() {
      this._hidepanel();
    }
  },

  _hidepanel() {
    $(`.${this.get('elementId')}`).off('mouseenter');
    $(`.${this.get('elementId')}`).off('mouseleave');
    if (!this.get('isDestroyed') && !this.get('isDestroying')) {
      this.set('isDisplayed', false);
    }
  }
});
