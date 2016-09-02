import { metaValueAlias } from 'sa/helpers/meta-value-alias';
import { module, test } from 'qunit';

module('Unit | Helper | meta value alias');

test('it works', function(assert) {
  const raw = 'foo';
  const rawAlias = 'foo-alias';
  const key = 'ip.src';
  const opts = {
    aliases: {}
  };
  opts.aliases[key] = {};
  opts.aliases[key][raw] = rawAlias;
  assert.equal(metaValueAlias([ key, raw, opts ]), rawAlias);
});
