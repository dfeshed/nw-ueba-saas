import computed from 'ember-computed-decorators';
import ListFilter from '../list-filter/component';

const SIGNATURE_OPTIONS = ['unsigned', 'signed'];


export default ListFilter.extend({


  signatureOptions: SIGNATURE_OPTIONS,

  selectedSignature: null,

  unsignedProperty: [{ value: 'unsigned' }],

  @computed('selectedSignature')
  isSigned(selectedSignature) {
    return selectedSignature === 'signed';
  },

  /**
   * Prepared checkbox option for display
   * @param options
   * @param selections
   * @returns {Array}
   * @public
   */
  @computed('config.options', 'config.selected', 'signatureOptions')
  checkBoxOptions(options, selections, signatureOptions) {
    const filteredOptions = options.filter((item) => !signatureOptions.includes(item));
    return filteredOptions.map((item) => {
      return { name: item, selected: selections.includes(item) };
    });
  },


  actions: {

    setSignature(selection) {
      if (selection === 'unsigned') {
        const { propertyName } = this.get('config');
        const restrictionType = 'IS_NULL';
        const propertyValues = this.get('unsignedProperty');
        this.send('updateFilter', { propertyName, restrictionType, propertyValues });
      }
    }
  }
});
