/**
 * @file RSA Curtain component.
 * RSA Curtain is container for hiding contents, and then animating their entrance/exit as the
 * curtain grows/shrinks its size. Typically styled as a circle, like a pinhole.
 */

import Ember from "ember";
import { animate } from "liquid-fire";

/**
 * Animates the given component so that its clipping region appears to grow from size 0 to 100%,
 * filling up the component's DOM bounding box.
 * Used when the component is first rendered as an entrance animation.
 * @param {Ember.Component} me The component instance.
 * @param {Boolean} showOrHide If true, animate the DOM opening; otherwise, animate it closing.
 * @param {Number} [duration=500] Optional duration (in millisec) of animation.
 * @param {Number} [delay=0] Optional delay (in millisec) before the animation should begin.
 * @private
 */
function _bounce(me, showOrHide, duration, delay){

    // Get a handle to the DOM node target; cache it, if not cached yet.
    var elAnim = Ember.$(me.element).find(".js-rsa-curtain__anim");

    // Animate the size of the DOM node from 0 to 100%. To keep it centered within its parent,
    // concurrently animate its (left,top) from (50%,50%) to (0,0).  If it is hiding, reverse
    // those from- and to- values.
    // For easing, use a spring when showing, but not for hiding, because spring easing goes beyond
    // the final range value, which will mean negative sizes, which will yield weird results. So when
    // hiding, instead use a bezier curve and cut the duration in half.
    var sizeRange = ["100%", 0],
        posRange = [0, "50%"],
        easing = showOrHide ? "spring" : [0.25,-0.63,0.83,0.67];
    if (!showOrHide) {
        sizeRange.reverse();
        posRange.reverse();
    }
    Ember.run(function(){
        animate(elAnim, {
            width: sizeRange,
            height: sizeRange,
            top: posRange,
            left: posRange
        },{
            duration: duration || 500,
            easing: easing,
            delay: parseInt(delay,10) || 0
        });
    });
}

export default Ember.Component.extend({
    tagName: "div",

    // Fixed class prefix for all instances.
    classNames: "rsa-curtain",

    // Dynamic class prefix for individual instances. Typically set by parent view/component.
    classNameBindings: "classExtra",
    classExtra: "",

    // Binds the opened property to the "data-opened" HTML attribute, for styling.
    attributeBindings: ["opened:data-opened"],

    /**
     * Duration for the opening animation in milliseconds.
     * @type Number
     * @default 1000
     */
    durationOpening: 1000,

    /**
     * Duration for the closing animation in milliseconds.
     * @type Number
     * @default 500
     */
    durationClosing: 500,

    /**
     * Setting this property to true will cause the curtain to be displayed in its "opened"
     * state; otherwise, it will be displayed in its "closed" state (typically, not visible).
     * @type Boolean
     */
    opened: true,

    /**
     * Handler for changes to "opened" property. Responds by updating the display accordingly.
     * @todo Must first check that didInsertElement has already happened; if not, do nothing.
     */
    onOpenedChange: function(){
        var isOpened = !!this.get("opened"),
            duration = this.get(isOpened ? "durationOpening" : "durationClosing");
        _bounce(this, isOpened, duration, 0);
    }.observes("opened"),

    /**
     * Delay (in milliseconds) before the opening animation of this component should begin.
     * Only used when the component is initially rendered in DOM. If "opened" is false at that
     * time, this "delay" property is ignored.
     * @type Number
     * @default 0
     */
    delay: 0,

    /**
     * Triggers the entrance animation when the component is intially rendered.
     * @returns {Ember.Component} this
     */
    didInsertElement: function(){
        if (this.get("opened")) {
            _bounce(this, true, this.get("durationOpening"), this.get("delay"));
        }
        return this;
    }
});
