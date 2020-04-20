import { module, test } from 'qunit';
import {
  _sortedMetaItems,
  groupByAlphabets
} from 'recon/utils/meta-grouping-util';
import _ from 'lodash';

const metaItems = [[ 'size', 62750 ],
  [ 'payload', 56460 ],
  [ 'eth.src', '70:56:81:9A:94:DD' ],
  [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
];

module('Unit | Utility | Meta Grouping Util');

test('sortedMetaItems returns sorted meta items for non-empty meta items', function(assert) {
  const metaItems = [[ 'size', 62750 ],
    [ 'OS', 'windows' ],
    [ 'eth.src', '70:56:81:9A:94:DD' ],
    [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
  ];
  const sortedMetaItems = _sortedMetaItems(metaItems);

  const expectedSortedMetaItems = [[ 'eth.dst', '10:0D:7F:75:C4:C8' ],
    [ 'eth.src', '70:56:81:9A:94:DD' ],
    [ 'OS', 'windows' ],
    [ 'size', 62750 ]
  ];

  assert.ok(_.isEqual(sortedMetaItems, expectedSortedMetaItems));
});

test('sortedMetaItems returns empty array for empty meta items', function(assert) {
  const sortedMetaItems = _sortedMetaItems([]);

  assert.equal(sortedMetaItems.length, 0);
});

test('groupByAlphabets returns meta items grouped by alphabets for non-empty meta items', function(assert) {
  const groupedMetaItems = groupByAlphabets(metaItems);

  const expectedGroupedMetaItems = [
    {
      group: 'E',
      children: [[ 'eth.dst', '10:0D:7F:75:C4:C8' ], [ 'eth.src', '70:56:81:9A:94:DD' ]]
    },
    {
      group: 'P',
      children: [[ 'payload', 56460 ]]
    },
    {
      group: 'S',
      children: [[ 'size', 62750 ]]
    }
  ];

  assert.ok(_.isEqual(groupedMetaItems, expectedGroupedMetaItems));
});

test('groupByAlphabets returns empty array for empty meta items', function(assert) {
  const groupedMetaItems = groupByAlphabets([]);

  assert.equal(groupedMetaItems.length, 0);
});
