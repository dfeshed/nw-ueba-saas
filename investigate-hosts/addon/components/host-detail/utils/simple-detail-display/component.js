import Component from 'ember-component';

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
  detailDisplayInputs: null
});