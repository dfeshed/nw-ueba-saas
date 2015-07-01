import config from 'sa/config/environment';
import Route from 'sa/base/Route';

export default Route.extend({
    setupController: function(controller) {
        controller.set('name', config.APP.name);
        controller.set('version', config.APP.version);
    }
});
