import { module, test } from 'qunit';
import { fileListSchema } from 'investigate-hosts/reducers/details/mft-directory/mft-file-schema';
import { normalize } from 'normalizr';

module('Unit | Utils | schema-utils');

test('fileListSchema', function(assert) {
  const data = [{ item: 1, value: 'one' }, { item: 2, value: 'two' }];
  const endData = normalize(data, fileListSchema);
  assert.equal(Object.keys(endData.entities.files).length, 2);
});