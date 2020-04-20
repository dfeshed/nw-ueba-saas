import Component from '@ember/component';
import layout from './template';
import stringFormatColumns from './string-format-columns';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: 'box',
  classNames: ['string-view'],

  stringFormatColumns,

  // Filtering data based on the search text and adding length key to the filtered data set.
  @computed('fileData', 'searchText')
  filteredDataWithLength(fileData, searchText) {

    searchText = searchText ? searchText : '';

    const filteredData = fileData && fileData.filter((item) => {
      return item.text.toString().toLowerCase().includes(searchText.toLowerCase());
    }).map((item) => ({
      ...item,
      length: item.text.length
    }));

    return filteredData;
  }
});