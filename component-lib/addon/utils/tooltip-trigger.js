import $ from 'jquery';
import { cancel, later } from '@ember/runloop';

const timerProp = '__rsa-tethered-panel-trigger-timer';

/**
 * Sends an event over the event bus that will be heard by an `rsa-content-tethered-panel` component.
 * Typically used by the panel trigger to send an event to the corresponding panel that it toggles.
 *
 * @param {HTMLElement} el The DOM node of the panel trigger.
 * @param {String} panelId The `panelId` attr of the `rsa-content-tethered-panel` to whom the event is being sent.
 * @param {Object} eventBus The eventBus service. @see component-lib/addon/services/event-bus
 * @param {String} eventType Either 'display', 'hide' or 'toggle'.
 * @param {*} [model] Optional data that will be sent along to the panel when it is toggled. Typically used
 * to give the panel some additional context about its active trigger. Useful for a panel that works with multiple triggers.
 * @public
 */
const sendTetherEvent = function(el, panelId, eventBus, eventType, model) {
  const $el = $(el);
  const height = $el.height();
  const width = $el.width();
  eventBus.trigger(
    `rsa-content-tethered-panel-${eventType}-${panelId}`,
    height,
    width,
    el.id,
    model
  );
};

/**
 * Wires up the click events of a given element to toggle a given `rsa-content-tethered-panel` component.
 *
 * @param {HTMLElement} el The DOM node whose click event is to be wired up.
 * @param {String} panelId The `panelId` attr of the {{rsa-content-tethered-panel}} component to be toggled.
 * @param {Object} eventBus The eventBus service. @see component-lib/addon/services/event-bus
 * @param {Object} [opts] Optional configuration hash.
 * @param {*} [opts.model] Optional data that will be sent along to the panel when it is toggled. Typically used
 * to give the panel some additional context about its active trigger. Useful for a panel that works with multiple triggers.
 * @param {Function} [opts.getIsDisabled] Optional callback that, when invoked, will return true if the trigger
 * DOM node is enabled or false otherwise.  If given, this callback will be invoked during every trigger click event,
 * to determine whether the panel component should be toggled. If the callback returns false, the toggle is aborted.
 * @param {Boolean} [opts.rightClick=false] If true, the click handler will only respond to 'contextmenu' events.
 * @public
 */
const wireTriggerToClick = function(el, panelId, eventBus, opts = {}) {
  const getIsDisabled = typeof opts.getIsDisabled === 'function' ? opts.getIsDisabled : null;
  const $el = $(el);

  // rsa-content-tethered-panel assumes its trigger ("target") DOM node has a
  // css class that matches its the panelId
  if (!$el.hasClass(panelId)) {
    $el.addClass(panelId);
  }

  const eventName = opts.rightClick ? 'contextmenu' : 'click';
  $el.on(`${eventName}.rsa-tethered-panel-trigger`, function() {
    if (!getIsDisabled || !getIsDisabled()) {
      sendTetherEvent(this, panelId, eventBus, 'toggle', opts.model);
    }
  });
};

/**
 * Tears down the click event listener that was attached by `wireTriggerToClick` for a given element.
 * Typically used when a DOM element is about to be removed/destroyed.
 *
 * @param {HTMLElement} el The DOM node whose click event is to be un-wired.
 * @public
 */
const unwireTriggerToClick = function(el) {
  $(el).off('.rsa-tethered-panel-trigger'); // unwires both click & contextmenu
};

/**
 * Wires up the mouseenter & mouseleave events of a given element to toggle a given `rsa-content-tethered-panel` component.
 *
 * @param {HTMLElement} el The DOM node whose mouse events is to be wired up.
 * @param {String} panelId The `panelId` attr of the {{rsa-content-tethered-panel}} component to be toggled.
 * @param {Object} eventBus The eventBus service. @see component-lib/addon/services/event-bus
 * @param {Object} [opts] Optional configuration hash.
 * @param {*} [opts.model] Optional data that will be sent along to the panel when it is toggled. Typically used
 * to give the panel some context about its active trigger. Useful for a panel that works with multiple triggers.
 * @param {Function} [opts.getIsDisabled] Optional callback that, when invoked, will return true if the trigger
 * DOM node is enabled or false otherwise.  If given, this callback will be invoked during every trigger click event,
 * to determine whether the panel component should be toggled. If the callback returns false, the toggle is aborted.
 * @param {Number} [opts.displayDelay=0] Optional pause (in milliseconds) after mouseenter before displaying the tooltip.
 * @param {Number} [opts.hideDelay=0] Optional pause (in milliseconds) after mouseleave before hiding the tooltip.
 * @param {view} [opts.trigger] Optional Reference to the trigger component view. Note: that if this option property is
 * not provided and the trigger component used to activate the tether panel is removed/destroyed within the display delay,
 * the el used for the tether panel may no longer be in the DOM, causing an error in the underlying tether code.
 * @public
 */
const wireTriggerToHover = function(el, panelId, eventBus, opts = {}) {
  const getIsDisabled = typeof opts.getIsDisabled === 'function' ? opts.getIsDisabled : null;
  const { trigger } = opts;
  const $el = $(el);

  // rsa-content-tethered-panel assumes its trigger ("target") DOM node has a
  // css class that matches its the panelId
  if (!$el.hasClass(panelId)) {
    $el.addClass(panelId);
  }

  $el
    .on('mouseenter.rsa-tethered-panel-trigger', function() {
      if (!getIsDisabled || !getIsDisabled()) {
        const lastTimer = this[timerProp];
        if (lastTimer) {
          cancel(lastTimer);
          this[timerProp] = null;
        }
        if (opts.displayDelay) {
          this[timerProp] = later(() => {
            if (trigger && (trigger.get('isDestroyed') || trigger.get('isDestroying'))) {
              return;
            }
            sendTetherEvent(this, panelId, eventBus, 'display', opts.model);
          }, opts.displayDelay);
        } else {
          sendTetherEvent(this, panelId, eventBus, 'display', opts.model);
        }
      }
    })
    .on('mouseleave.rsa-tethered-panel-trigger', function() {
      const lastTimer = this[timerProp];
      if (lastTimer) {
        cancel(lastTimer);
        this[timerProp] = null;
      }
      if (opts.hideDelay) {
        this[timerProp] = later(() => {
          sendTetherEvent(this, panelId, eventBus, 'hide', opts.model);
        }, opts.hideDelay);
      } else {
        sendTetherEvent(this, panelId, eventBus, 'hide', opts.model);
      }
    });
};

/**
 * Tears down the mouse event listeners that were attached by `wireTriggerToHover` for a given element.
 * Typically used when a DOM element is about to be removed/destroyed.
 *
 * @param {HTMLElement} el The DOM node whose mouse events are to be un-wired.
 * @public
 */
const unwireTriggerToHover = function(el) {
  $(el)
    .off('mouseenter.rsa-tethered-panel-trigger')
    .off('mouseleave.rsa-tethered-panel-trigger');
};

/**
 * @class Tooltip Trigger utilities
 * Utilities for wiring and unwiring the DOM events of a "trigger" to show/hide an `rsa-content-tethered panel` component.
 *
 * Some of these utilities are used by the `rsa-content-tethered-panel-trigger` component to show or hide a
 * corresponding `rsa-content-tethered-panel` component.  Typically an app developer just uses that trigger
 * component rather than calling these utilities directly.  However we provide them here anyway so that they can be used
 * to implement alternative custom triggers, such as a trigger which is just a DOM node but not an Ember component
 * (e.g., an SVG path or object in a visualization).
 *
 * @public
 */
export {
  sendTetherEvent,
  wireTriggerToHover,
  unwireTriggerToHover,
  wireTriggerToClick,
  unwireTriggerToClick
};
