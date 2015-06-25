/**
 * @description Specifies the list of keys and values that must be created and
 * populated for the /api/users call
 * @author Srividhya Mahalingam
 */

import Mirage, {faker}  from 'ember-cli-mirage';

export default Mirage.Factory.extend({

    firstName: faker.name.firstName,
    lastName: faker.name.firstName,
    zipCode: faker.address.zipCode,
    email: function(i) {
        return 'person' + i + '@test.com';
    }

});
