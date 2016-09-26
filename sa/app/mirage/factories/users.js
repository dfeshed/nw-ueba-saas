/**
 * @description Specifies the list of keys and values that must be created and
 * populated for the /api/users call
 * @public
 */

import { faker, Factory } from 'ember-cli-mirage';

const users = ['admin', 'Ian', 'Justin', 'Tony', 'Bo'];

export default Factory.extend({

  firstName: faker.name.firstName,
  lastName: faker.name.lastName,
  friendlyName(i) {
    return users[i % users.length];
  },
  email(i) {
    return `person${i}@test.com`;
  }

});
