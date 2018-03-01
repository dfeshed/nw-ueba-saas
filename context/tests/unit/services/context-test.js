import { moduleFor, test } from 'ember-qunit';
import { typeOf } from '@ember/utils';
import { next } from '@ember/runloop';

moduleFor('service:context', 'Unit | Service | context', {
  // Specify the other units that are required for this test.
  needs: ['service:request']
});

test('it can fetch a list of entity types', function(assert) {
  assert.expect(2);

  const service = this.subject();
  assert.ok(service);

  const done = assert.async();
  service.types().then((response) => {
    assert.ok(response, 'Expected promise callback to be invoked with a response.');
    done();
  });
});

test('fetching entity types twice will piggyback on the first request', function(assert) {
  assert.expect(2);

  const service = this.subject();
  const done = assert.async();

  // Request the entity types once...
  const firstPromise = service.types();
  firstPromise.then((firstResponse) => {

    // ...and after it succeeds, request the entity types again.
    const secondPromise = service.types();
    secondPromise.then((secondResponse) => {
      assert.equal(firstPromise, secondPromise, 'Expected first promise to be re-used for second request.');
      assert.equal(firstResponse, secondResponse, 'Expected first response to be re-used as second response.');
      done();
    });
  });
});

test('it can stream summary data for an entity, and the given stop function will stop the stream', function(assert) {
  const waitForResponses = 3;
  const entities = (new Array(waitForResponses + 1))
    .fill(0)
    .map((d, i) => ({ type: 'foo', id: String(i) }));

  assert.expect(waitForResponses);
  const done = assert.async();
  const service = this.subject();
  let responsesReceived = 0;
  const stopFn = service.summary(entities, (response) => {
    assert.ok(response, 'Expected stream callback to be invoked with a response.');

    responsesReceived++;
    if (responsesReceived === waitForResponses) {
      if (typeOf(stopFn) === 'function') {
        stopFn();
        done();
      } else {
        assert.notOk(true, 'Expected to receive a stop function to shut off the stream.');
      }
    }
  });
});

test('it can fetch a map of meta keys to entity types for an endpoint', function(assert) {
  assert.expect(2);

  const service = this.subject();
  assert.ok(service);

  const done = assert.async();
  service.metas('endpointId1').then((response) => {
    assert.ok(response, 'Expected promise callback to be invoked with a response.');
    done();
  });
});

test('fetching meta key maps twice for the same endpoint will piggyback on the first request', function(assert) {
  assert.expect(2);

  const service = this.subject();
  const done = assert.async();

  // Request the entity types once...
  const firstPromise = service.metas('CORE');
  firstPromise.then((firstResponse) => {

    // ...and after it succeeds, request the entity types again.
    const secondPromise = service.metas('CORE');
    secondPromise.then((secondResponse) => {
      assert.equal(firstPromise, secondPromise, 'Expected first promise to be re-used for second request.');
      assert.equal(firstResponse, secondResponse, 'Expected first response to be re-used as second response.');
      done();
    });
  });
});

test('fetching meta key maps for 2 different endpoints will not piggyback on the first request', function(assert) {
  assert.expect(2);

  const service = this.subject();
  const done = assert.async();

  // Request the entity types once...
  const firstPromise = service.metas('IM');
  firstPromise.then((firstResponse) => {

    // ...and after it succeeds, request the entity types again.
    const secondPromise = service.metas('CORE');
    secondPromise.then((secondResponse) => {
      assert.notEqual(firstPromise, secondPromise, 'Expected first promise to not be re-used for second request.');
      assert.notEqual(firstResponse, secondResponse, 'Expected first response to not be re-used as second response.');
      done();
    });
  });
});

test('it retrieves the meta key map for Incident Management from this addon\'s config', function(assert) {
  assert.expect(2);

  const service = this.subject();
  assert.ok(service);

  const done = assert.async();
  service.metas('IM').then((response) => {
    assert.ok(response, 'Expected promise callback to be invoked with a response.');
    done();
  });
});

test('it fires callback with an error status when the request for summary data returns an error code', function(assert) {

  // Hijack request service to simulate an error response for any summary data request.
  const service = this.subject();
  const request = service.get('request');
  request.streamRequest = ({ onInit, onError }) => {
    onInit();
    next(onError);
  };

  // Submit a request for summary data, and wait for the callback.
  // Warning: the service uses caching, so don't test below with an entity that was already tested earlier.
  assert.expect(3);
  const done = assert.async();
  const entity = { type: 'type1', id: 'id1' };
  const callback = (type, id, status) => {
    assert.equal(type, entity.type, 'Expected error callback to receive entity type');
    assert.equal(id, entity.id, 'Expected error callback to receive entity id');
    assert.equal(status, 'error', 'Expected error callback to receive an error status');
    done();
  };
  service.summary([ entity ], callback);
});
