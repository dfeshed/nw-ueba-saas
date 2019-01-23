import MonitorMixin from '../monitor-mixin/component';
import Component from '@ember/component';
import parseTimeCapture from 'ngcoreui/reducers/selectors/parse-time-capture';

export default Component.extend(MonitorMixin, {
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  isLogDecoder: false,

  monitor: [
    {
      name: 'capture_rate',
      path: '/decoder/stats/capture.rate',
      isSeries: true
    },
    {
      name: 'capture_rate_max',
      path: '/decoder/stats/capture.rate.max'
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
  ],

  actions: {
    startCapture() {
      const transport = this.get('transport');
      return transport.send('/decoder', { message: 'start' });
    },
    stopCapture() {
      const transport = this.get('transport');
      return transport.send('/decoder', { message: 'stop' });
    }
  },

  init() {
    // set the correct paths for log decoder stats
    if (this.isLogDecoder) {
      this.monitor[0].path = '/decoder/stats/capture.packet.rate';
      this.monitor[1].path = '/decoder/stats/capture.packet.rate.max';
    }

    this._super(...arguments);
  }

});
