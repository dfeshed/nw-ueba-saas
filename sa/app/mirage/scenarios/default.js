/**
 * @file Loads the mirage DB with records for each API listed in mirage/config.js
 * @public
 */
import login from 'sa/mirage/data/login';
import info from 'sa/mirage/data/info';
import asyncFixtures from './async-fixtures';

export default function(server) {

  // Seed your development database using your factories. This
  // data will not be loaded in your tests.
  login(server);
  info(server);

  // Synchronously load fixtures from app/mirage/fixtures into mirage DB collections.
  server.loadFixtures();

  // Load mirage DB collections from any JSON/BSON files for testing here.
  asyncFixtures(server, ['context', 'related-entity', 'liveconnect-feedback']);  // Example, to load file `vendor/incidents.json`: asyncFixtures(server, ['incidents'])
}
