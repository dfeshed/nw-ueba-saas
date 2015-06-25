/**
 * @file Manages the configuration for ember-cli-mirage
 * @description Lists all the APIs that would return mock data in non-production environment
 * @author Srividhya Mahalingam
 */

import Mirage from 'ember-cli-mirage';

export default function() {
    this.namespace = '/api';

    //When a POST call is make to /api/login check if the credentials passed in the POST body
    //is in the list of users we have added in our system
    this.post('/login', function(db, request) {
        var params = JSON.parse(request.requestBody);
        if (db.logins.where({identification: params.identification, password: params.password})[0]) {
            return {'access_token': 'success'};
        } else{
            return new Mirage.Response(401,  {message: 'invalid credentials'});
        }
    });

    this.get('/users');
    this.get('/devices');

}
