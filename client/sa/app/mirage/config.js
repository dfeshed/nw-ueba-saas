/**
 * @file Manages the configuration for ember-cli-mirage
 * @description Lists all the APIs that would return mock data in non-production environment
 * @author Srividhya Mahalingam
 */

import login from 'sa/mirage/routes/login';
import devices from 'sa/mirage/routes/devices';
import users from 'sa/mirage/routes/users';

export default function() {
    this.namespace = '/api';

    login(this);
    devices(this);
    users(this);
}
