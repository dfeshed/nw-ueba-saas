import arrayFindByList from 'respond/utils/array/find-by-list';

// Given an array of items, each with an `events` array, find all the items with at least 1 event that matches
// a given field-values filter.  Returns the ids of those items, if any.
const filterIdsByEvents = (arr, field, values) => {
  return arr
    .filter((item) => !!arrayFindByList(item.events, field, values))
    .map((item) => item.id);
};

/**
 * Computes the IDs of the nodes & links in a given dataset which match a given selection.
 * @param {object} data The nodes & links data for a d3 force-layout.
 * @param {object[]} data.nodes The nodes.
 * @param {object[]} data.links The links.
 * @param {object} selection The selection to be parsed.
 * @param {string} selection.type The type of objects which are referenced by selection.ids.
 * @param {string[]} selection.ids The list of selected objects' ids.
 * @returns {{ nodeIds: string[], linkIds: string[] } The ids of the nodes & links which match the given selection.
 * @public
 */
export default function selectionToFilter(data, selection) {
  const { nodes = [], links = [] } = data || {};
  const { type, ids } = selection || {};

  if (ids && ids.length) {
    switch (type) {
      case 'storyPoint':
        return {
          nodeIds: filterIdsByEvents(nodes, 'indicatorId', ids),
          linkIds: filterIdsByEvents(links, 'indicatorId', ids)
        };
      case 'event':
        return {
          nodeIds: filterIdsByEvents(nodes, 'id', ids),
          linkIds: filterIdsByEvents(links, 'id', ids)
        };
    }
  }
  return null;
}