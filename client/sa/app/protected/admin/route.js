import config from 'sa/config/environment';
import Ember from "ember";

export default Ember.Route.extend({
    setupController: function(controller) {
        controller.set('name', config.APP.name);
        controller.set('version', config.APP.version);
    }
});
