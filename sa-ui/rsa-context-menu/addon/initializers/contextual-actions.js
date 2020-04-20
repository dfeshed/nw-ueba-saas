/*
 * @file contextual-actions initializer
 * initializer that injects the contextual-actions class as a singleton throughout the app.
 * @public
 */
export function initialize(application) {
  application.inject('component', 'contextual-actions', 'service:contextual-actions');
}

export default {
  name: 'contextual-actions-initializer',
  initialize
};