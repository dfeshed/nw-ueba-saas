import Ember from "ember";
import config from "sa/config/environment";

export default Ember.Service.extend({

    /**
     * Name of the currently selected theme.
     * @type String
     * @default ""
     */
    selected: Ember.computed({
        get: function(){
            return this.selected;
        },
        set: function(key, val){
            var was = this.get("selected") || "";
            if (was !== val) {
                var rootSelector = config.APP.rootElement || "body",
                    $root = Ember.$(rootSelector);
                if (was) {
                    $root.removeClass("rsa-" + was);
                }
                $root.addClass("rsa-" + val);
            }
            return val;
        }
    })
});
