/**
 * @file RSA Spinner component styling.
 * RSA Spinner is an SVG circular arc that appears to expand & contract its length as it rotates
 * about its center point. Useful for "wait" animations. The root DOM node can be resized via CSS;
 * the SVG content will automatically scale.
 */

import Ember from 'ember';

export default Ember.Component.extend({
    tagName: "svg",

    classNames: "rsa-spinner",

    // Wires up the viewBox HTML attribute, and the status property to the "data-status" HTML attribute.
    attributeBindings: ["viewBox", "status:data-status"],

    /**
     * Defines the SVG viewport area that will be visible to the user. Any SVG markup outside
     * this viewport is not shown. Any SVG markup within this viewport is scaled to fit the
     * root DOM node's size, without distortion (i.e., fit to container using the smallest dimension).
     * @type String
     * @default "0 0 56 56"
     */
    viewBox: "0 0 56 56",

    /**
     * Indicates whether or not the spinner should animate. By default, style rules will render
     * the animation when status = "wait"; otherwise the spinner is not visible.
     * @type String
     * @default "wait"
     */
    status: "wait"
});
