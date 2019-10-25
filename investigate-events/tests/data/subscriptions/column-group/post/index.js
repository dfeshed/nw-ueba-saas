import { columnGroups } from '..';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/column/groups/set',
  requestDestination: '/ws/investigate/column/groups/set',
  message(frame) {
    const body = JSON.parse(frame.body);
    const num = Date.now();
    const newGroup = {
      'id': body.columnGroup.id ? body.columnGroup.id : `abc${num}`,
      'name': body.columnGroup.name,
      'contentType': 'USER',
      'columns': body.columnGroup.columns
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
