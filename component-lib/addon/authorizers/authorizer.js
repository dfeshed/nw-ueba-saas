/**
* @file Custom authorizer
* @description custom version of ember-simple-auth authorizer that adds headers
* required for all our XHR calls
* @public
*/

import Base from 'ember-simple-auth/authorizers/base';
import csrfToken from '../mixins/csrf-token';

export default Base.extend(csrfToken, {

  authorize(jqXHR) {
    let csrfKey = this.get('csrfLocalstorageKey');
    jqXHR.setRequestHeader('X-CSRF-TOKEN', localStorage.getItem(csrfKey));
  }

});
