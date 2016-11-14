/**
 * @file Async fixtures loader.
 * Loads fixtures from JSON files into Mirage DB collections in an async manner, to avoid locking up the browser.
 * Minimally supports loading data from BSON files and post-processing the data before loading it into Mirage's in-memory db.
 * Caches a Promise on the Mirage server so that subsequent code can wait for this async loading to finish.
 * @public
 */
import Ember from 'ember';
import bson from 'sa/utils/bson';
import Thread from 'sa/utils/thread';

const { RSVP, $ } = Ember;

export default function(server, collectionNames) {
  collectionNames = collectionNames || [];

  let promise;

  if (!collectionNames.length) {

    // No collections to load, so resolve an empty promise.
    promise = RSVP.resolve();
  } else {
    // We have collections to load. For each, fetch via Ajax & cache a promise.
    const defs = collectionNames.map((name) => {
      return new RSVP.Promise((resolve) => {

        $.ajax({
          method: 'GET',
          url: `/vendor/${name}.json`,
          dataType: 'text'
        })
          .fail(function() {
            // @TODO: for some reason this ajax call fails intermittently when running UTs in browser and
            // consistently fails in phantomjs environment. creating an empty collection and resolving the promise
            // while we figure out the root cause
            server.db.createCollection(name);
            resolve();
          })
          .done(function(responseText) {

            // Create a Mirage collection to store the data.
            server.db.createCollection(name);
            const collection = server.db[name];

            // Feed the data into the collection on an interval thread, to avoid locking browser.
            Thread.create({
              queue: JSON.parse(bson.toJson(responseText, true)),
              rate: 10,
              interval: 17,
              onNextBatch(arr) {
                collection.insert(arr);
              },
              onCompleted() {
                resolve();
              }
            }).start();
          });
      });
    });

    // Create a composite promise from all the individual collections' promises above.
    promise = RSVP.Promise.all(defs);
  }

  // Give all our MockSocket servers (if any) a handle to this composite promise too, so they can detect when async
  // data has finished loading and use it, if they wish.
  (window.MockServers || []).forEach(function(mockServer) {
    mockServer.asyncFixturesPromise = promise;
    mockServer.mirageServer = server;
  });

  return promise;
}
