import contextEntityTypeByMeta from 'sa/utils/context/entity-type-by-meta';
import { module, test } from 'qunit';
import config from 'sa/config/environment';

module('Unit | Utility | context/entity type by meta');

const cfg = {
  entityTypes: [
    {
      name: 'IP',
      enabled: true,
      metaKeys: [
        'ip.src',
        'ip.dst'
      ]
    },
    {
      name: 'IP-Disabled',
      enabled: false,
      metaKeys: [
        'ipv6.src',
        'ipv6.dst'
      ]
    }
  ]
};

test('it works', function(assert) {
  const originalEntityTypes = config.contextLookup;
  config.contextLookup = cfg;

  assert.equal(
    contextEntityTypeByMeta('ip.dst').name,
    'IP',
    'Expected to match meta key for an enabled entity type'
  );

  assert.equal(
    contextEntityTypeByMeta('ipv6.dst'),
    undefined,
    'Expected to not match meta key for a disabled entity type'
  );

  assert.equal(
    contextEntityTypeByMeta('foo'),
    undefined,
    'Expected not to match an unknown meta key'
  );

  config.contextLookup = originalEntityTypes;
});
