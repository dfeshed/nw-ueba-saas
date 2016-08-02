/**
* @file Custom authorizer
* @description custom version of ember-simple-auth authorizer that adds headers
* required for all our XHR calls
* @public
*/

import Ember from 'ember';
import OAuth2BearerAuthorizer from 'ember-simple-auth/authorizers/oauth2-bearer';
import oauthToken from '../mixins/oauth-token';
const { isEmpty } = Ember;
export default OAuth2BearerAuthorizer.extend(oauthToken, {

  authorize(data, block) {
    const accessToken = data.access_token;

    if (!isEmpty(accessToken)) {
      block('Authorization', `Bearer ${accessToken}`);
    }
  }

});
