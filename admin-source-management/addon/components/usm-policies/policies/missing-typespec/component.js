import Component from '@ember/component';
import computed from 'ember-computed-decorators';

const MissingTypespec = Component.extend({
  tagName: 'span',
  classNames: ['missing-typespec'],
  sources: null,

  @computed('sources')
  missingTypespec(sources) {
    sources = sources ? sources : [];
    return sources.filter((source) => source?.errorState?.state).length;
  }
});

export default MissingTypespec;
