import actionCreators from './creators';

/**
 * The Action (Creator) Broker is responsible for looking up a redux action creator function and dispatching it.
 *
 * The function takes a namespace string (e.g., 'incidents', 'alerts', 'remediationTasks') and an actionCreatorName (a
 * string representation of the action creator function (e.g., 'toggleFilterPanel'), and resolves the actual action
 * creator function. It then invokes/dispatches the action creator, passing along any additional arguments as provided.
 *
 * The rationale currently for having this lookup is to be able to create abstract, reusable components (e.g., rsa-explorer)
 * that have built in dispatchToActions functions, but which do not need (or want) to know specifically about the exact
 * action creator being dispatched. Instead, the reusable component is given a namespace key to lookup the function it needs
 * at run-time.
 *
 * All 'rsa-explorer' components, for example, need to support the same action functionality
 * (e.g., fetchItems, toggleFilterPanel, resetFilters, updateFilter, etc), but each action creator is slightly different
 * in terms of the specific action type that is dispatched, even if the payloads are the same. The action broker is the
 * first step in trying to DRY up the implementation of identical functionality.
 *
 * The consumer of this kind of reusable component will declare the namespace on the component, and the component will then resolve
 * the appropriate action creator dynamically.
 * @param dispatch
 * @param namespace
 * @param actionCreatorName
 * @param args
 * @public
 */
export default function(dispatch, namespace, actionCreatorName, ...args) {
  if (actionCreators[namespace] && actionCreators[namespace][actionCreatorName]) {
    dispatch(actionCreators[namespace][actionCreatorName](...args));
  }
}