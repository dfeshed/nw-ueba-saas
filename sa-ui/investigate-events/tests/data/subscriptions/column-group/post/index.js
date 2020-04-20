import { columnGroups, BASE_COLUMNS } from '..';
import _ from 'lodash';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/column/groups/set',
  requestDestination: '/ws/investigate/column/groups/set',
  message(frame) {
    const body = JSON.parse(frame.body);
    const num = Date.now();
    const newGroup = {
      'id': body.columnGroup.id ? body.columnGroup.id : num,
      'name': body.columnGroup.name,
      'contentType': 'USER',
      // simulate inclusion of time,medium in response for set columnGroup
      'columns': _.uniqBy([...BASE_COLUMNS, ...body.columnGroup.columns ], 'metaName')
    };

    // Save off to columnGroup cache
    if (body.columnGroup.id) {
      // replace the updated item by id
      const groupIndex = columnGroups.indexOf((item) => item.id === body.columnGroup.id);
      columnGroups.splice(groupIndex, 1);
      columnGroups.push(newGroup);
    } else {
      columnGroups.push(newGroup);
    }

    return {
      data: newGroup
    };
  }
};
