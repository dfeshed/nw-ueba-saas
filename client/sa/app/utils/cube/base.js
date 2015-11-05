/**
 * @file  Base Cube class.
 * A wrapper for Square's crossfilter library.
 */
/* global crossfilter */
import Ember from "ember";
import ENUM_FIELD_TYPE from "./field/enum-type";
import DefaultField from "./field/default";
import CsvField from "./field/csv";
import ArrayField from "./field/array";

// Utility that instantiates a new field class instance for a given field config.
function newFieldObject(cube, key, config) {
    var whichClass;
    config = config || {};
    config.propertyName = config.propertyName || key;
    config.cube = cube;
    switch(config && config.type) {
        case ENUM_FIELD_TYPE.ARRAY:
            whichClass = ArrayField;
            break;
        case ENUM_FIELD_TYPE.CSV:
            whichClass = CsvField;
            break;
        default:
            whichClass = DefaultField;
    }
    return whichClass.create(config);
}

// Generates a callback for a subscription for an instance of cube.
function makeMessageCallback(me) {
    return function (message) {
        var response = message.body;
        if (response.code !== 0) {      // error
            console.err("Socket error message received: ", message);
        }
        else {      // success

            // Add incoming data to cube.
            me.add(response.data);

            // Recompute count, total and progress.
            var count = (me.get("records") || []).length,
                total = (response.meta && response.meta.total) || count;
            me.setProperties({
                totalCount: total,
                progress: total ? parseInt(100 * count / total, 10) : 100
            });

            // Are there more pages of socket data to fetch?
            if (count < total) {

                /*
                 @hack Read the page index from this payload, and request the next page.
                 This assumes a certain structure for the payload JSON, which is cheating.
                 @todo Can we make this be a little more formalized?
                 */
                var request = response.request;
                if (request.page && (typeof request.page.index === "number")) {
                    me.resetChannel(
                        false,  // don't clear the data we've fetched so far
                        me.getChannelBody(request.page.index + 1)   // request the next page of data
                    );
                }
            }
        }
    };
}

export default Ember.Object.extend({

    /**
     * Master set of data records for this crossfilter to filter/aggregate/sort.  This array should be treated
     * as read-only. To add records to the crossfilter, use this object's "add()" method.
     * @readonly
     * @type Object[]
     * @default []
     */
    records: null,

    /**
     * Total size of the master set of data records for this crossfilter to filter/aggregate/sort. This count should
     * be treated as read-only. It may be larger than the length of "records" if the records are being received from
     * the webserver in "pages" (chunks); in that scenario, "totalCount" is the count across all pages, not just the
     * current page.
     * @readonly
     * @type Number
     * @default 0
     */
    totalCount: 0,

    /**
     * Maximum number of records to fetch over websocket per request.
     * @type Integer
     * @default 100
     */
    pageSize: 100,

    /**
     * Measure of progress in loading records from websocket. Generally defined as count of records received so far
     * divided by total count of incoming records, expressed as a whole number percentage (0-100).
     * If totalCount is zero or undefined, progress is considered 100.
     * @readonly
     * @type Number
     */
    progress: 100,

    /**
     * Hash of field objects. Each field object represents a field in the data records. A field object is an instance
     * of one of the cube/field/ classes (e.g., DefaultField, CsvField, or ArrayField).  These fields can be
     * used for sorting, filtering and aggregation.  The "fields" hash is derived at run-time from the "fieldsConfig"
     * property. For more details, see "fieldsConfig" docs.
     * @readonly
     * @type {}
     * @default {}
     */
    fields: null,

    /**
     * An Array with the objects from the "fields" hash.
     * @workaround This is redundant with the "fields" attribute, but useful for templates. If the template helper
     * "{{#each}}" supported hashes (not just arrays), we wouldn't need "fieldsList".
     */
    fieldsList: null,

    /**
     * Metadata hash for the fields of "records". These fields can be used to create dimensions for this crossfilter.
     * Each field represents either an actual property of the objects in "records" or a computed property of those
     * objects.  (For example, if records have "createdDate" property, then the computed field "age" could be defined
     * by using a getter that computes an age number from the "createdDate" property).
     * The keys of the "records" hash are names of fields; the hash values are each an Object with the following
     * properties:
     * "propertyName": {String} optional name of the property that this field maps to; used for non-computed fields.
     * "getter": (Function) optional function for reading the field value; required if "key" is not specified.
     * "type": optional; one of the enumerated values in enum-type. Default type = DEFAULT.
     * @type Object[]
     * @default {}
     */
    fieldsConfig: null,

    /**
     * The field by which to sort the results array.
     * If the name of a record property is given which is not found in the "fields" hash, then a field is
     * automatically added for that property.
     * @type String
     * @default "id"
     */
    sortField: null,

    /**
     * If true, indicates that the sort order is descending. Otherwise it is descending.
     * @type Boolean
     * @default true
     */
    sortDesc: true,

    /**
     * Subset of "records" that matches the current filter criteria, sorted by the current sort configuration attrs.
     * This computed property fetches sorted results from the corresponding dimension object of the current sort field.
     * @type Object[]
     */
    results: function(){
        var sortField = this.get("sortField") || "id",
            fieldObject = this.get("fields." + sortField) || this.addField(sortField);

        // The dimension caches sorted results in descending order. Use "bottom" for ascending.
        return fieldObject.get("dimension")[this.get("sortDesc") ? "top" : "bottom"](Infinity);
    }.property("lastRecalc"),

    /**
     * An observable timestamp, used to trigger the recalculation of computed properties. See "recalc()" method.
     * @type Number
     * @default 0
     * @private
     */
    lastRecalc: 0,

    /**
     * Triggers a recalculation of computed properties (e.g., "results").
     * This is accomplished by simply updating an attribute ("lastRecalc") to the current timestamp.  Computed
     * properties can then observe this attribute and update themselves accordingly.
     */
    recalc: function(){
        this.set("lastRecalc", this.get("lastRecalc") + 1);
    },

    /**
     * Adds the given set of objects to "records" and to the crossfilter's data space. Triggers a recalculation
     * of computed properties, such as "results".
     * @param {Object[]} records The objects to be added.
     */
    add: function(records) {
        if (records) {
            this.get("crossfilter").add(records);
            this.get("records").pushObjects(records);
            this.recalc();
        }
    },

    /**
     * Removes all the records from the cube's data space (even records that fall outside of the current
     * filters). Triggers a recalculation of computed properties, such as "results".
     */
    clear: function() {
        this.setProperties({
            records: [],
            totalCount: 0,
            progress: 100
        });

        // Crossfilter's native remove() method will only work on the records within the current filter. So we
        // temporarily clear all the filters, call remove(), then restore the filters.
        var xfilter = this.get("crossfilter"),
            fields = this.get("fields"),
            filters = {};

        // Remove all the current filters, one dimension at a time, temporarily caching them.
        Object.keys(fields).forEach(function(f) {
            var filter = fields[f].get("filter.native");
            if (filter !== null) {
                filters[f] = filter;
                fields[f].get("dimension").filterAll();
            }
        });

        // Now that all filters are cleared, .remove() should clear all records.
        xfilter.remove();

        // Now restore all the filters we just removed.
        Object.keys(filters).forEach(function(f) {
            fields[f].get("dimension").filter(filters[f]);
        });

        this.recalc();
    },

    /**
     * Sets the field and order for sorting the "results" array. Triggers a recalculation of computed properties,
     * such as "results".  Subsequently, the "results" will continue to be automatically sorted, even after records
     * are added or removed. The given field and order are cached in the attributes "sortField" and "sortDesc" for
     * future reference.
     */
    sort: function(field, desc){
        var props = {};
        if (field !== null) {
            props.sortField = field;
        }
        if (desc !== null) {
            props.sortDesc = !!desc;
        }
        this.setProperties(props);
        this.recalc();
    },

    /**
     * Applies a given filter or set of filters to 1 or more fields.
     * This method accepts two signatures:
     * (1) field, value, options, clearOthers; and
     * (2) filters, clearOthers.
     * When using the first signature, the expected params are as follows:
     * (i) "field" (String): the identifier of the field to which the filter will be applied;
     * (ii) "value" (null|Number|String|{from: *, to: *}|Array|Function): the filter value to be applied;
     * (iii) options (Object): optional hash of options, used to specify whether to add to (options = {add: true}),
     * remove from (options = {remove: true}) or reset (default behavior) the current filter value for the given field;
     * (iv) "clearOthers" (Boolean): if true, indicates that the filters for all other fields should be cleared.
     * When using the 2nd signature, the expected params are as follows:
     * (i) "filters" (Object[]): an array of objects with the properties "field", "value" & "options", which correspond
     * to the params of the 1st signature described above;
     * (ii) "clearOthers" (Boolean): see the description of the "clearOthers" param in the 1st signature above.
     */
    filter: function(){

        // Validate inputs to determine which signature this method was called with.
        var arr, clearOthers;
        if (typeof arguments[0] === "string") {
            arr = [{field: arguments[0], value: arguments[1], options: arguments[2]}];
            clearOthers = arguments[3];
        }
        else {
            arr = arguments[0];
            clearOthers = arguments[1];
        }

        // If we don't have any filter changes to apply, exit.
        if (!arr.length && !clearOthers) {
            return;
        }

        // Apply each given field filter.
        var changed = {},
            me = this;
        arr.forEach(function(item){

            // Get the dimension object for the field.
            var fieldObject = me.get("fields." + item.field) || me.addField(item.field),
                opts = item.options || {},
                methodName = opts.remove ? "remove" : (opts.add ? "add" : "reset"),
                nativeFilter = fieldObject.get("filter")[methodName](item.value).get("native");

            fieldObject.get("dimension").filter(nativeFilter);
            changed[item.field] = true;
        });

        // Clear the other fields' filters, if requested.
        if (clearOthers) {
            var fields = this.get("fields");
            Object.keys(fields).forEach(function(key) {
                if (!changed[key]) {
                    var fieldObject = fields.get(key);
                    fieldObject.get("filter").reset(null);
                    fieldObject.get("dimension").filter(null);
                }
            });
        }

        // Update computed attributes.
        this.recalc();
    },


    /**
     * Instantiates a new native crossfilter object, a local cache for storing the (unfiltered) data records, and a
     * hash of field objects.
     */
    init: function(){
        this._super();

        // Define local caches.
        this.setProperties({
            records: [],
            crossfilter: crossfilter(),
            fields: Ember.Object.create(),
            fieldsList: []
        });

        // Generate the field objects from the given configs.
        var cfg = this.get("fieldsConfig"),
            me  = this;
        if (cfg) {
            Object.keys(cfg).forEach(function(key) {
                me.addField(key, cfg[key]);
            });
        }
    },

    /**
     * Adds a new member to the "fields" hash under the given hash key, using the given config.
     * @param {String} key Unique identifier for the field; typically the property name that the field corresponds to.
     * @param {Object} [cfg] Optional hash of default properties for the field.
     * @returns {Object} The newly created Object that represents the requested field.
     */
    addField: function(key, cfg){
        var fieldObject = newFieldObject(this, key, cfg);
        this.set("fields." + key, fieldObject);
        this.get("fieldsList").pushObjects([fieldObject]);
        return fieldObject;
    },

    /**
     * Optional handle to the websocket service, which this object can use to retrieve data via openChannel().
     * @workaround We add "websocket" as an attribute because we can't get a reference to the service in a util.
     * @type Object
     * @default null
     */
    websocket: null,

    /**
     * The destination used by openChannel() to subscribe for data records. Must be set in order to use openChannel().
     * Typically set by subclasses or set on individual instances.
     * @type String
     * @default null
     */
    channel: null,

    /**
     * The destination used by openChannel() to send an initial message over a new channel, in order to kick off data
     * flow. Must be set in order to use openChannel(). Typically set by subclasses or set on individual instances.
     * @type String
     * @default null
     */
    channelDestination: null,

    /**
     * Optional data payload sent by openChannel() in the initial message over a new channel, in order to kick off data
     * flow.  Typically extended by subclasses or set on individual instances.
     * @type String
     * @default null
     */
    getChannelBody: function(index) {
        var out = {
                "page": {
                    "index": index || 0
                }
            },
            size = this.get("pageSize");

        if (size) {
            out.page.size = size;
        }

        return out;
    },

    /**
     * Opens a websocket channel to fetch data records. The data records are then cached into the crossfilter.
     * If channel is already open, does nothing.
     * Relies on required properties "channel" and "channelDestination" to configure the socket destination. If those
     * properties return null, then this method aborts and exits.
     * When the channel returns a successful response, its response is assumes to be an object with the following
     * structure:
     * "code": (Number) an error code (0 = success, no error);
     * "data": (Object[]) an array of data records (e.g., a list of Incident objects);
     * "request": (Object) an object that echo's the original request body (e.g., {page: .., sort: .., filter: ..});
     * "meta": (Object) an object with additional information about the response (e.g., {total: ###}).
     * @param {Object} [body] Optional payload to be sent to websocket server in the MESSAGE immediately following the
     * SUBSCRIBE request.  This payload usually specifies the request that the websocket is used to service. If
     * body is null or undefined, this method will attempt to call the configurable "getChannelBody" method (if any) to
     * compute the appropriate payload.  Use this body argument as a way to override "getChannelBody" and ask
     * for a specific request.
     */
    openChannel: function(body){
        if (this.get("_subscription")) {
            console.warn("Tried to open a 2nd channel before closing the previous channel.");
            return;
        }

        var websocket = this.get("websocket");
        if (websocket) {
            var me = this,
                channel = this.get("channel"),
                channelDest = this.get("channelDestination");
            if (channel && channelDest) {
                websocket.subscribe(channel, makeMessageCallback(me, channelDest, "_subscription"))
                    .then(function(subscription){
                        me.set("_subscription", subscription);
                        subscription.send(
                            {},
                            body || (me.getChannelBody && me.getChannelBody(0)) || {},
                            channelDest
                        );
                    });
            }
        }
    },

    /**
     * Closes the currently opened channel (if any) and clears the cached data records.
     * @param {Boolean} [clearMe=false] If true, indicates that the cube's data should be cleared.
     */
    closeChannel: function(clearMe){
        var sub = this.get("_subscription");
        if (sub) {
            this.set("_subscription", null);
            sub.unsubscribe();
        }
        if (clearMe) {
            this.clear();
        }
    },

    /**
     * Helper method for closing current channel and immediately opening new channel.
     * @param {Boolean} clearMe This flag is passed directly to closeChannel.  See closeChannel docs for details.
     * @param {Object} body This payload is passed directly to openChannel. See openChannel docs for details.
     */
    resetChannel: function(clearMe, body){
        this.closeChannel(clearMe);
        this.openChannel(body);
    },

    /**
     * Destroys all the generated field objects.
     */
    destroy: function(){
        this.closeChannel(true);
        var fields = this.get("fields");
        Object.keys(fields).forEach(function(f){
            fields[f].destroy();
        });

        this._super();
    }
});


