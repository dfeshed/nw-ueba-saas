/**
 * Helper function to create the reducer instance.
 * @param reducerFunction
 * @param reducerPredicate
 * @returns {function(*=, *=): *}
 * @public
 */
export const createFilteredReducer = (reducerFunction, reducerPredicate) => {
  return (state, action) => {
    const isInitializationCall = state === undefined;
    const shouldRunWrappedReducer = reducerPredicate(action) || isInitializationCall;
    return shouldRunWrappedReducer ? reducerFunction(state, action) : state;
  };
};
