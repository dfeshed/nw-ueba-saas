import sinon from 'sinon';
import { module, test } from 'qunit';

import { Stream, StreamCache } from 'streaming-data/services/data-access';

module('Unit | Utility | stream/stream');

test('can take a stream into the cache and remove it when a route changes', function(assert) {
  assert.expect(1);
  const done = assert.async();

  const stream = Stream.create();
  sinon.stub(stream, 'stop');

  StreamCache.registerStream(stream, '/foo/bar', {});
  StreamCache.cleanUpRouteStreams('/baz/foo');

  setTimeout(function() {
    assert.ok(stream.stop.calledOnce, 'stream\'s stop method was called');
    done();
  }, 10);
});

test('can take a stream into the cache and remove it (by default) when the route just gets deeper', function(assert) {
  assert.expect(1);
  const done = assert.async();

  const stream = Stream.create();
  sinon.stub(stream, 'stop');

  StreamCache.registerStream(stream, '/foo/bar', {});
  StreamCache.cleanUpRouteStreams('/foo/bar/baz');

  setTimeout(function() {
    assert.ok(stream.stop.called, 'stream\'s stop method was called');
    done();
  }, 10);
});

test('can take a stream into the cache and not remove it when the route just gets deeper when configured as such', function(assert) {
  assert.expect(1);
  const done = assert.async();

  const stream = Stream.create();
  sinon.stub(stream, 'stop');

  StreamCache.registerStream(stream, '/foo/bar', { keepAliveOnTransitionToChildRoute: true });
  StreamCache.cleanUpRouteStreams('/foo/bar/baz');

  setTimeout(function() {
    assert.ok(!stream.stop.called, 'stream\'s stop method was not called');
    done();
  }, 10);
});

test('will not manage stream if told that the stream should stay alive on route change', function(assert) {

  const done = assert.async();
  assert.expect(1);

  const stream = Stream.create();
  sinon.stub(stream, 'stop');

  StreamCache.registerStream(stream, '/foo/bar', { keepAliveOnRouteChange: true });
  StreamCache.cleanUpRouteStreams('/baz/foo');

  setTimeout(function() {
    assert.ok(!stream.stop.called, 'stream\'s stop method was not called');
    done();
  }, 10);
});

test('can take an Anon stream into the cache and remove it when a route changes', function(assert) {
  assert.expect(1);
  const done = assert.async();

  const stream = Stream.create();
  sinon.stub(stream, 'stop');

  StreamCache.registerStream(stream, undefined, {});
  StreamCache.cleanUpRouteStreams('/baz/foo');

  setTimeout(function() {
    assert.ok(stream.stop.calledOnce, 'stream\'s stop method was called');
    done();
  }, 10);
});