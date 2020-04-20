import { module, test } from 'qunit';
import { filterValidMeta } from 'investigate-events/util/meta';
import { metaIsIndexedByNoneUInt16 as metaConfigIsIndexedByNone,
  metaSessionidIsIndexedByNone as metaConfigSessionid,
  metaIsIndexedByValueUInt16 as metaConfigIsIndexedByValue,
  metaIsIndexedByKeyUInt64 as metaConfigIsIndexedByKey
} from '../../helpers/meta-data-helper';

module('Unit | Util | meta');

test('FilterValidMeta filters out isIndexedByNone meta', function(assert) {
  const isIndexedByNoneMetaOptions = [metaConfigIsIndexedByNone];
  const count = isIndexedByNoneMetaOptions.filter(filterValidMeta).length;
  assert.equal(count, 0, 'Did not filter out isIndexedByNone meta');
});

test('FilterValidMeta does not filter out meta if metaName is sessionid', function(assert) {
  const sessionidMetaOptions = [metaConfigSessionid];
  const count = sessionidMetaOptions.filter(filterValidMeta).length;
  assert.equal(count, sessionidMetaOptions.length, 'Shall not filter out metaName sessionid');
});

test('FilterValidMeta only filters out invalid meta', function(assert) {
  const metaOptions = [
    metaConfigIsIndexedByNone,
    metaConfigSessionid,
    metaConfigIsIndexedByValue,
    metaConfigIsIndexedByKey
  ];
  const count = metaOptions.filter(filterValidMeta).length;
  assert.equal(count, 3, 'Shall filter out invalid meta only');
});
