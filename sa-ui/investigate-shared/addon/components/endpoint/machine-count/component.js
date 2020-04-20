import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  classNames: ['machine-count'],

  item: null,

  machineCountMapping: null,

  count: null,

  @computed('count')
  countLabelKey(count) {
    return 1 < count ? 'investigateShared.machineCount.plural' : 'investigateShared.machineCount.singular';
  },

  didReceiveAttrs() {
    this._super(arguments);

    const machineCountMapping = this.get('machineCountMapping');
    const { checksumSha256 } = this.get('item') || {};
    let count = -1;
    if (machineCountMapping && (machineCountMapping[checksumSha256] > -1)) {
      count = machineCountMapping[checksumSha256];
    }
    this.set('count', count);
  }
});
