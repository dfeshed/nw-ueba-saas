import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';

const { Mixin } = Ember;

export default Mixin.create({
  @computed('dataIndex')
  eventIndex(index) {
    return index + 1;
  },

  @alias('numberOfItems')
  packetCount: 0
});
