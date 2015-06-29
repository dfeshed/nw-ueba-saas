/**
 * @description Holds the list of valid credentials to authenticate to our system
 * @author Srividhya Mahalingam
 */

import Mirage  from 'ember-cli-mirage';

export default Mirage.Factory.extend({
    identification: 'admin',
    password: 'netwitness'
});
