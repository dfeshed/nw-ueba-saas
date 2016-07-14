/**
 * @description populates the records for /api/devices API.
 * This API passes the set of records as JSON (see scenarios/default)
 * This file appends a unique id to each of the records
 * @public
 */

import { Factory } from 'ember-cli-mirage';

export default Factory.extend({
  id: (i) => `${i}`
});
