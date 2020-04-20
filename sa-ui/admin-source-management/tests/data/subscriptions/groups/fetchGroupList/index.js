import groups from '../fetchGroups/data';

export default {
  subscriptionDestination: '/user/queue/usm/groups/list',
  requestDestination: '/ws/usm/groups/list',
  message(/* frame */) {
    const list = groups.map(function(group) {
      return {
        id: group.id,
        name: group.name,
        description: group.description,
        createdOn: group.createdOn,
        lastModifiedOn: group.lastModifiedOn,
        lastPublishedOn: group.lastPublishedOn,
        dirty: group.dirty
      };
    });
    return {
      code: 0,
      data: list
    };
  }
};
