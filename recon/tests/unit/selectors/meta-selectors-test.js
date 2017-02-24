import { isHttpData, isNotHttpData } from 'recon/selectors/meta-selectors';
import { module, test } from 'qunit';

module('Unit | Mixin | meta-selectors');

const generateHttpDataTests = function(selector) {
  return {
    shouldNotBeHttpData: selector({
      data: {
        meta: []
      }
    }),
    shouldAlsoNotBeHttpData: selector({
      data: {
        meta: [['service', 0]]
      }
    }),
    shouldBeHttpData: selector({
      data: {
        meta: [['service', 80]]
      }
    })
  };
};

test('isHttpData', function(assert) {
  assert.expect(3);
  const tests = generateHttpDataTests(isHttpData);
  assert.equal(tests.shouldNotBeHttpData, false, 'isHttpData should return false when no meta');
  assert.equal(tests.shouldAlsoNotBeHttpData, false, 'isHttpData should return false for non http service');
  assert.equal(tests.shouldBeHttpData, true, 'isHttpData should return true for http events');
});

test('isNotHttpData', function(assert) {
  assert.expect(3);
  const tests = generateHttpDataTests(isNotHttpData);
  assert.equal(tests.shouldNotBeHttpData, true, 'isNotHttpData should return true when no meta');
  assert.equal(tests.shouldAlsoNotBeHttpData, true, 'isNotHttpData should return true for non http service');
  assert.equal(tests.shouldBeHttpData, false, 'isNotHttpData should return false for http events');
});