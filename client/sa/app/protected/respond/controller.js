import Ember from "ember";
import IncidentsCube from "sa/utils/cube/incidents";
import timeUtil from "sa/utils/time";

export default Ember.Controller.extend({

    /**
     * The time range unit of the current data query. A value from the enumeration sa/utils/time.UNITS.
     * @type String
     */
    timeRangeUnit: timeUtil.UNITS.WEEK,

    /**
     * The time range for these data records. An object with 2 properties, "from" and "to", which are both UTC Dates
     * (in milliseconds) cast as long integers.  This property is computed from "timeRangeUnit". To change the
     * timeRange, simply update the timeRangeUnit.  Note that timeRange is always computed using now as the "to" value.
     * @type {from: number, to: number}
     */
    timeRange: function() {
        var now = Number(new Date());
        return {
            from: now - timeUtil.toMillisec(this.get("timeRangeUnit")),
            to: now
        };
    }.property("timeRangeUnit"),

    /**
     * Fetches a new model for the current time range, and discards the old model (if any) to free up memory usage.
     * Waits to discard the old model until once we have wired up a new model; that has two benefits:
     * (1) if an error occurs, we're not left with an empty UI, and
     * (2) the UI isn't updated unnecessarily as the old model dumps its data.
     * Note that these models are cubes, which can contain large caches that are memory intensive, so discarding them
     * after done using them is important.
     */
    fetchModel: function(){
        var me = this,
            timeRangeUnit = this.get("timeRangeUnit"),
            timeRange = this.get("timeRange");

        // @todo Move this configuration somewhere else (config/environment.js?)
        this.get("websocket").stream(
            {
                url: "/threats/socket",
                subscriptionDestination: "/user/queue/threats/incidents",
                requestDestination: "/ws/threats/incidents/stream"
            },
            {
                sort: [{field: "created", descending: true}],
                filter: [{field: "created", range: timeRange}],
                stream: {limit: 10000}
            })
            .then(function(results) {
                var oldModel = me.get("model"),
                    newModel = IncidentsCube.create({
                        array: results,
                        timeRangeUnit: timeRangeUnit,
                        timeRange: timeRange
                    });
                me.set("model", newModel);
                if (oldModel) {
                    oldModel.destroy();
                }
            });
    }.observes("timeRangeUnit")
});
