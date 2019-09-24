import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import moment from 'moment';

export default Component.extend({
  layout,
  classNames: ['download-status'],

  // unique panelId
  @computed('checksum')
  panelId(checksum) {
    const currentTime = moment.now();
    const subString = checksum.substring(0, 5);
    return `error${subString}${currentTime}`;
  },

  @computed('downloadInfo')
  downloadState(downloadInfo) {
    if (downloadInfo) {
      const { status, error } = downloadInfo;
      return { status, error };
    }
    return { status: 'NotDownloaded' };
  }
});