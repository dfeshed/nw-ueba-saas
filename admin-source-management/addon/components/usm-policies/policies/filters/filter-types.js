/**
 * Policy filter types
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [

  {
    'name': 'publishStatus',
    'label': 'adminUsm.policies.list.publishStatus',
    'listOptions': [
      // policy.lastPublishedOn > 0 ???
      { name: 'published', label: 'adminUsm.publishStatus.published' },
      // policy.lastPublishedOn === 0
      { name: 'unpublished', label: 'adminUsm.publishStatus.unpublished' },
      // policy.dirty === true
      { name: 'unpublished_edits', label: 'adminUsm.publishStatus.unpublishedEdits' }
    ],
    type: 'list'
  }

];

export {
  FILTER_TYPES
};
