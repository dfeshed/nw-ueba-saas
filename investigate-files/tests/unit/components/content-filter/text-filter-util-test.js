import { module, test } from 'qunit';
import { evaluateTextAgainstRegEx } from 'investigate-files/components/content-filter/text-filter/utils';


module('Unit | Util | Text filter');

// MD5, SHA1 and SHA256
test('MD5, SHA1 and SHA256 Text filter util test', function(assert) {
  const validValue = [{ value: '18f744c256fe30ff23c2d38dde2fc3cd' }, { value: '4f81738707cc75920928fd5e5954cbcf9950e2f5' }];
  assert.equal(evaluateTextAgainstRegEx(validValue, 'alphaNumericChars'), 0, 'Valid MD5, SHA1 and SHA256');
});

test('MD5, SHA1 and SHA256 Text filter util test', function(assert) {
  const invalidValue = [{ value: '12AS@@##$$%%asdf' }, { value: '12AS@@##$asdf' }];
  assert.equal(evaluateTextAgainstRegEx(invalidValue, 'alphaNumericChars'), 2, 'Invalid MD5, SHA1 and SHA256');
});