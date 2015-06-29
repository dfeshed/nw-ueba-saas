/**
 * @description creates mock API route for authentication related APIs
 * @author Srividhya Mahalingam
 */

import Mirage  from 'ember-cli-mirage';

export default function(config) {
    config.post('/login', function(db, request) {
        var params = JSON.parse(request.requestBody);
        if (db.logins.where({identification: params.identification, password: params.password})[0]) {
            return {'access_token': 'success', 'version': '11.0'};
        } else{
            return new Mirage.Response(401,  {message: 'invalid credentials'});
        }
    });
}
