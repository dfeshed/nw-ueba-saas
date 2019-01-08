import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isArchiver, isBroker, isConcentrator, isDecoder, isLogDecoder } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  isArchiver: isArchiver(state),
  isBroker: isBroker(state),
  isConcentrator: isConcentrator(state),
  isDecoder: isDecoder(state),
  isLogDecoder: isLogDecoder(state)
});

const dashboardView = Component.extend({
  tagName: 'grid',
  classNames: ['padding'],

  captureRate: ['/decoder/stats/capture.rate'],
  captureRateFunction: (stats) => {
    return {
      value: Math.min(Math.max(stats['/decoder/stats/capture.rate'], 10000), 0),
      display: `${stats['/decoder/stats/capture.rate']} MB/s`
    };
  },

  cpu: ['/sys/stats/cpu'],

  metaRate: ['/database/stats/meta.rate'],
  metaRateFunction: (stats) => {
    return {
      // 5500 was used as the default max value from the thick client
      value: Math.min(Math.max(stats['/database/stats/meta.rate'], 5500), 0),
      display: `${(stats['/database/stats/meta.rate'] / 1000).toFixed(1)}K`
    };
  },

  sessionRate: ['/database/stats/session.rate'],
  sessionRateFunction: (stats) => {
    return {
      value: Math.min(Math.max(stats['/database/stats/session.rate'], 100), 0),
      display: `${stats['/database/stats/session.rate']}`
    };
  },

  assemblerMetaRate: ['/decoder/stats/assembler.meta.rate'],
  assemblerMetaRateFunction: (stats) => {
    return {
      value: stats['/decoder/stats/assembler.meta.rate'],
      display: `${stats['/decoder/stats/assembler.meta.rate']}`
    };
  },

  packetCapturePool: ['/decoder/stats/pool.packet.capture'],
  packetCapturePoolFunction: (stats) => {
    return parseFloat(stats['/decoder/stats/pool.packet.capture']);
  },

  packetAssemblerPool: ['/decoder/stats/pool.packet.assembler'],
  packetAssemblerPoolFunction: (stats) => {
    return parseFloat(stats['/decoder/stats/pool.packet.assembler']);
  },

  packetWritePool: ['/decoder/stats/pool.packet.write'],
  packetWritePoolFunction: (stats) => {
    return parseFloat(stats['/decoder/stats/pool.packet.write']);
  }
});

export default connect(stateToComputed)(dashboardView);
