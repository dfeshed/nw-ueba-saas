import Mixin from 'ember-metal/mixin';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';
import run from 'ember-runloop';
import $ from 'jquery';
import service from 'ember-service/inject';

export default Mixin.create({
  displayDelay: 500,
  displayEvent: null,
  hideDelay: 500,
  target: null,
  eventBus: service(),
  /**
   * Checks if text content are overflow or not. Checking the scrollWidth with offsetWidth
   * @param target
   * @returns {boolean}
   * @private
   */
  _isTextOverFlow(target) {
    return target.scrollWidth > target.offsetWidth;
  },

  /**
   * Show the tool tip if text content are more
   * @param e
   * @public
   */
  mouseEnter(e) {
    this.set('showToolTip', true);
    if (this._isTextOverFlow(e.target)) {
      if (this.get('hideEvent')) {
        run.cancel(this.get('hideEvent'));
        this.set('hideEvent', null);
      }
      const displayEvent = run.later(() => {
        if (this.get('isDestroying') || this.get('isDestroyed') || !this.element) {
          // The element has been destroyed since the time when the delay started
          return;
        }
        this.set('target', this.element);
        sendTetherEvent(
          this.element,
          this.get('panelId'),
          this.get('eventBus'),
          'display',
          this.get('model')
        );
      }, this.get('displayDelay'));
      this.set('displayEvent', displayEvent);
    }
  },

  mouseLeave() {
    if (this.get('displayEvent')) {
      run.cancel(this.get('displayEvent'));
      this.set('displayEvent', null);
    }
    const hideEvent = run.later(() => {
      if (!this.get('isDestroying') && !this.get('isDestroyed') && this.target) {
        sendTetherEvent(
          this.target,
          this.get('panelId'),
          this.get('eventBus'),
          'hide'
        );
        this.set('target', null);
      }
    }, this.get('hideDelay'));
    this.set('hideEvent', hideEvent);
  },
  /**
   * Register the window click event, on click set the show tooltip flag to false
   * @public
   */
  didInsertElement() {
    const windowClickFunct = this._handleWindowClick.bind(this);
    $(window).click(windowClickFunct);
    this.setProperties({ windowClickFunct });
  },

  /**
   * Unbind the events
   * @public
   */
  willDestroyElement() {
    const { windowClickFunct } = this.getProperties('windowClickFunct');
    $(window).off('click', windowClickFunct);
  },

  /**
   * If click event is outside tooltip reset the flag
   * @param e
   * @private
   */
  _handleWindowClick(e) {
    const $targetParent = $(e.target.parentElement);
    let isClickInsideTooltip = false;
    if (!$targetParent.length || $targetParent.hasClass('tool-tip-value')) {
      isClickInsideTooltip = true;
    }
    if (!isClickInsideTooltip) {
      this.set('showToolTip', false);
      this.set('target', null);
    }
  }
});
