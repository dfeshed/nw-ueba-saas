/**
 * @file RSA Arrow Button component.
 * A button that supports a "status" property whose value can be set to "wait" (among other values), and a
 * boolean "disabled" property.  The button has 3 states of display, which correspond to:
 * (1) disabled = true;
 * (2) disabled = false AND status not = "wait"
 * (3) disabled = false AND status = "wait".
 * Default styling will transition between the 3 different display states, each of which is rendered as
 * a separate SVG element. The transitions are animated with horizontal sliding.
 */

import Ember from 'ember';

export default Ember.Component.extend({
    tagName: "button",

    // Fixed base class for all instances.
    classNames: "rsa-arrow-button",

    // Dynamic classes for individual instances. Typically set by parent view/component.
    classNameBindings: "classExtra",
    classExtra: "",

    // Wires up the disabled property with the root DOM node.
    attributeBindings: "disabled",

    /**
     * The HTML "type" attribute of the button (e.g., "submit", "button", etc).
     * @type String
     * @default "button"
     */
    type: "button",

    /**
     * Indicates whether the button is waiting (status equals "wait") or not.
     * @type String
     * @default Empty string.
     */
    status: "",

    /**
     * If set to true, disables user interaction with the component.
     * @type Boolean
     * @default true
     */
    disabled: false,

    /**
     * Handles user click on the button (in any state). Responds by sending the action named in
     * this component's "action" property, unless this component's "disabled" is truthy.
     * Normally this logic would go into this component's actions hash, and we would put an {{action}}
     * token in the template to trigger it, but in this case we want it to be handled by the root
     * DOM node, which isn't in the template!
     */
    didInsertElement: function(){
        var me = this;
        this.$(this.element).click(function(){
            if (!me.get("disabled")) {
                me.sendAction();
            }
        });
    }
});
