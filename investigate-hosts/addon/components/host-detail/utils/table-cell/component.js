import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({

  item: null,

  column: null,

  @computed('item')
  itemStatus: ((item) => item.status ? item.status : item.state)
});
