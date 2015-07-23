/**
 * @file Default (index) route
 * Defines the default route when no top-level route is given. Eventually, the logic for choosing the default
 * could be sophisticated (e.g., it could depend on the user's roles/permissions). For now, we hard-code.
 */
import Ember from "ember";

export default Ember.Route.extend({
    beforeModel: function(){

        // By default, redirect to "monitor".
        this.transitionTo("protected.monitor");
    }
});
