import { module, test } from 'qunit';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';

module('Unit | Selectors | Dictionaries');

const language = [
  { metaName: 'ip.src', format: 'IPV4' },
  { metaName: 'device.type', format: 'Text' },
  { metaName: 'sessionid', format: 'Unicode' }
];

test('get meta format map for language', function(assert) {
  const metaFormat = metaFormatMap(language);
  assert.equal(metaFormat['ip.src'], 'IPV4');
  assert.equal(metaFormat['device.type'], 'Text');
  assert.equal(metaFormat.sessionid, 'Unicode');
});
