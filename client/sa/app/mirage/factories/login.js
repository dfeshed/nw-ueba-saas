/**
 * @description Holds the list of valid credentials to authenticate to our system
 * @author Srividhya Mahalingam
 */

import Mirage  from "ember-cli-mirage";

var logins = ["admin", "Ian", "Justin", "Tony"];

export default Mirage.Factory.extend({
    username: function(i) { return logins[i % logins.length]; },
    password: "netwitness"
});
