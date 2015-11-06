/**
 * @file Incidents Queue component
 * Displays a list of incidents. Supports toggle between the user's incidents or all incidents. Includes
 * popovers for sorting and filtering (client-side).  The incidents data is fetched via websocket. The
 * socket data records are then stored in an IncidentsCube for filtering, sorting & aggregation.
 */
import Ember from "ember";

/**
 * Enumeration of queues.  These are used for the component's "whichQueue" property.
 * @type {{MY: string, ALL: string}}
 * @private
 */
var ENUM_QUEUES = {
    MY: "my",
    ALL: "all"
};

export default Ember.Component.extend({
    tagName: "article",
    classNames: "rsa-incidents-queue",

    /**
     * An instance of the IncidentsCube, used for storing & manipulating data records.
     * @type Object
     */
    cube: null,

    /**
     * Indicates which incidents queue will be shown; either "my" (only the incidents of the current user) or
     * "all" (incidents for all users).
     * @type String
     * @default "my"
     */
    whichQueue: ENUM_QUEUES.MY,

    /**
     * The incident record from cube which has been selected, typically by some user interaction.
     * Used for highlighting the selected record in the DOM template.
     * @type Object
     */
    selectedRecord: null,

    /**
     * Optional callback to be invoked whenever the selectedRecord changes. This is intended to be set externally, by
     * either a parent component or some other object that observes this component.
     * @type Function
     * @default null
     */
    onSelectIncident: null,

    /**
     * Optional callback to be invoked whenever user requests a change in time range. This is intended to be set
     * externally, by either a parent component or some other object that observes this component.
     * @type Function
     */
    onSelectTimeRangeUnit: null,

    actions: {

        /**
         * Resets the "selectedRecord" attribute with the given record (if any), or null.
         * @todo Support multiple selections by inspecting the 2nd param (event object)'s shiftKey & ctrlKey properties.
         * @param {Object} [record] The newly selected record, or null.
         */
        selectRecord: function(record){
            this.set("selectedRecord", record);
            var callback = this.get("onSelectIncident");
            if (callback) {
                callback(record);
            }
        },

        /**
         * Template helper that exposes this component's "set" method to the template.
         * @workaround This should not be necessary. We can call the component's set using the action helper, like so:
         * (action set "key1" "val1" target=this)
         * Indeed that works fine at run-time, but throws an error in unit testing (something about "set" not resolving
         * to an action). So we wrap the set() method in an action to address the unit testing error. Maybe testing bug?
         * @param {String} key Property name to be set.
         * @param {*} val Property value to be applied.
         */
        set: function(key, val) {
            this.set(key, val);
        }
    },

    /**
     * Responds to change in "cube" object or "whichQueue" attribute by setting the assignee filter of the
     * cube object (while leaving any other filters intact).
     */
    cubeOrWhichQueueDidChange: function(){

        function setQueue(){
            var assignee = (this.get("whichQueue") === ENUM_QUEUES.ALL) ?
                    null : this.get("session.content.secure.username"),
                cube = this.get("cube");
            if (cube && cube.filter) {
                cube.filter("assignee", assignee, null, true);
            }
        }
        Ember.run.once(this, setQueue);

    }.observes("whichQueue", "cube").on("didInsertElement")

});
