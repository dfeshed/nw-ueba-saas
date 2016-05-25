/**
 * @description Specifies the list of keys and values that must be created and
 * populated for the /api/users call
 * @public
 */

import Mirage, {faker}  from 'ember-cli-mirage';

export default Mirage.Factory.extend({

  firstName: faker.name.firstName,
  lastName: faker.name.lastName,
  email(i) {
    return `person${i}@test.com`;
  }

});
