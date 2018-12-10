import _ from 'lodash';
import { assert } from '@ember/debug';
import { isPresent } from '@ember/utils';
import { get, set } from '@ember/object';
import Component from '@ember/component';
import Changeset from 'ember-changeset';
import computed from 'ember-computed-decorators';
import deepSet from 'ember-deep-set';
import { inject as service } from '@ember/service';
import lookupValidator from 'ember-changeset-validations';

const VALIDATIONS_REQUIRED = 'You must include a "formValidations" attribute in all uses of "{{form-element}}".';
const MODEL_REQUIRED = 'You must include a "formModel" attribute in all uses of "{{form-element}}".';

export default Component.extend({
  tagName: 'form',
  testId: 'formElement',
  attributeBindings: [
    'autocomplete',
    'autocorrect',
    'autocapitalize',
    'spellcheck',
    'testId:test-id'
  ],
  autocomplete: 'off',
  autocorrect: 'off',
  autocapitalize: 'off',
  spellcheck: false,
  submitted: false,
  i18n: service(),
  verifyPresence: (value, message) => {
    assert(message, isPresent(value));
  },
  init() {
    this._super(...arguments);
    const formValidations = get(this, 'formValidations');
    this.verifyPresence(formValidations, VALIDATIONS_REQUIRED);
    const formModel = get(this, 'formModel');
    this.verifyPresence(formModel, MODEL_REQUIRED);
  },
  @computed('formModel')
  model(formModel) {
    return _.cloneDeep(formModel);
  },
  @computed('model')
  changeset(model) {
    const formValidations = get(this, 'formValidations');
    return new Changeset(model, lookupValidator(formValidations), formValidations);
  },
  @computed('i18n.locale')
  buttonLabels() {
    const i18n = get(this, 'i18n');
    return {
      save: i18n.t('forms.save'),
      reset: i18n.t('forms.reset')
    };
  },
  actions: {
    validateProperty(changeset, property) {
      return changeset.validate(property);
    },
    reset(changeset) {
      changeset.rollback();
      set(this, 'submitted', false);
      this.notifyPropertyChange('submitted');
    },
    save(changeset) {
      set(this, 'submitted', true);
      const model = get(this, 'model');
      const snapshot = changeset.snapshot();
      return changeset
        .cast(Object.keys(model))
        .validate()
        .then(() => {
          if (get(changeset, 'isValid')) {
            const { changes } = snapshot;
            const content = changeset.get('data');
            Object.keys(changes).forEach((key) => {
              deepSet(content, key, changes[key]);
            });
            get(this, 'formSave')(content);
            set(this, 'submitted', false);
            this.notifyPropertyChange('submitted');
          }
        });
    }
  }
});
