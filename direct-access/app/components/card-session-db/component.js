import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  monitor: [
    {
      name: 'session_rate',
      path: '/database/stats/session.rate',
      isSeries: true
    },
    {
      name: 'session_rate_max',
      path: '/database/stats/session.rate.max'
    },
    {
      name: 'session_total',
      path: '/database/stats/session.total'
    },
    {
      name: 'session_bytes',
      path: '/database/stats/session.bytes'
    },
    {
      name: 'session_oldest_file_time',
      path: '/database/stats/session.oldest.file.time'
    },
    {
      name: 'session_volume_bytes',
      path: '/database/stats/session.volume.bytes'
    },
    {
      name: 'session_free_space',
      path: '/database/stats/session.free.space'
    }
  ]

});
