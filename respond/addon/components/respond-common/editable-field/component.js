import Component from 'ember-component';
import computed, { oneWay } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';

export default Component.extend({
  classNames: ['editable-field'],
  classNameBindings: ['type', 'hasChanges', 'isEditing', 'isDisabled'],

  /**
   * The type of input to use. Acceptable values are "input" (default) and "textarea"
   * @type {string}
   * @property type
   * @public
   */
  type: 'input',

  /**
   * The value of the field is the string representation displayed to the viewer, which in turn becomes the value
   * of the text input available for edit once the field enters edit mode.
   * @type {string}
   * @property
   * @public
   */
  @oneWay('value') text: null,

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
   * The maximum number of characters for the input type
   * @type {number}
   * @property max
   * @public
   */
  inputMaxlength: 75,

  /**
   * Whether the input is disabled (which means in this case to show as plain text rather than as an editable field)
   * @type {boolean}
   * @property isDisabled
   * @public
   */
  isDisabled: false,

  /**
   * Whether the user can make a change to the editable-field if the change is only whitespace or empty string. If
   * false, the confirm button will be disabled and the user will not be able to "save" the change. Note: this does
   * not preclude the value from starting as empty.
   * @type {boolean}
   * @property allowEmptyValue
   * @public
   */
  allowEmptyValue: true,

  /**
   * Returns true if the current text in the editable field is invalid.
   * @param text
   * @param allowEmptyValue
   * @returns {boolean}
   * @public
   */
  @computed('text', 'allowEmptyValue')
  isInvalid(text, allowEmptyValue) {
    return !allowEmptyValue && isEmpty(text.trim());
  },

  /**
   * The hasChanges property represents whether or not the user has modified the original value while in editing mode.
   * @type {boolean}
   * @property hasChanges
   * @param value
   * @param originalValue
   * @returns {boolean}
   * @private
   */
  @computed('text', 'originalValue')
  hasChanges(text, originalValue) {
    return text !== originalValue;
  },

  /**
   * When the component is rendered, we focus on the input (if it exists) so that the cursor is immediately ready
   * for the user to make changes to the field.
   * @private
   */
  didRender() {
    this._super(...arguments);
    this.$('input, textarea').focus();
  },

  reset() {
    const value = this.get('value');
    this.setProperties({ isEditing: false, originalValue: value, text: value });
  },

  /**
   * Using the didReceiveAttrs hook to double-check that the value and the known original value are still the same. If
   * not, it's likely that the source has changed, and we want to reset the component to non-editing mode, otherwise
   * the component remains in the previous state, even though the user has not explicitly requested to edit that
   * specific source.
   * @private
   */
  didReceiveAttrs() {
    this._super(...arguments);
    const { value, originalValue } = this.getProperties('value', 'originalValue');
    if (value !== originalValue) {
      this.reset();
    }
  },

  onFieldChange() {},

  actions: {
    edit() {
      if (!this.get('isDisabled')) {
        this.set('isEditing', true);
      }
    },
    cancel() {
      const { hasChanges, originalValue } = this.getProperties('hasChanges', 'originalValue');
      if (hasChanges) {
        this.set('text', originalValue);
      }
      this.reset();
    },
    confirm() {
      const { hasChanges, text, originalValue } = this.getProperties('hasChanges', 'text', 'originalValue');
      if (hasChanges) {
        // The confirm operation will send the action 'change', including as arguments the new value, the original value,
        // and a function that will revert the field back to the original value (e.g., if a server call fails)
        this.get('onFieldChange')(text, originalValue, () => {
          this.setProperties({ text: originalValue, isEditing: false }); // revert callback provided
        });
      }
      this.setProperties({ isEditing: false, originalValue: text });
    },
    /**
     * Handler (blur) for when the user exits/blurs from the input by tabbing or clicking out of the input.
     * @private
     */
    focusOut() {
      const { isEditing, hasChanges } = this.getProperties('isEditing', 'hasChanges');
      if (isEditing && !hasChanges) {
        this.reset();
      }
    }
  }
});