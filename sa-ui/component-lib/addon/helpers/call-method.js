/**
 * @file Generic method caller.
 * Used for calling named methods in a given context object.
 * @public
 */
import { helper } from '@ember/component/helper';

export function callMethod(params /* , hash */) {
  const [ctxt, methodName] = params;
  if (ctxt && methodName && (typeof ctxt[methodName] === 'function')) {
    return ctxt[methodName](...params.slice(2));
  } else {
    return undefined;
  }
}

export default helper(callMethod);
