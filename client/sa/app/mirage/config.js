/**
 * @file Manages the configuration for ember-cli-mirage
 * @description Lists all the APIs that would return mock data in non-production environment
 * @author Srividhya Mahalingam
 */

import passthrough from "sa/mirage/routes/passthrough";

import login from "sa/mirage/routes/login";
import devices from "sa/mirage/routes/devices";
import users from "sa/mirage/routes/users";
import info from "sa/mirage/routes/info";
import initSockets from "sa/mirage/config-sockets";


import connect from "sa/mirage/sockets/connect";
import disconnect from "sa/mirage/sockets/disconnect";
import echo from "sa/mirage/sockets/echo";
import files from "sa/mirage/sockets/files";

export default function() {
    /* initalize the list of all apis that doesn't need mirage*/
    passthrough(this);

    this.namespace = "/api";

    login(this);
    devices(this);
    users(this);
    info(this);

    var server = initSockets();
    connect(server);
    disconnect(server);
    echo(server);
    files(server);
}
