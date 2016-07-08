/**
 * @description creates mock API route for authentication related APIs
 * When login successful, returns a response that includes username but not password (like our real login service).
 * @public
 */

import Mirage  from 'ember-cli-mirage';
import {parsePostData} from 'sa/mirage/helpers/utils';

export default function(config) {
  config.post('/user/login', function(db, request) {
    let params = parsePostData(request.requestBody);
    if (db.logins.where({ username: params.username, password: params.password })[0]) {
      return { 'access_token': 'success', username: params.username, password: null };
    } else {
      return new Mirage.Response(401,  { message: 'invalid credentials' });
    }
  });

  config.post('/user/logout', function() {
    });
}
