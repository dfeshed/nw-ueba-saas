import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

import { base64ToUnicode } from 'investigate-shared/utils/file-analysis-base64decoder';

export default Component.extend({
  layout,
  title: 'investigateShared.endpoint.fileAnalysis.textView',
  tagName: '',

  @computed('fileData')
  decodedData(fileData) {
    const { encodedData = [''] } = fileData || {};

    let decodedData = '';
    encodedData.forEach((element) => {
      decodedData = decodedData + base64ToUnicode(element);
    });

    return decodedData;
  }
});