import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import Immutable from 'seamless-immutable';

export default Component.extend({
  item: null,

  @computed('item')
  raw: (item) => {
    item = Immutable.without(item, ['path', 'route', 'flags']);
    return JSON.stringify(item, null, 2);
  }
});
