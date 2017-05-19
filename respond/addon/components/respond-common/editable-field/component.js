import Component from 'ember-component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  classNames: ['editable-field'],
  classNameBindings: ['hasChanges', 'isEditing'],

  /**
   * The value of the field is the string representation displayed to the viewer, which in turn becomes the value
   * of the text input available for edit once the field enters edit mode.
   * @type {string}
   * @property
   * @public
   */
  value: null,

  /**
   * The value of the input is cached as originalValue as soon as the component enters editing mode. This property
   * then can be used to revert the editable-field back to the original value should the user decide to cancel
   * the edit, or if the user exits (blurs() from) the input when there are no changes made.
   * @type {string}
   * @property originalValue
   * @private
   */
  originalValue: null,

  /**
   * The isEditing property represents the two possible states of the editable-field component, namely the default,
   * non-editing state in which the text of the field is displayed to the user as plain text, and the editing mode state,
   * during which the user can modify the text of the field and then confirm or cancel the modification.
   * @type {boolean}
   * @property isEditing
   * @private
   */
  isEditing: false,

  /**
   * The hasChanges property represents whether or not the user has modified the original value while in editing mode.
   * @type {boolean}
   * @property hasChanges
   * @param value
   * @param originalValue
   * @returns {boolean}
   * @private
   */
  @computed('value', 'originalValue')
  hasChanges(value, originalValue) {
    return originalValue !== null && value !== originalValue;
  },

  /**
   * Handler (blur) for when the user exits/blurs from the input by tabbing or clicking out of the input.
   * @private
   */
  focusOut() {
    const hasChanges = this.get('hasChanges');
    if (!hasChanges) {
      this.set('isEditing', false);
    }
  },

  /**
   * When the component is rendered, we focus on the input (if it exists) so that the cursor is immediately ready
   * for the user to make changes to the field.
   * @private
   */
  didRender() {
    this._super(...arguments);
    this.$('input').focus();
  },

  onFieldChange() {},

  actions: {
    edit() {
      const isEditing = this.get('isEditing');
      if (!isEditing) {
        this.setProperties({ originalValue: this.get('value'), isEditing: true });
      }
    },
    cancel() {
      const { hasChanges, originalValue } = this.getProperties('hasChanges', 'originalValue');
      if (hasChanges) {
        this.set('value', originalValue);
      }
      this.setProperties({ isEditing: false, originalValue: null });
    },
    confirm() {
      const { hasChanges, value, originalValue } = this.getProperties('hasChanges', 'value', 'originalValue');
      if (hasChanges) {
        // The confirm operation will send the action 'change', including as arguments the new value, the original value,
        // and a function that will revert the field back to the original value (e.g., if a server call fails)
        this.get('onFieldChange')(value, originalValue, () => {
          this.setProperties({ value: originalValue, originalValue: null }); // revert callback provided
        });
      }
      this.setProperties({ isEditing: false, originalValue: null });
    }
  }
});