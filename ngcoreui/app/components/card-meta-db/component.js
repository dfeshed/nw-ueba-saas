import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  monitor: [
    {
      name: 'meta_rate',
      path: '/database/stats/meta.rate',
      isSeries: true
    },
    {
      name: 'meta_rate_max',
      path: '/database/stats/meta.rate.max'
    },
    {
      name: 'meta_total',
      path: '/database/stats/meta.total'
    },
    {
      name: 'meta_bytes',
      path: '/database/stats/meta.bytes'
    },
    {
      name: 'meta_oldest_file_time',
      path: '/database/stats/meta.oldest.file.time'
    },
    {
      name: 'meta_volume_bytes',
      path: '/database/stats/meta.volume.bytes'
    },
    {
      name: 'meta_free_space',
      path: '/database/stats/meta.free.space'
    }
  ]

});
