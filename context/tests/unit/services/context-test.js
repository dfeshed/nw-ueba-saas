import { moduleFor, test } from 'ember-qunit';
import { typeOf } from 'ember-utils';

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
  const firstPromise = service.metas('endpointId1');
  firstPromise.then((firstResponse) => {

    // ...and after it succeeds, request the entity types again.
    const secondPromise = service.metas('endpointId1');
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
  const firstPromise = service.metas('endpointId1');
  firstPromise.then((firstResponse) => {

    // ...and after it succeeds, request the entity types again.
    const secondPromise = service.metas('endpointId2');
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

