import sources from '../fetchSources/data';

export default {
  subscriptionDestination: '/user/queue/usm/sources/list',
  requestDestination: '/ws/usm/sources/list',
  message(/* frame */) {
    const list = sources.map(function(source) {
      return {
        id: source.id,
        name: source.name,
        sourceType: source.sourceType,
        defaultSource: source.defaultSource,
        description: source.description,
        associatedGroups: source.associatedGroups,
        createdOn: source.createdOn,
        lastModifiedOn: source.lastModifiedOn,
        lastPublishedOn: source.lastPublishedOn,
        dirty: source.dirty
      };
    });
    return {
      code: 0,
      data: list
    };
  }
};
