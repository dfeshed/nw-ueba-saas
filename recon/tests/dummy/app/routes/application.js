import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

const contextAddToListModalId = 'addToList';

export default Route.extend({
  eventBus: service(),
  model() {
    return {
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
      ]
    };
  },

  actions: {
    openContextPanel(entity) {
      const { type, id } = entity || {};
      this.get('controller').setProperties({
        entityId: id,
        entityType: type
      });
    },

    closeContextPanel() {
      this.get('controller').setProperties({
        entityId: undefined,
        entityType: undefined
      });
    },

    openContextAddToList(entity) {
      const { type, id } = entity || {};
      const eventName = (type && id) ?
        `rsa-application-modal-open-${contextAddToListModalId}` :
        `rsa-application-modal-close-${contextAddToListModalId}`;
      this.get('controller').set('entityToAddToList', entity);
      this.get('eventBus').trigger(eventName);
    },

    closeContextAddToList() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${contextAddToListModalId}`);
      this.get('controller').set('entityToAddToList', undefined);
    }
  }
});
