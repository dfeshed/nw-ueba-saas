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
  panelClass: null,

  // _position is a copy of the originally configured position and potentially modified to avoid window collision (cf forceWithinWindow)
  _position: computed.oneWay('position'),

  displayEventName: computed('panelId', function() {
    return `rsa-content-tethered-panel-display-${this.get('panelId')}`;
  }),

  hideEventName: computed('panelId', function() {
    return `rsa-content-tethered-panel-hide-${this.get('panelId')}`;
  }),

  toggleEventName: computed('panelId', function() {
    return `rsa-content-tethered-panel-toggle-${this.get('panelId')}`;
  }),

  attachment: computed('_position', function() {
    let position = null;
    switch (this.get('_position')) {
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

  forceWithinWindow() {
    let position = this.get('_position');

    const width = $(window).width();
    const height = $(window).height();
    const panel = $('.ember-tether .panel-content');
    const panelHeight = panel.height();
    const panelWidth = panel.width();
    const offset = panel.offset();
    const borderWidth = 2;
    const edgeMargin = 7;
    const pxModifier = borderWidth + edgeMargin;

    const panelBottom = () => {
      return panel.offset().top + panelHeight + pxModifier;
    };
    const panelTop = () => {
      return panel.offset().top + pxModifier;
    };
    const panelLeft = () => {
      return panel.offset().left + pxModifier;
    };
    const panelRight = () => {
      return panel.offset().left + panelWidth + pxModifier;
    };

    let newPosition = position;

    if (!offset) {
      return;
    }

    const reposition = (initialSide, newSide) => {
      newPosition = position.replace(initialSide, newSide);
      return (newPosition === position) ? `${position}-${newSide}` : newPosition;
    };

    if (height < panelBottom()) {
      position = reposition('bottom', 'top');
    }

    if (panelTop() <= 0) {
      position = reposition('top', 'bottom');
    }

    if (panelLeft() <= 0) {
      position = reposition('left', 'right');
    }

    if (width < panelRight()) {
      position = reposition('right', 'left');
    }

    this.set('_position', position);
  },

  horizontalModifier: computed('anchorWidth', 'anchorHeight', 'position', function() {
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

      if (styleString) {
        return styleString;
      }
    }
  }),

  verticalModifier: computed('anchorHeight', 'position', function() {
    const halfAnchorHeight = `${this.get('anchorHeight') / 2}px`;

    if (!this.get('isPopover')) {
      let topOrBottom;
      if (this.get('position') === 'left-bottom' || this.get('position') === 'right-top') {
        topOrBottom = 'top';
      } else if (this.get('position') === 'left-top' || this.get('position') === 'right-bottom') {
        topOrBottom = 'bottom';
      }

      if (topOrBottom) {
        return `margin-${topOrBottom}: calc(-1rem - 7px - ${halfAnchorHeight})`;
      }
    }
  }),

  styleModifiers: computed('horizontalModifier', 'verticalModifier', function() {
    const horizontalModifier = this.get('horizontalModifier') || '';
    const verticalModifier = this.get('verticalModifier') || '';
    return htmlSafe(`${horizontalModifier}${verticalModifier}`);
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

  willDestroyElement() {
    this.get('eventBus').off(this.get('displayEventName'), this, this._didDisplay);
    this.get('eventBus').off(this.get('hideEventName'), this, this._didHide);
    this.get('eventBus').off(this.get('toggleEventName'), this, this._didToggle);
    this.get('eventBus').off('rsa-application-click', this, this._didApplicationClick);
    this.get('eventBus').off('rsa-application-header-click', this, this._didApplicationClick);
  },

  didInsertElement() {
    run.schedule('afterRender', () => {
      this.get('eventBus').on(this.get('displayEventName'), this, this._didDisplay);
      this.get('eventBus').on(this.get('hideEventName'), this, this._didHide);
      this.get('eventBus').on(this.get('toggleEventName'), this, this._didToggle);
      this.get('eventBus').on('rsa-application-click', this, this._didApplicationClick);
      this.get('eventBus').on('rsa-application-header-click', this, this._didApplicationClick);
    });
  },

  _didDisplay(anchorHeight, anchorWidth, elId, model) {
    // always reset _position to the original position config on display of the panel to avoid reusing the last repositioning
    // from forceWithinWindow()
    this.set('_position', this.get('position'));
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
          this.forceWithinWindow();

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
  },

  _didHide() {
    run.next(() => {
      if (!this.get('isHovering')) {
        this._hidepanel();
      }
    });
  },

  _didToggle(height, width, elId, model) {
    this.set('_position', this.get('position'));
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

        run.schedule('afterRender', this, function() {
          this.forceWithinWindow();
        });
      }
    });
  },

  _didApplicationClick(target) {
    if (!$(target).closest(this.get('targetClass')).length > 0) {
      run.next(() => {
        if (!this.get('isDestroyed') && !this.get('isDestroying')) {
          this.set('isDisplayed', false);
        }
      });
    }
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
