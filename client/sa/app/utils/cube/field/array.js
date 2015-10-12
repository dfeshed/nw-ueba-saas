/**
 * @file  Array Field class.
 * Represents a field in a crossfilter of records whose values are of type ARRAY.
 */
import Ember from "ember";
import ENUM_DIM_TYPE from "./enum-type";
import DefaultDim from "./default";
import Filter from "sa/utils/cube/filter/array";

/**
 * Helper function to be used as a reduceAdd when creating a crossfilter group for a dimension whose values are arrays.
 * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
 * @param {String} propertyName The property name used by the dimension's getter.
 * @private
 */
function _makeReduceAddForArrayDimension(propertyName) {
    return function (p, v) {
        (v[propertyName] || []).forEach (function(val) {
            p[val] = (p[val] || 0) + 1; //increment counts
        });
        return p;
    };
}

/**
 * Helper function to be used as a reduceRemove when creating a crossfilter group for a dimension whose values are arrays.
 * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
 * @param {String} propertyName The property name used by the dimension's getter.
 * @private
 */
function _makeReduceRemoveForArrayDimension(propertyName) {
    return function(p, v) {
        (v[propertyName] || []).forEach (function(val) {
            p[val] = (p[val] || 0) - 1; //decrement counts
        });
        return p;
    };

}

/**
 * Helper function to be used as a reduceInitial when creating a crossfilter group for a dimension whose values are arrays.
 * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
 * @private
 */
function _reduceInitial() {
    return {};
}

export default DefaultDim.extend({
    type: ENUM_DIM_TYPE.ARRAY,

    filter: Ember.computed(function() {
        return Filter.create({field: this});
    }),

    grouping: function(){
        var prop = this.get("propertyName");
        return this.get("dimension").groupAll()
            .reduce(
            _makeReduceAddForArrayDimension(prop),
            _makeReduceRemoveForArrayDimension(prop),
            _reduceInitial
        );

    }.property("dimension"),

    /**
     * Generates an array of grouped values and their respective counts for a field whose values are arrays.
     * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
     * @type Object[]
     */
    groups: function(){
        var results = this.get("cube.results"),
            totalCount = results && results.length,
            groups = this.get("grouping").value();
        groups.all = function() {
            var newObject = [];
            for (var key in this) {
                if (this.hasOwnProperty(key) && key !== "all") {
                    newObject.push({
                        key: key,
                        value: this[key]
                    });
                }
            }
            return newObject;
        };
        var all = groups.all(),
            maxCount = all.reduce(function(p, v) {
                return ( p > v.value ? p : v.value );
            }, 1),
            out = all.map(function(group){
                return {
                    key: group.key,
                    value: group.value,
                    valuePercent: totalCount ? group.value / totalCount : group.value,
                    max: maxCount,
                    maxPercent: maxCount ? group.value / maxCount : group.value
                };
            }),
            hash = {};
        out.forEach(function(item) {
            hash[item.key] = item;
        });
        out.hash = hash;
        return out;
    }.property("grouping", "cube.results")
});
