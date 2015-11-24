/**
 * @description populates the records for all all authentication related APIs.
 * @author Srividhya Mahalingam
 */

export default function(server) {
    //Create the authenticated users listed in factories/login.js
    server.createList('login', 4);
}
