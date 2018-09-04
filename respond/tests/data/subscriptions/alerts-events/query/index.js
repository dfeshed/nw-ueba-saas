import ueba from './ueba';
import re from './re';
import logs from './logs';
import wtd from './wtd';
import ecat from './ecat';
import network from './network';
import log from './log';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/alerts/events',
  requestDestination: '/ws/respond/alerts/events',
  message(frame) {
    const body = JSON.parse(frame.body);
    const filters = body && body.filter || [];
    const uebaAlertId = '586ecfc0ecd25950034e1318';
    const logsAlertId = '5b8554be0a32bd353ad3a167';
    const reAlertId = '5b841c880a32bd5a68baeaf3';
    const wtdAlertId = '5b7f08240a32bd5a68baea89';
    const ecatAlertId = '5b7f06c10a32bd5a68baea84';
    const networkOneAlertId = '5b757f480a32bd36c7609e96';
    const networkTwoAlertId = '5b757f480a32bd36c7609e97';
    const logAlertId = '5b89f97d0a32bd26fdf4507d';
    if (filters.length > 0 && filters[0].value === uebaAlertId) {
      return {
        data: ueba
      };
    } else if (filters.length > 0 && filters[0].value === reAlertId) {
      return {
        data: re
      };
    } else if (filters.length > 0 && filters[0].value === logsAlertId) {
      return {
        data: logs
      };
    } else if (filters.length > 0 && filters[0].value === wtdAlertId) {
      return {
        data: wtd
      };
    } else if (filters.length > 0 && filters[0].value === ecatAlertId) {
      return {
        data: ecat
      };
    } else if (filters.length > 0 && filters[0].value === logAlertId) {
      return {
        data: log
      };
    } else if (filters.length > 0 && filters[0].value === networkOneAlertId || filters[0].value === networkTwoAlertId) {
      return {
        data: network
      };
    }
  }
};
