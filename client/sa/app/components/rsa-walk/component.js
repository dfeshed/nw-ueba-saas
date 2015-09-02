import Ember from "ember";

export default Ember.Component.extend({
    tagName: "section",
    classNames: "rsa-walk",

    /**
     * The current path of this walk. The path is an array of steps. Each step is any Object with a "type" property,
     * which the walk will inspect in order to determine what component to render for the corresponding step UI.
     * @type {Object[]}
     */
    path: null,

    actions: {
        forward: function(from, type, value){
            var path = this.get("path"),
                idx = from ? path.indexOf(from) : -1;

            if (idx > -1) {
                for (var i = path.length-1; i> idx; i--) {
                    path.popObject();
                }
            }

            path.pushObjects([{type: type, value: value}]);
        }
    }
});
