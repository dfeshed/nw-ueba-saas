import Component from '@ember/component';
import computed from 'ember-computed';
import { htmlSafe } from '@ember/string';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';

import layout from './template';
import {
  offset as jOffset
} from 'component-lib/utils/jquery-replacement';

export default Component.extend({
  layout,
  classNames: ['rsa-content-tethered-panel'],

  eventBus: service(),

  anchorHeight: 0,
  anchorWidth: 0,
  closeOnAppClick: true,
  closeOnEsc: false,
  displayCloseButton: true,
  hideDelay: 500,
  // optional The component that is using the tethered-panel can pass in true/false
  // for hideOnLeave. By default it is set to true, which means the tooltip will disappear
  // once the cursor enters the tooltip and then leaves
  hideOnLeave: true,
  isDisplayed: null,
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

  // attach a function to be executed when this tethered panel is open
  // example: focusing an input
  panelDidOpen: null,

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
      case 'top-top':
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
    const panel = document.querySelector('.ember-tether .panel-content');
    if (!panel) {
      return;
    }

    let position = this.get('_position');

    const width = window.innerWidth;
    const height = window.innerHeight;
    const panelHeight = panel.offsetHeight;
    const panelWidth = panel.offsetWidth;
    const offset = jOffset(panel);

    if (!offset) {
      return;
    }

    const borderWidth = 2;
    const edgeMargin = 7;
    const pxModifier = borderWidth + edgeMargin;

    const panelBottom = () => {
      return jOffset(panel).top + panelHeight + pxModifier;
    };
    const panelTop = () => {
      return jOffset(panel).top + pxModifier;
    };
    const panelLeft = () => {
      return jOffset(panel).left + pxModifier;
    };
    const panelRight = () => {
      return jOffset(panel).left + panelWidth + pxModifier;
    };

    let newPosition = position;

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
        const tetherElementList = document.querySelectorAll('.ember-tether');
        if (tetherElementList.length > 1) {
          tetherElementList[0].remove();
        }
      });

      this.set('isDisplayed', value);
      return value;
    }
  }),

  _onEscapeKey(e) {
    if (this.get('closeOnEsc') && e.keyCode === 27) {
      this._didHide();
    }
  },

  willDestroyElement() {
    this.get('eventBus').off(this.get('displayEventName'), this, this._didDisplay);
    this.get('eventBus').off(this.get('hideEventName'), this, this._didHide);
    this.get('eventBus').off(this.get('toggleEventName'), this, this._didToggle);
    this.get('eventBus').off('rsa-application-click', this, this._didApplicationClick);
    this.get('eventBus').off('rsa-application-header-click', this, this._didApplicationClick);
    document.removeEventListener('keydown', this.get('_boundClickListener'));
  },

  didInsertElement() {
    run.schedule('afterRender', () => {
      this.get('eventBus').on(this.get('displayEventName'), this, this._didDisplay);
      this.get('eventBus').on(this.get('hideEventName'), this, this._didHide);
      this.get('eventBus').on(this.get('toggleEventName'), this, this._didToggle);
      this.get('eventBus').on('rsa-application-click', this, this._didApplicationClick);
      this.get('eventBus').on('rsa-application-header-click', this, this._didApplicationClick);
      const _boundClickListener = this._onEscapeKey.bind(this);
      this.set('_boundClickListener', _boundClickListener);
      document.addEventListener('keydown', _boundClickListener);
    });
  },

  _mouseEnter() {
    this.set('isHovering', true);
  },

  _mouseLeave() {
    this.set('isHovering', false);
    run.later(() => {
      if (!this.get('isHovering') && this.get('hideOnLeave')) {
        this.set('isDisplayed', false);
      }
    }, this.get('hideDelay'));
  },

  _didDisplay(anchorHeight, anchorWidth, elId, model) {
    // always reset _position to the original position config on display of the panel to avoid reusing the last repositioning
    // from forceWithinWindow()
    this.set('_position', this.get('position'));
    run.next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        const tC = this.get('targetClass');
        if (document.querySelectorAll(tC).length > 1) {
          this.set('target', `#${elId}`);
        } else {
          this.set('target', tC);
        }

        this.setProperties({
          anchorHeight,
          anchorWidth,
          model,
          isDisplayed: true
        });

        run.schedule('afterRender', () => {
          this.forceWithinWindow();
          if (this.panelDidOpen) {
            this.panelDidOpen();
          }

          const element = document.querySelector(`.${this.get('elementId')}`);
          element.addEventListener('mouseenter', this._mouseEnter);
          element.addEventListener('mouseleave', this._mouseLeave);
        });
      }
    });
  },

  _didHide() {
    run.next(() => {
      if (!this.get('isHovering') && !this.get('isDestroyed') && !this.get('isDestroying')) {
        this._hidepanel();
      }
    });
  },

  _didToggle(height, width, elId, model) {
    this.set('_position', this.get('position'));
    run.next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        const tC = this.get('targetClass');
        if (document.querySelectorAll(tC).length > 1) {
          this.set('target', `#${elId}`);
        } else {
          this.set('target', tC);
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
          if (this.panelDidOpen && this.get('isDisplayed')) {
            this.panelDidOpen();
          }
        });
      }
    });
  },

  _didApplicationClick(target) {
    if (this.get('closeOnAppClick')) {
      if (!target.closest(this.get('targetClass'))) {
        run.next(() => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            this.set('isDisplayed', false);
          }
        });
      }
    }
  },

  actions: {
    hidepanel() {
      this._hidepanel();
    }
  },

  _hidepanel() {
    const element = document.querySelector(`.${this.get('elementId')}`);
    if (element && !this.get('isDestroyed') && !this.get('isDestroying')) {
      element.removeEventListener('mouseenter', this._mouseEnter);
      element.removeEventListener('mouseleave', this._mouseLeave);
      this.set('isDisplayed', false);
    }
  }
});
