import Component from '@ember/component';
import layout from './template';
import { run } from '@ember/runloop';

export default Component.extend({
  layout,

  classNames: ['rsa-editable-text'],
  classNameBindings: ['editMode'],

  editMode: false,
  initialValue: null,

  value: null,
  persistChanges: null,

  // TODO: implement and test validation functionality
  validateChanges: null,

  init() {
    this._super(...arguments);
    this.set('initialValue', this.get('value'));
  },

  actions: {
    cancelChanges(value, event) {
      if (this.get('editMode') && (!event || event.keyCode === 27)) {
        this.set('value', this.get('initialValue'));
        this.toggleProperty('editMode');
      }
    },

    saveChanges() {
      const { persistChanges, value } = this.getProperties('value', 'persistChanges');
      this.set('initialValue', value);
      if (persistChanges) {
        persistChanges(value);
      }
      this.send('toggleEditMode');
    },

    toggleEditMode() {
      this.toggleProperty('editMode');

      run.schedule('afterRender', () => {
        const input = this.element.querySelector('input');
        if (input) {
          input.focus();
        }
      });
    }
  }

});
