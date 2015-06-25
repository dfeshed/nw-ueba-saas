/**
 * @file Loads the mirage DB with records for each API listed in mirage/config.js
 * @author Srividhya Mahalingam
 */

export default function( server ) {

    // Seed your development database using your factories. This
    // data will not be loaded in your tests.

    //Create the authenticated users listed in factories/login.js
    server.createList('login', 1);

    //Create 5 users with the schema listed in factories/users.js
    server.createList('users', 5);

    //Make a create call and pass the json that needs to be created
    server.create('devices', {"username":"admin","deviceType":"LOG_DECODER","deviceVersion":"10.4.0.2.3360","displayName":"LAB-SA-LOGDECODER - Log Decoder"});
    server.create('devices', {"username":null,"deviceType":"BROKER","deviceVersion":"10.4.0.2.3360","displayName":"SA-LAB-BROKER - Broker"});
    server.create('devices', {"username":"admin","deviceType":"CONCENTRATOR","deviceVersion":"10.4.0.2.3360","displayName":"LAB-SA-CONCENTRATOR - Concentrator"});
    server.create('devices', {"username":null,"deviceType":"DECODER","deviceVersion":"10.4.0.2.3360","displayName":"LAB-SA-PACKETDECODER - Decoder"});
}
