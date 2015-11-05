/**
 * @file IncidentsCube class.
 * Subclass of Base Cube class that includes properties specifically for an Incidents list.
 */
import Ember from "ember";
import Base from "./base";
import timeUtil from "sa/utils/time";

export default Base.extend({

    pageSize: 300,

    // Default fields
    fieldsConfig: {
        "id": {
            dataType: "string"
        },
        "idNumber": {
            dataType: "string",
            propertyName: "id",
            getter: function(d){
                var match = (d.id || "").match(/\-(\d+)$/);
                return match ? parseInt(match[1],10) : d.id;
            }
        },
        "name":{
            dataType: "string"
        },
        "priority": {
            dataType: "number",
            propertyName: "prioritySort",
            getter: function(d) {
                return d.prioritySort || 0;
            }
        },
        "assignee": {
            getter: function(d) {
                return (d.assignee && d.assignee.login) || "";
            }
        },
        "status": {
            dataType: "number",
            propertyName: "statusSort",
            getter: function(d) {
                return d.statusSort || 0;
            }
        },
        "priorityRiskScore": {
            dataType: "number",
            getter: function(d) {
                return (d.prioritySort || 0) * 1000 + (d.riskScore || 0);
            }
        }
    },

    // Default sort
    sortField: "priorityRiskScore",
    sortDesc: true,

    // The time range unit of the current set of data records. Accessed by "timeRangeUnit" attribute.
    _timeRangeUnit: null,

    /**
     * The time range unit of the current set of data records. A value from the enumeration sa/utils/time.UNITS.
     * A customer getter & setter access a private attribute "_timeRangeUnit" to cache the value.  The setter applies
     * validation to ensure only enumerated values are applied. The getter applies a default when needed.
     * @type String
     * @default DAY
     */
    timeRangeUnit: Ember.computed("_timeRangeUnit", {
        get: function(){
            return this.get("_timeRangeUnit") || timeUtil.UNITS.DAY;
        },
        set: function(key, value) {

            // Validate input. If invalid, use a default.
            var UNITS = timeUtil.UNITS,
                isValid = false;
            for (var k in UNITS) {
                if (UNITS.hasOwnProperty(k) && (UNITS[k] === value)) {
                    isValid = true;
                    break;
                }
            }
            this.set("_timeRangeUnit", isValid ? value : UNITS.DAY);
            return value;
        }
    }),

    /**
     * The time range for these data records. An object with 2 properties, "from" and "to", which are both UTC Dates
     * (in milliseconds) cast as long integers.  This property is computed from "timeRangeUnit". To change the
     * timeRange, simply update the timeRangeUnit.  Note that timeRange is always computed using now as the "to" value.
     * @type {from: Number, to: Number}
     */
    timeRange: function() {
        var now = Number(new Date());
        return {
            from: now - timeUtil.toMillisec(this.get("timeRangeUnit")),
            to: now
        };
    }.property("timeRangeUnit"),

    /**
     * Responds to change in time range by dumping data records and fetching new data records.
     * @todo Consider caching or other optimizations.
     */
    timeRangeDidChange: function(){

        Ember.run.once(this, "resetChannel", true);
    }.observes("timeRange", "timeRange.from", "timeRange.to").on("init"),

    // Configure socket channel according to timeRange:
    channel: function(){
        var timeFrom = this.get("timeRange.from"),
            timeTo = this.get("timeRange.to");
        if (timeFrom && timeTo) {
            return "/topic/threats/incidents";
        }
        else {
            return null;
        }

    }.property("timeRange"),

    channelDestination: "/ws/threats/incidents",

    getChannelBody: function(index){
        var body = this._super(index);
        body.sort = [
                {
                    "field": "created",
                    "descending": true
                }
            ];
        body.filter = [
                {
                    "field": "created",
                    "range": {"from": this.get("timeRange.from"), "to": this.get("timeRange.to")}
                }
            ];
        return body;
    }
});
