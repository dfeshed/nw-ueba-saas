import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  title: 'investigateShared.endpoint.fileAnalysis.textView',
  tagName: 'pre',
  classNames: 'text-view',

  @computed('fileData')
  decodedData(fileData) {
    const { encodedData = [''] } = fileData || {};

    let decodedData = '';
    encodedData.forEach((element) => {
      decodedData = decodedData + atob(element);
    });

    return decodedData;
  }
});