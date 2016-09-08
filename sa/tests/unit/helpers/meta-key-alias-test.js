import { metaKeyAlias } from 'sa/helpers/meta-key-alias';
import { module, test } from 'qunit';

module('Unit | Helper | meta key alias');

test('it works', function(assert) {
  assert.expect(6);

  const metaName = 'foo';
  const displayName = 'bar';
  const language = [{
    metaName,
    displayName
  }];
  const unknownMetaName = 'baz';
  let result;

  result = metaKeyAlias([ metaName ]);
  assert.equal(result.metaName, metaName, 'Expected result to include the given input.');
  assert.equal(result.displayName, metaName, 'Expected lookup to match input when no language is provided.');

  result = metaKeyAlias([ metaName, language ]);
  assert.equal(result.displayName, displayName, 'Expected lookup to work when language is provided.');

  result = metaKeyAlias([ metaName, language ]);
  assert.equal(result.bothNames, `${displayName} [${metaName}]`, 'Expected result to include both names.');

  result = metaKeyAlias([ unknownMetaName, language ]);
  assert.equal(result.displayName, unknownMetaName, 'Expected unknown meta name to be returned as-is.');

  result = metaKeyAlias([ unknownMetaName, language ]);
  assert.equal(result.bothNames, unknownMetaName, 'Expected unknown meta name to be returned as-is even with option to show 2 names.');
});
