import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  classNames: ['download-status'],

  // unique panelId
  @computed('checksum')
  panelId(checksum) {
    const subString = checksum.substring(0, 5);
    return `downloadToServerError${subString}`;
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