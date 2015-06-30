/**
 * @file Loads the mirage DB with records for each API listed in mirage/config.js
 * @author Srividhya Mahalingam
 */
import login from 'sa/mirage/data/login';
import devices from 'sa/mirage/data/devices';
import users from 'sa/mirage/data/users';

export default function( server ) {

    // Seed your development database using your factories. This
    // data will not be loaded in your tests.
    login(server);
    devices(server);
    users(server);
}
