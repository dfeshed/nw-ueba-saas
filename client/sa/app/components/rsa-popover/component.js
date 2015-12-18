/**
 * @file Popover component
 * A popover is a non-modal popup that can appear either on click or on hover, and can be dismissed by clicking
 * anywhere outside of its DOM (and even in its DOM, if desired).  Popovers are used for things like dropdown
 * lists, right-click menus & tooltips.
 * This popover implementation uses the library 'drop' to position and show/hide the popover content.
 * See: http://github.hubspot.com/drop/
 * @public
 */
/* global Drop */
import Ember from 'ember';
import config from 'sa/config/environment';

export default Ember.Component.extend({
  tagName: 'article',
  classNames: 'rsa-popover',

  /**
   * Either a DOM node, or a CSS selector for a DOM node, which will open/close the popover.
   * @type {String|HTMLElement}
   * @default null
   * @public
   */
  target: null,

  /**
   * Indicates the target's event name which should open this popover. Either 'click', 'hover' or 'always'.
   * See: http://github.hubspot.com/drop/
   * @type String
   * @default 'click'
   * @public
   */
  openOn: 'click',

  /**
   * Specifies the attachment point (on the target) to attach the drop to. Should be set to a two words,
   * separated by a space, which specify the vertical alignment and horizontal alignment, respectively.
   * Supported vertical alignments are 'top', 'middle' & 'bottom'. Supported horizontal alignments are
   * 'left', 'center' & 'right'.
   * See: http://github.hubspot.com/drop/
   * @type String
   * @default 'bottom center'
   * @public
   */
  position: 'bottom center',

  /**
   * If set to true, the popover will get flipped when it would otherwise be outside the viewport.
   * For example, this will cause popovers with bottom attachments to switch to top when colliding with the bottom of
   * the page and vice-versa.
   * See: http://github.hubspot.com/drop/
   * @type Boolean
   * @default true
   * @public
   */
  constrainToWindow: true,

  /**
   * Similar to 'constrainToWindow' but for the target element's first scroll parent: the first parent that has
   * overflow: auto or overflow: scroll set, or the body, whichever comes first.
   * See: http://github.hubspot.com/drop/
   * @type Boolean
   * @default true
   * @public
   */
  constrainToScrollParent: true,

  /**
   * Indicates whether the popover should automatically close itself when clicked on.
   * @type Boolean
   * @default true
   * @public
   */
  closeOnClick: true,

  /**
   * The drop JS object which is used internally to open/close this popover.
   * @type Object
   * @private
   */
  _drop: null,

  actions: {

    /**
     * Exposes the close() method as an action so it can be used in templates.
     * @public
     */
    close() {
      this.close();
    }
  },

  /**
   * Responds to clicks within the popover DOM by closing it, if the 'closeOnClick' attribute is true.
   * @public
   */
  click() {
    if (this.get('closeOnClick')) {
      this.close();
    }
    return true;
  },

  /**
   * Closes the popover DOM. Wraps the native drop JS 'close' method.
   * @public
   */
  close() {
    let drop = this.get('_drop');
    if (drop) {
      drop.close();
    }
  },

  /**
   * Responds to open events by sending a configurable action for any observers.  The action should be specified
   * by consumers of this component as the component's 'openAction' property.
   * @public
   */
  onopen() {
    this.sendAction('openAction');
  },

  /**
   * Responds to close events by sending a configurable action for any observers.  The action should be specified
   * by consumers of this component as the component's 'closeAction' property.
   * @public
   */
  onclose() {
    this.sendAction('closeAction');
  },

  /**
   * Wires up the popover's DOM to its target DOM, so that events on the target will open/close the popover.
   * This is done by instantiating a Drop object from the drop JS library. The Drop instance is cached in
   * the local 'drop' attribute for later re-use.
   * @public
   */
  initialize() {
    let target = this.get('target');
    if (typeof target === 'string') {
      target = Ember.$(target)[0];
    }
    if (target) {
      let drop = new Drop({
        target,
        content: this.element,
        position: this.get('position'),
        constrainToWindow: this.get('constrainToWindow'),
        constrainToScrollParent: this.get('constrainToScrollParent')
      });

      // Wire up action hooks for open and close events.
      drop.on('open', this.onopen, this);
      drop.on('close', this.onclose, this);
      this.set('_drop', drop);

      // @workaround For ember testing, the popover's contents won't fire any {{action}}s unless the DOM is
      // inside the ember testing container DOM node. Alas, the Drop code will move the DOM to document.body every
      // time the popover is positioned, so it won't be inside the ember testing container, and no {{action}}s in
      // its templates will fire. So we must move the popover DOM back into the container each time it is opened.
      // Luckily, the automated tests don't care WHERE the DOM's x & y are positioned as long as the DOM nodes are
      // contained in the testing container.  So if you LOOK at the popover in ember tests, they won't be located
      // at the correct coordinates any more, but the automated tests should still fire actions as expected!
      let rootSelector = config.APP.rootElement;
      if (rootSelector) {
        let $root = Ember.$(rootSelector);
        if ($root && $root[0] && $root[0] !== document.body) {
          drop.on('open', function() {
            let el = this.get('_drop').drop;
            $root.append(el);
          }, this);
        }
      }
    }

  },

  didInsertElement() {
    Ember.run.schedule('afterRender', this, 'initialize');
  }
});
