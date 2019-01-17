import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';
import parseTimeCapture from 'direct-access/reducers/selectors/parse-time-capture';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  monitor: [
    {
      name: 'capture_rate',
      path: '/decoder/stats/capture.packet.rate',
      isSeries: true
    },
    {
      name: 'capture_rate_max',
      path: '/decoder/stats/capture.packet.rate.max'
    },
    {
      name: 'capture_received',
      path: '/decoder/stats/capture.received'
    },
    {
      name: 'capture_dropped',
      path: '/decoder/stats/capture.dropped'
    },
    {
      name: 'capture_status',
      path: '/decoder/stats/capture.status'
    },
    {
      name: 'time_capture',
      path: '/decoder/stats/time.capture',
      displayFunction: parseTimeCapture
    }
  ]

});
