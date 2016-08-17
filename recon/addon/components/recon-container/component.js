import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
const { A, Component } = Ember;

export default Component.extend({
  layout,
  tagName: '',
  showMetaDetails: false,
  @computed('model.summary')
  headerItems(items) {
    return items.reduce(function(headerItems, item) {
      if (item.name === 'destination' || item.name === 'source') {
        headerItems.pushObjects([
          {
            name: `${item.name} IP:PORT`,
            value: item.value
          }
        ]);
      } else {
        headerItems.pushObject(item);
      }

      return headerItems;
    },A([]));
  },
  meta: [
    [
      'size',
      62750
    ],
    [
      'payload',
      56460
    ],
    [
      'medium',
      1
    ],
    [
      'eth.src',
      '70:56:81:9A:94:DD'
    ],
    [
      'eth.dst',
      '10:0D:7F:75:C4:C8'
    ],
    [
      'eth.type',
      2048
    ],
    [
      'ip.src',
      '192.168.58.6'
    ],
    [
      'ip.dst',
      '23.67.246.152'
    ],
    [
      'ip.proto',
      6
    ],
    [
      'tcp.flags',
      26
    ],
    [
      'tcp.srcport',
      55003
    ],
    [
      'tcp.dstport',
      80
    ],
    [
      'service',
      80
    ],
    [
      'streams',
      2
    ],
    [
      'packets',
      95
    ],
    [
      'lifetime',
      54
    ],
    [
      'action',
      'get'
    ],
    [
      'directory',
      '/'
    ],
    [
      'filename',
      'rtblog.php'
    ],
    [
      'extension',
      'php'
    ]
  ],
  actions: {
    toggleMetaDetails() {
      this.toggleProperty('showMetaDetails');
    }
  }
});
