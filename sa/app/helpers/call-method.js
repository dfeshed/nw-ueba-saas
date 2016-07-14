/**
 * @file Generic method caller.
 * Used for calling named methods in a given context object.
 * @public
 */
import Ember from 'ember';

const { Helper: { helper } } = Ember;

export function callMethod(params/*, hash*/) {
  let [ctxt, methodName] = params;
  if (ctxt && methodName && (typeof ctxt[methodName] === 'function')) {
    return ctxt[methodName].apply(ctxt, params.slice(2));
  } else {
    return undefined;
  }
}

export default helper(callMethod);
