import Component from '@ember/component';
import { inject as service } from '@ember/service';

// when we upgrade to ember-cli v3.4.3 we can remove this
// https://github.com/ember-cli/ember-cli/pull/8048/files
export default Component.extend({
  tagName: '',
  model: service('head-data')
});
