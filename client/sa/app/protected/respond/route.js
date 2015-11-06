import Ember from "ember";

export default Ember.Route.extend({

    /**
     * Submits a query and sets "model" to an Incidents cube that wraps the query's PromiseArray results.
     * @returns {Promise}
     */
    actions: {

        // Initiates data load upon user's arrival.
        didTransition: function(){
            this.get("controller").fetchModel();
        },

        // Tears down the data.
        willTransition: function(){
            var cube = this.get("controller.model");
            if (cube && cube.destroy) {
                cube.destroy();
            }
        }
    }
});
