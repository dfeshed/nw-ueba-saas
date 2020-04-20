import Mixin from '@ember/object/mixin';
import computed, { alias } from 'ember-computed-decorators';

export default Mixin.create({
  @computed('dataIndex')
  eventIndex(index) {
    return index + 1;
  },

  @alias('numberOfItems')
  packetCount: 0
});
