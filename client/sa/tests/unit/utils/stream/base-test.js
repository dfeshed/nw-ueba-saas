import Stream from '../../../../utils/stream/base';
import { module, test } from 'qunit';

module('Unit | Utility | stream/base');

const INPUT = 'hello world';

test('it exists', function(assert) {
  let stream = Stream.create();
  assert.ok(!!stream);
});

test('it accepts subscribers of type object', function(assert) {
  assert.expect(2);

  let stream = Stream.create();
  stream.subscribe({
    onNext(val) {
      assert.equal(val, INPUT, 'onNext callback was invoked with expected input param');
    },
    onError(val) {
      assert.equal(val, INPUT, 'onError callback was invoked with expected input param');
    },
    onCompleted(val) {
      assert.equal(val, INPUT, 'onCompleted callback was invoked with expected input param');
    }
  });
  stream.next(INPUT);
  stream.completed(INPUT);
});

test('it accepts subscribers of type function', function(assert) {
  assert.expect(2);

  let stream = Stream.create();
  stream.subscribe(
    function(val) {
      assert.equal(val, INPUT, 'onNext callback was invoked with expected input param');
    },
    function(val) {
      assert.equal(val, INPUT, 'onError callback was invoked with expected input param');
    },
    function(val) {
      assert.equal(val, INPUT, 'onCompleted callback was invoked with expected input param');
    }
  );
  stream.next(INPUT);
  stream.completed(INPUT);
});

test('it stops notifying subscribers after it is completed', function(assert) {
  assert.expect(2);

  let stream = Stream.create();
  stream.subscribe({
    onNext(val) {
      assert.equal(val, INPUT, 'onNext callback was invoked with expected input param');
    },
    onError(val) {
      assert.equal(val, INPUT, 'onError callback was invoked with expected input param');
    },
    onCompleted(val) {
      assert.equal(val, INPUT, 'onCompleted callback was invoked with expected input param');
    }
  });
  stream.next(INPUT);
  stream.completed(INPUT);
  stream.next(INPUT);
});

test('it stops notifying subscribers after it errors', function(assert) {
  assert.expect(2);

  let stream = Stream.create();
  stream.subscribe({
    onNext(val) {
      assert.equal(val, INPUT, 'onNext callback was invoked with expected input param');
    },
    onError(val) {
      assert.equal(val, INPUT, 'onError callback was invoked with expected input param');
    },
    onCompleted(val) {
      assert.equal(val, INPUT, 'onCompleted callback was invoked with expected input param');
    }
  });
  stream.next(INPUT);
  stream.error(INPUT);
  stream.next(INPUT);
});

test('it stops notifying subscribes once they unsubscribe', function(assert) {
  assert.expect(1);
  let stream = Stream.create(),
    subscription = stream.subscribe(
      function(val) {
        assert.equal(val, INPUT, 'onNext callback was invoked with expected input param');
      }
    );

  stream.next(INPUT);
  subscription.dispose();
  stream.next(INPUT);
});
