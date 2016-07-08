/**
 * @description Holds the list of valid credentials to authenticate to our system
 * @public
 */

import Mirage  from 'ember-cli-mirage';

const logins = ['admin', 'Ian', 'Justin', 'Tony'];

export default Mirage.Factory.extend({
  username(i) {
    return logins[i % logins.length];
  },
  password: 'netwitness'
});
