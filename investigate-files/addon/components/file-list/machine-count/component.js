import Component from '@ember/component';

export default Component.extend({
  classNames: ['machine-count'],

  item: null,

  machineCountMapping: null,

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
