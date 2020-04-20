import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

function setTransportTestingState(transport) {
  transport.setUrl('ws://localhost:32400');
  transport.set('ws', null);
  transport.set('lastTid', 0);
  transport.set('channels', {});
  transport.set('pendingChannels', {});
}

module('Unit | Service | transport', function(hooks) {
  setupTest(hooks);

  test('connect connects', function(assert) {
    assert.timeout(3000);
    assert.expect(1);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    transport.on('connected', () => {
      assert.strictEqual(transport.get('ws').readyState, WebSocket.OPEN);
      done();
    });
    transport.on('error', (err) => {
      throw new Error(err);
    });
    transport.connect();
  });

  test('disconnect disconnects when connected', function(assert) {
    assert.timeout(3000);
    assert.expect(6);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    transport.on('connected', () => {
      const ws = transport.get('ws');
      assert.strictEqual(ws.readyState, WebSocket.OPEN);
      transport.disconnect().then(() => {
        assert.strictEqual(ws.readyState, WebSocket.CLOSED);
        assert.strictEqual(transport.get('lastTid'), 0);
        assert.deepEqual(transport.get('channels'), {});
        assert.deepEqual(transport.get('pendingChannels'), {});
        assert.deepEqual(transport.get('ws'), null);
        done();
      });
    });
    transport.on('error', (err) => {
      throw new Error(err);
    });
    transport.connect();
  });

  test('disconnect does not error when disconnected', function(assert) {
    assert.timeout(3000);
    assert.expect(4);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    transport.disconnect().then(() => {
      assert.strictEqual(transport.get('lastTid'), 0);
      assert.deepEqual(transport.get('channels'), {});
      assert.deepEqual(transport.get('pendingChannels'), {});
      assert.deepEqual(transport.get('ws'), null);
      done();
    });
  });

  test('assertConnected is true when connected', function(assert) {
    assert.timeout(3000);
    assert.expect(2);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    transport.on('connected', () => {
      assert.strictEqual(transport.get('ws').readyState, WebSocket.OPEN);
      assert.strictEqual(transport.assertConnected(), true);
      done();
    });
    transport.on('error', (err) => {
      throw new Error(err);
    });
    transport.connect();
  });

  test('assertConnected returns false when not connected', function(assert) {
    assert.timeout(3000);
    assert.expect(1);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    assert.strictEqual(transport.assertConnected(), false);
    done();
  });

  test('send sends a message and returns a promise with the response', function(assert) {
    assert.timeout(3000);
    assert.expect(2);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    transport.on('connected', () => {
      assert.strictEqual(transport.get('ws').readyState, WebSocket.OPEN);
      transport.send('/', {
        message: 'Foobar'
      }).then((response) => {
        assert.strictEqual(response.params.description, 'Foobar');
        done();
      });
    });
    transport.on('error', (err) => {
      throw new Error(err);
    });
    transport.connect();
  });

  test('stream sends a message and calls back each time a response arrives', function(assert) {
    assert.timeout(3000);
    assert.expect(4);
    const done = assert.async(1);
    const transport = this.owner.lookup('service:transport');
    setTransportTestingState(transport);
    transport.on('connected', () => {
      assert.strictEqual(transport.get('ws').readyState, WebSocket.OPEN);
      const expectedResponses = ['One', 'Two', 'Three'];
      let responsesReceived = 0;
      transport.stream({
        path: '/',
        message: {
          message: 'stream'
        },
        messageCallback: (message) => {
          assert.strictEqual(message.params.streamresponse, expectedResponses[responsesReceived++]);
          if (responsesReceived === 3) {
            done();
          }
        },
        errorCallback: (err) => {
          throw new Error(err);
        }
      });
    });
    transport.on('error', (err) => {
      throw new Error(err);
    });
    transport.connect();
  });
});
