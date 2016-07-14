/**
 * @file RSA Logo component.
 * An HTML element with two children: an RSA logo in SVG (a 'brick' with the letters 'RSA' inside),
 * and an (optional) title text in HTML DOM.
 * The root DOM node can be resized via CSS; the SVG will automatically scale.
 * @public
 */
import Ember from 'ember';
import layout from '../templates/components/rsa-logo';

const { Component } = Ember;

/**
 * The HTML attribute used in the component markup to specify the animation delay for a DOM node.
 * @type {string}
 * @default 'data-js-delay-index'
 * @private
 */
const _HTML_ATTR = 'data-js-delay-index';

export default Component.extend({

  layout,

  /*
   Wraps the content in a containing element. If all the markup was in SVG, this root node could
   just be the SVG element, but alas, sometimes the markup includes a text title, which is NOT done
   in SVG, so we need this container.
   @public
   */
  tagName: 'div',

  // Base class prefix for all instances.
  classNames: 'rsa-logo',
  classNameBindings: ['animated'],

  /**
   * If true, indicates that the logo uses CSS animation.
   * @type boolean
   * @default true
   * @public
   */
  animated: true,

  /**
   * Comma-delimited list of animation delays (in millisec) for the component's DOM nodes.  These delays will be
   * applied to any DOM nodes with the HTML attribute [data-js-delay-index].  The value of the attribute
   * specifies which delay to be applied; e.g., '0' indicates the first value in 'delays', '1' indicates
   * the second value in 'delays', etc.  If a DOM node's 'data-js-delay-index' is set to some index greater
   * than the last value in the 'delays' string, then the last value will be applied to that DOM node.
   * Non-integer values and negative values are treated as zeroes.
   * @type String
   * @default '0,1000,2000'
   * @public
   */
  delays: '0,0,1000',

  /**
   * Sets the animation delays for all the DOM nodes that need it (as denoted by the [data-js-delay-index]
   * HTML attribute in the markup).  This could alternatively be accomplished by binding the 'style'
   * attribute of DOM nodes in the template, but that results in CrossSiteScript warnings from Ember.
   * @returns {Ember.Component} this
   * @public
   */
  didInsertElement() {

    // Parse the 'delays' property into an array of integers.
    let delays = String(this.get('delays'))
        .split(',')
        .map(function(arg) {
          return Math.max(0, parseInt(arg, 10) || 0);
        }),
      maxIndex = delays.length - 1;
    if (maxIndex < 0) {
      delays = [0];
      maxIndex = 0;
    }

    // For each DOM node (in this component) with the special HTML attribute, set an animationDelay.
    let me = this;
    this.$(`[${_HTML_ATTR}]`).css('animationDelay', function() {

      // Read the node's delay index and the corresponding delay value (ms).
      let $el = me.$(this),
        index = parseInt($el.attr(_HTML_ATTR), 10) || 0,
        delay = delays[Math.min(maxIndex, index)];
      return `${delay}ms`;
    });
    return this;
  }

});
