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
      path: '/concentrator/stats/session.rate',
      isSeries: true
    },
    {
      name: 'meta_rate',
      path: '/concentrator/stats/meta.rate',
      isSeries: true
    },
    {
      name: 'status',
      path: '/concentrator/stats/status'
    }
  ],

  actions: {
    startAggregation() {
      const transport = this.get('transport');
      return transport.send('/concentrator', { message: 'start' });
    },
    stopAggregation() {
      const transport = this.get('transport');
      return transport.send('/concentrator', { message: 'stop' });
    }
  }
});
