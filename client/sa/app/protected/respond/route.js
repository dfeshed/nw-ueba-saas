import Ember from 'ember';
import Incidents from "sa/utils/cube/incidents";

export default Ember.Route.extend({

    actions: {

        /**
         * Initialize the data and display of components for this route.
         */
        didTransition: function(){
            this.set("controller.path", [
                {
                    type: "incidents-queue",
                    value: Incidents.create({websocket: this.websocket})
                },
                {
                    type: "incident-info",
                    value: null     // selected incident record will go here after user selects it
                }
            ]);
        },

        /**
         * Tear down the data.
         */
        willTransition: function(){
            var path = this.get("controller.path"),
                cube = path && path[0] && path[0].value;
            if (cube) {
                cube.destroy();
            }
        }
    }

});
