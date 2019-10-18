import { columnGroups } from '..';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/column/groups/delete-by-id',
  requestDestination: '/ws/investigate/column/groups/delete-by-id',
  message(frame) {
    const body = JSON.parse(frame.body);

    // Remove from columnGroup cache
    const itemToRemove = columnGroups.find((d) => d.id === body.id);
    const index = columnGroups.indexOf(itemToRemove);
    columnGroups.splice(index, 1);

    return {
      data: true,
      request: {
        id: body.id
      }
    };
  }
};
