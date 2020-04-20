import { module, test } from 'qunit';
import { convertTreeToList } from 'investigate-hosts/reducers/details/process/util';

module('Unit | Selectors | process');

test('convertTreeToList', function(assert) {
  const data = {
    id: 1,
    parentPid: 0,
    pid: 1,
    name: 'test',
    checksumSha256: 1,
    childProcesses: [
      {
        id: 2,
        parentPid: 1,
        pid: 2,
        name: 'test',
        checksumSha256: 11,
        childProcesses: [
          {
            id: 3,
            parentPid: 2,
            pid: 3,
            name: 'test',
            checksumSha256: 111,
            childProcesses: [

            ]
          },
          {
            id: 4,
            parentPid: 2,
            pid: 4,
            name: 'test',
            checksumSha256: 1,
            childProcesses: [
              {
                id: 7,
                parentPid: 4,
                pid: 7,
                name: 'test',
                checksumSha256: 7
              }
            ]
          }
        ]
      },
      {
        id: 5,
        parentPid: 1,
        pid: 5,
        name: 'test',
        checksumSha256: 5
      },
      {
        id: 6,
        parentPid: 1,
        pid: 6,
        name: 'test',
        checksumSha256: 6
      }
    ]
  };
  const list = convertTreeToList(data); // converts to flat list
  assert.deepEqual(list.length, 7);
});
