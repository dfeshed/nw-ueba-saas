import { module, test } from 'qunit';

module('Unit | Util | esnext');

test('Numeric Separator', function(assert) {
  const foo = 100_000_000;
  const bar = 100000000;
  assert.equal(foo, bar);
});

test('Nullish Coalescing Operator', function(assert) {
  let duration = (x) => {
    return x || 300;
  };
  assert.equal(duration(0), 300);
  assert.equal(duration(), 300);

  duration = (x) => {
    return x ?? 300;
  };
  assert.equal(duration(0), 0);
  assert.equal(duration(), 300);
});

test('throw expressions 1', function(assert) {
  assert.expect(1);

  const save = (filename = throw new Error('filename required')) => {
    assert.ok(false, filename);
  };

  try {
    save();
    assert.ok(false);
  } catch (err) {
    assert.equal(err.toString(), 'Error: filename required');
  }
});

test('throw expressions 2', function(assert) {
  assert.expect(1);

  let UTF8Encoder;
  const getEncoder = (encoding) => {
    return encoding === 'utf8' ?
      new UTF8Encoder() :
      throw new Error('Unsupported encoding');
  };

  try {
    getEncoder('foo');
    assert.ok(false);
  } catch (err) {
    assert.equal(err.toString(), 'Error: Unsupported encoding');
  }
});

test('Logical Assignment Operators', function(assert) {

  const obj = {
    x: null
  };

  // if no obj.x, then set it to 2
  obj.x ||= 2;
  assert.equal(obj.x, 2);

  // if obj.x, then set it to 7
  obj.x &&= 7;
  assert.equal(obj.x, 7);

  // if obj.x, then set it to 7
  obj.x = 0;
  obj.x &&= 7;
  assert.equal(obj.x, 0);
});

// PIPELINE

test('pipeline operator 1', function(assert) {
  const double = (x) => x * 2;
  const subtractFive = (x) => x - 5;
  const repeatOnce = (x) => `${x}${x}`;

  const foo = (num) => {
    return repeatOnce(subtractFive(double(num)));
  };

  assert.equal(foo(10), '1515');


  const bar = (num) => {
    return num
      |> double
      |> subtractFive
      |> repeatOnce;
  };

  assert.equal(bar(10), '1515');
});

test('pipeline operator 2', function(assert) {
  const add = (x, y) => x + y;
  const subtract = (x, y) => x - y;
  const repeatOnce = (x) => `${x}${x}`;

  const foo = (num) => {
    return num
      |> ((x) => add(x, 20))
      |> ((x) => subtract(x, 5))
      |> repeatOnce;
  };

  assert.equal(foo(10), '2525');
});

test('Do expressions 1', function(assert) {
  const foo = 10;

  const x = do {
    const tmp = foo;
    tmp * tmp + 1;
  };

  assert.equal(x, 101);
});

test('Do expressions 2', function(assert) {
  const foo = 10;
  const setting = do {
    if (foo < 5) {
      'low';
    } else if (foo < 10) {
      'medium';
    } else {
      'high';
    }
  };

  assert.equal(setting, 'high');
});

test('optional chaining', function(assert) {
  const obj = {
    abc: {
      def: {
        ghi: {
          jkl: {
            value: 104
          }
        }
      }
    }
  };

  // The old way
  let e;
  if (obj && obj.abc && obj.abc.def && obj.abc.def.ghi && obj.abc.def.ghi.jkl) {
    e = obj.abc.def.ghi.jkl.value;
  }
  assert.equal(e, 104);

  // The new way
  const ee = obj?.abc?.def?.ghi?.jkl?.value;
  assert.equal(ee, 104);
});

// Polyfills

test('set functions 1', function(assert) {
  const a = new Set([1, 2, 3]);
  const b = new Set([3, 4, 5]);

  // all elements in both combined and deduped
  const c = a.union(b);
  assert.deepEqual(Array.from(c), [1, 2, 3, 4, 5]);

  // elements that are only in both
  const d = a.intersection(b);
  assert.deepEqual(Array.from(d), [3]);

  // elements in a that aren't in b
  const e = a.difference(b);
  assert.deepEqual(Array.from(e), [1, 2]);

  // elements in b that aren't in a
  const f = b.difference(a);
  assert.deepEqual(Array.from(f), [4, 5]);

  // elements in b that aren't in a
  const g = a.symmetricDifference(b);
  assert.deepEqual(Array.from(g), [1, 2, 4, 5]);
});

test('set functions 2', function(assert) {
  const a = new Set([1, 2, 3]);
  const b = new Set([1, 2, 3, 4, 5, 6, 7]);

  const c = a.isSubsetOf(b);
  assert.equal(c, true);

  const d = b.isSubsetOf(a);
  assert.equal(d, false);

  const e = a.isSupersetOf(b);
  assert.equal(e, false);

  const f = b.isSupersetOf(a);
  assert.equal(f, true);

  const g = a.isDisjointFrom(b);
  assert.equal(g, false);

  const h = (new Set([1, 2, 3])).isDisjointFrom(new Set([4, 5, 6]));
  assert.equal(h, true);
});

test('string.replaceAll', function(assert) {
  const str = 'swayze+drogon+housestark+beatles+lumberjanes';
  const withSpacesRE = str.replace(/\+/g, ' ');
  assert.equal(withSpacesRE, 'swayze drogon housestark beatles lumberjanes');

  const withSpacesRA = str.replaceAll('+', ' ');
  assert.equal(withSpacesRA, 'swayze drogon housestark beatles lumberjanes');
});

test('Array.lastItem/lastIndex 1', function(assert) {
  const foo = [1, 2, 3, 4, 5, 6, 7, 11, 15];

  // last index
  assert.equal(foo.length - 1, 8);

  // last item
  assert.equal(foo[foo.length - 1], 15);

  assert.equal(foo.lastItem, 15);
  assert.equal(foo.lastIndex, 8);
});

test('Array.lastItem 2', function(assert) {
  const foo = [1, 2, 3, 4, 5, 6, 7, 11, 15];
  foo.lastItem = 561;
  assert.equal(foo[8], 561);
});

// Partial application
// not working/compiling for some reason, look into this later
//
// test('Partial application 1', function(assert) {
//   const add = (x, y) => x + y;
//   const subtract = (x, y) => x - y;
//   const repeatOnce = (x) => `${x}${x}`;

//   const foo = (num) => {
//     return num
//       |> add(?, 20)
//       |> subtract(?, 5)
//       |> repeatOnce;
//   };

//   assert.equal(foo(10), '2525');
// });