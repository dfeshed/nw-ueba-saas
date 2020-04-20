/*
* @file simple-auth session initializer
* initializer that injects session as a singleton throughout the app.
* @public
*/

export function initialize(application) {
  application.inject('controller', 'session', 'service:session');
  application.inject('component', 'session', 'service:session');
  application.inject('view', 'session', 'service:session');
}

export default {
  name: 'simple-auth',
  initialize
};
