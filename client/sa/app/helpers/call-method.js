/**
 * @file Generic method caller.
 * Used for calling named methods in a given context object.
 */
import Ember from "ember";

export function callMethod(params/*, hash*/) {
    var ctxt = params[0],
        methodName = params[1];
    if (ctxt && methodName && (typeof ctxt[methodName] === "function")) {
        return ctxt[methodName].apply(ctxt, params.slice(2));
    }
    else {
        return undefined;
    }
}

export default Ember.Helper.helper(callMethod);
