import Component from '@ember/component';
import { get, set } from '@ember/object';
import computed from 'ember-computed-decorators';
import { later } from '@ember/runloop';

const PROPAGATE = 100;

export default Component.extend({
  focused: 0,
  testId: 'formValidation',
  classNames: ['form-validation'],
  attributeBindings: ['testId:test-id'],
  classNameBindings: ['validForm::is-invalid'],

  didReceiveAttrs() {
    this._super(...arguments);
    set(this, 'focused', 0);
  },

  @computed('property', 'changeset.change')
  hasChanges(property) {
    return get(this, `changeset.change.${property}`) === undefined;
  },

  @computed('focused', 'property', 'hasChanges')
  validForm(focused, property, hasChanges) {
    if (hasChanges || focused === 1) {
      return this.changeset.get('isValid');
    }
    return !get(this, `changeset.error.${property}`);
  },

  actions: {
    focus() {
      const property = get(this, 'property');
      const hasChanges = get(this, 'hasChanges');
      const focused = get(this, 'focused');
      set(this, 'focused', hasChanges ? 0 : focused + 1);
      get(this, 'validateProperty')(property);
    },

    blur() {
      later(() => {
        if (!this.isDestroying) {
          this.incrementProperty('focused');
        }
      }, PROPAGATE);
    }
  }
});
