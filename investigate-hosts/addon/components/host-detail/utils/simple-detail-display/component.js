import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  classNames: ['simple-detail-display-wrapper'],
  tagName: 'hbox',

  /*
   * A hash of inputs coming from consumers of this component.
   *
   * Possible values:
   *   status
   *   tableItems
   *   columnsConfig
   *   selectRowAction
   *   localeNameSpace
   *   propertyConfig
   *   propertyData
   */
  detailDisplayInputs: null,

  /* Sets the class as col-xs-9 only if the property panal is present for the selected host details tab */
  @computed('detailDisplayInputs')
  datatableWidth(detailDisplayInputs) {
    return (detailDisplayInputs && detailDisplayInputs.propertyConfig) ? 'col-xs-9' : 'col-xs-12';
  },

  @computed('detailDisplayInputs')
  selectedIndex(detailDisplayInputs) {
    return (detailDisplayInputs && detailDisplayInputs.selectedIndex) ? detailDisplayInputs.selectedIndex : 0;
  }
});
