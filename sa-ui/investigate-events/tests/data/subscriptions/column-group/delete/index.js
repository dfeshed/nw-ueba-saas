import { columnGroups } from '..';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/column/groups/delete-by-id',
  requestDestination: '/ws/investigate/column/groups/delete-by-id',
  message(frame) {
    const body = JSON.parse(frame.body);
    const { columnGroup } = body;
    const { id } = columnGroup;

    // Remove from columnGroup cache
    const itemToRemove = columnGroups.find((d) => d.id === id);
    const index = columnGroups.indexOf(itemToRemove);
    const [ removedColumnGroup ] = columnGroups.splice(index, 1);

    return {
      data: removedColumnGroup,
      request: {
        id: body.id,
        columnGroup
      }
    };
  }
};
