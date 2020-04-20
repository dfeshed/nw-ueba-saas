import EmberObject from '@ember/object';
import { deprecate } from '@ember/application/deprecations';

/**
  The base class for all authorizers. __This serves as a starting point for
  implementing custom authorizers and must not be used directly.__

  Authorizers use the session data acquired by an authenticator when
  authenticating the session to construct authorization data that can, e.g., be
  injected into outgoing network requests. Depending on the authorization
  mechanism the authorizer implements, that authorization data might be an HTTP
  header, query string parameters, a cookie, etc.

  __The authorizer has to fit the authenticator__ (see
  {{#crossLink "BaseAuthenticator"}}{{/crossLink}})
  as it can only use data that the authenticator acquires when authenticating
  the session.

  @class BaseAuthorizer
  @module ember-simple-auth/authorizers/base
  @extends Ember.Object
  @deprecated BaseAuthorizer:class
  @public
*/
export default EmberObject.extend({
  init() {
    this._super(...arguments);
    deprecate(`Ember Simple Auth: Authorizers are deprecated in favour of setting headers directly.`,
      false,
      {
        id: 'ember-simple-auth.baseAuthorizer',
        until: '2.0.0',
        url: 'https://github.com/simplabs/ember-simple-auth#deprecation-of-authorizers',
      }
    );
  },
  /**
    Authorizes a block of code. This method will be invoked by the session
    service's {{#crossLink "SessionService/authorize:method"}}{{/crossLink}}
    method which will pass the current authenticated session data (see
    {{#crossLink "SessionService/data:property"}}{{/crossLink}}) and a block.
    Depending on the mechanism it implements, the authorizer transforms the
    session data into authorization data and invokes the block with that data.

    `BaseAuthorizer`'s implementation does nothing. __This method must be
    overridden in custom authorizers.__

    @method authorize
    @param {Object} data The current authenticated session data
    @param {Function} block The callback to call with the authorization data
    @public
  */
  authorize() {}
});
