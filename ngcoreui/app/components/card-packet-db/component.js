import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  monitor: [
    {
      name: 'packet_rate',
      path: '/database/stats/packet.rate',
      isSeries: true
    },
    {
      name: 'packet_rate_max',
      path: '/database/stats/packet.rate.max'
    },
    {
      name: 'packet_total',
      path: '/database/stats/packet.total'
    },
    {
      name: 'packet_bytes',
      path: '/database/stats/packet.bytes'
    },
    {
      name: 'packet_oldest_file_time',
      path: '/database/stats/packet.oldest.file.time'
    },
    {
      name: 'packet_volume_bytes',
      path: '/database/stats/packet.volume.bytes'
    },
    {
      name: 'packet_free_space',
      path: '/database/stats/packet.free.space'
    }
  ]

});
