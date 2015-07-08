/**
 * @file RSA Arrow component.
 * An SVG arrow. Useful for buttons. Points to the right by default, but can
 * be rotated to any direction via applying a CSS transform to the root DOM node.
 * The root DOM node can be resized via CSS; the SVG will automatically scale.
 */

import Ember from 'ember';

export default Ember.Component.extend({
    tagName: "svg",

    // Fixed base class for all instances.
    classNames: "rsa-arrow",

    // Dynamic classes for individual instances. Typically set by parent view/component.
    classNameBindings: "classExtra",
    classExtra: "",

    // Wires up the viewBox property with the root DOM node.
    attributeBindings: ["viewBox"],

    /**
     * Defines the SVG viewport area that will be visible to the user. Any SVG markup outside
     * this viewport is not shown. Any SVG markup within this viewport is scaled to fit the
     * root DOM node's size, without distortion (i.e., fit to container using the smallest dimension).
     * @type String
     * @default "0.333 0 512 391.688"
     */
    viewBox: "0.333 0 512 391.688"

});
