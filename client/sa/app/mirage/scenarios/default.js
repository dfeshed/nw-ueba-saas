/**
 * @file Loads the mirage DB with records for each API listed in mirage/config.js
 * @public
 */
import login from 'sa/mirage/data/login';
import users from 'sa/mirage/data/users';
import info from 'sa/mirage/data/info';
import asyncFixtures from './async-fixtures';

export default function(server) {

  // Seed your development database using your factories. This
  // data will not be loaded in your tests.
  login(server);
  users(server);
  info(server);

  // Load mirage DB collections from any JSON/BSON files for testing here.
  asyncFixtures(server, ['incident']);  // Example, to load file `vendor/incidents.json`: asyncFixtures(server, ['incidents'])
}
