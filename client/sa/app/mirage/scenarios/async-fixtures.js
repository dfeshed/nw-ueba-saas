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

export default function(server, collectionNames) {
  collectionNames = collectionNames || [];

  let promise;

  if (!collectionNames.length) {

    // No collections to load, so resolve an empty promise.
    promise = Ember.RSVP.resolve();
  } else {

    // We have collections to load. For each, fetch via Ajax & cache a promise.
    let defs = collectionNames.map((name) => {
      return new Ember.RSVP.Promise((resolve, reject) => {

        Ember.$.ajax({
          method: 'GET',
          url: `/vendor/${name}.json`,
          dataType: 'text'
        })
          .fail(reject)
          .done(function(responseText) {

            // Create a Mirage collection to store the data.
            server.db.createCollection(name);
            let collection = server.db[name];

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
    promise = Ember.RSVP.Promise.all(defs);
  }

  // Give all our MockSocket servers (if any) a handle to this composite promise too, so they can detect when async
  // data has finished loading and use it, if they wish.
  (window.MockServers || []).forEach(function(mockServer) {
    mockServer.asyncFixturesPromise = promise;
    mockServer.mirageServer = server;
  });

  return promise;
}
