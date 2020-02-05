import Component from '@ember/component';
import { computed } from '@ember/object';

const MissingTypespec = Component.extend({
  tagName: 'span',
  classNames: ['missing-typespec'],
  sources: null,

  missingTypespec: computed('sources', function() {
    const itemSources = this.sources ? this.sources : [];
    return itemSources.filter((source) => source?.errorState?.state).length;
  })
});

export default MissingTypespec;
