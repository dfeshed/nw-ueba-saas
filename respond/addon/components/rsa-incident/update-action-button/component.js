import Ember from 'ember';
import * as DataActions from 'respond/actions/data-creators';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

const FLASH_MESSAGE_TYPES = {
  SUCCESS: {
    name: 'success',
    icon: 'check-circle-2'
  },
  INFO: {
    name: 'info',
    icon: 'information-circle'
  },
  WARNING: {
    name: 'warning',
    icon: 'report-problem-circle'
  },
  ERROR: {
    name: 'error',
    icon: 'delete-1'
  }
};

const {
  inject: { service },
  Component,
  isArray,
  isNone
} = Ember;

const stateToComputed = undefined; // no computed properties from app state

const dispatchToActions = (dispatch) => {
  return {
    handleChange(selectedOption) {
      const { incidentId, isBulk } = this.getProperties('incidentId', 'isBulk');

      this.set('stagedOption', selectedOption);

      if (isBulk) {
        this.send('showConfirmationDialog');
      } else {
        const id = isArray(incidentId) ? incidentId[0] : incidentId;
        this.send('updateIncident', id, this.get('stagedValue'));
      }
    },

    cancelBulkUpdate() {
      this.send('closeConfirmationDialog');
    },

    applyBulkUpdate() {
      this.send('bulkUpdateIncidents', this.get('incidentId'), this.get('stagedValue'));
      this.send('closeConfirmationDialog');
    },

    /**
     * Convenience method for showing a flash success/error message to the user on update or failure
     * @method showFlashMessage
     * @public
     * @param type
     * @param i18nKey
     * @param context
     */
    showFlashMessage(type, i18nKey, context) {
      const { i18n, flashMessages } = this.getProperties('i18n', 'flashMessages');

      flashMessages[type.name](i18n.t(i18nKey, context), { iconName: type.icon });
    },

    /**
     * Displays the bulk update confirmation modal dialog
     * @method showConfirmationDialog
     * @public
     */
    showConfirmationDialog() {
      this.get('eventBus').trigger(`rsa-application-modal-open-${this.get('bulkConfirmationDialogId')}`);
    },

    /**
     * Closes/Cancels the bulk update confirmation modal dialog
     * @method closeConfirmationDialog
     * @public
     */
    closeConfirmationDialog() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${this.get('bulkConfirmationDialogId')}`);
    },

    /**
     * Executes the action for updating a single incident's field with a new value and display of a success or failure
     * notification to the user
     * @method updateIncident
     * @public
     * @param incidentId
     * @param value
     */
    updateIncident(incidentId, value) {
      const {
        fieldName,
        stagedLabel,
        actionMethod,
        successMessageI18nKey,
        failureMessageI18nKey
      } = this.getProperties('redux', 'fieldName', 'stagedLabel', 'actionMethod', 'successMessageI18nKey', 'failureMessageI18nKey');

      if (DataActions[actionMethod]) {
        dispatch(DataActions[actionMethod](incidentId, value, {
          onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, successMessageI18nKey, { field: fieldName, incidentId, name: stagedLabel })),
          onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, failureMessageI18nKey, { field: fieldName, incidentId, name: stagedLabel }))
        }));
      }
    },

    /**
     * Executes the action for updating a field value on more than one incident, as well as handles the display of a success or failure
     * notification to the user
     * @method bulkUpdateIncidents
     * @public
     * @param incidentIds
     * @param value
     */
    bulkUpdateIncidents(incidentIds, value) {
      if (incidentIds.length) {
        const {
          fieldName,
          stagedLabel,
          bulkActionMethod,
          bulkSuccessMessageI18nKey,
          bulkFailureMessageI18nKey
        } = this.getProperties('redux', 'fieldName', 'stagedLabel', 'bulkActionMethod', 'bulkSuccessMessageI18nKey', 'bulkFailureMessageI18nKey');

        if (DataActions[bulkActionMethod]) {
          dispatch(DataActions[bulkActionMethod](incidentIds, value, {
            onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, bulkSuccessMessageI18nKey, { field: fieldName, count: incidentIds.length, name: stagedLabel })),
            onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, bulkFailureMessageI18nKey, { field: fieldName, count: incidentIds.length, name: stagedLabel }))
          }));
        }
      }
    }
  };
};

/**
 * A component for updating a single field on one or more incidents. The component wraps the ember-power-select so
 * that pressing the button will open the power-select dropdown
 * @public
 */
const IncidentUpdateActionButton = Component.extend({
  classNames: ['incident-action-button'],

  i18n: service(),
  flashMessages: service(),
  eventBus: service(),

  /**
   * The Incident record field name which will be updated by changes made by this control (e.g., priority, status, etc)
   * @property fieldName
   * @public
   */
  fieldName: null,

  /**
   * The ID of the Incident which will be updated by selections made via this control
   * @property fieldName
   * @public
   */
  incidentId: null,

  /**
   * The options that appear in the dropdown control
   * @property options
   * @public
   */
  options: null,

  /**
   * Sets the button into an enabled or disabled state. If disabled, the trigger will not open the dropdown. Default
   * is false (i.e., enabled)
   * @property isDisabled
   * @public
   */
  isDisabled: false,

  /**
   * Determines whether the resulting power select has search enabled or not
   * @property searchEnabled
   * @public
   */
  searchEnabled: true,

  /**
   * Determines the button styling for the embedded rsa-form-button. This has no impact if the button is shown as an
   * icon only (i.e., isIconOnly=true), but rather impacts the full button look/feel. See rsa-form-button docs in the
   * component-lib for more information on the various style options (e.g., 'primary', 'danger', etc)
   *
   * Delegated to the embedded rsa-form-button component
   * @property style
   * @public
   */
  style: 'standard',

  /**
   * Sets the button to icon only if true, in which case it does not appear as a standard form button. See component-lib
   * documentation for more details.
   *
   * Delegated to the embedded rsa-form-button component
   * @property isIconOnly
   * @public
   */
  isIconOnly: false,

  /**
   * The name of the icon to be used with the embedded rsa-form-button. Cf. streamline font names
   * @property iconName
   * @public
   */
  iconName: 'location-pin-unknown-2',

  /**
   * The property name on the dropdown option object which should be used as the display value in the dropdown
   * @property optionLabelProperty
   * @public
   */
  optionLabelProperty: 'label',

  /**
   * The property name on the dropdown option object which should be used as the value. If null, the entire dropdown object
   * will be used as the value
   * @property optionValueProperty
   * @public
   */
  optionValueProperty: null,

  /**
   * Represents the current incident value which should be selected in the dropdown when the dropdown opens.
   * Cf `selected` computed property
   * @property selectedValue
   * @public
   */
  selectedValue: null,

  /**
   * Determines the horizontal positioning of the dropdown control relative to the trigger.
   *
   * Delegated to embedded power-select component attributes
   * @property horizontalPosition
   * @public
   */
  horizontalPosition: 'left',

  /**
   * I18n key used for a successful update message
   * @property successMessageI18nKey
   * @public
   */
  successMessageI18nKey: 'respond.incidents.actions.actionMessages.updateSuccess',

  /**
   * I18n key used for a failed update message
   * @property failureMessageI18nKey
   * @public
   */
  failureMessageI18nKey: 'respond.incidents.actions.actionMessages.updateFailure',

  /**
   *  I18n key used for a successful bulk update message
   * @property bulkSuccessMessageI18nKey
   * @public
   */
  bulkSuccessMessageI18nKey: 'respond.incidents.actions.actionMessages.bulkUpdateSuccess',

  /**
   *  I18n key used for a failed bulk update message
   * @property bulkFailureMessageI18nKey
   * @public
   */
  bulkFailureMessageI18nKey: 'respond.incidents.actions.actionMessages.bulkUpdateFailure',

  /**
   * I18n key used in the bulk update confirmation dialog
   * @property bulkUpdateConfirmationI18nKey
   * @public
   */
  bulkUpdateConfirmationI18nKey: 'respond.incidents.actions.actionMessages.bulkUpdateConfirmation',

  /**
   * The API and action-creator method name that should be called for a single incident update. This computed property is
   * dynamically constructed to avoid implementing subclasses or passing too many properties via attributes, and
   * works well since all of the API method names follow the same naming convention
   * @property actionMethod
   * @public
   * @param fieldName
   * @returns {string}
   */
  @computed('fieldName')
  actionMethod(fieldName) {
    return `changeIncident${fieldName.capitalize()}`;
  },

  /**
   * The API and action-creator method name that should be called for a bulk incident update. Cf related `actionMethod`
   * property
   * @property bulkActionMethod
   * @public
   * @param actionMethod
   * @returns {string}
   */
  @computed('actionMethod')
  bulkActionMethod(actionMethod) {
    return `bulk${actionMethod.capitalize()}`;
  },

  /**
   * The currently selected option in the dropdown options. Since ember-power-select expects this to be the object
   * reference, this computed property uses the passed in `selectedValue` to look up the object reference from the
   * options array property
   * @property selected
   * @public
   * @param options
   * @param optionValueProperty
   * @param selectedValue
   * @returns {*|Object}
   */
  @computed('options', 'optionValueProperty', 'selectedValue')
  selected(options, optionValueProperty, selectedValue) {
    optionValueProperty = optionValueProperty || 'id';
    return options.findBy(optionValueProperty, selectedValue);
  },

  /**
   * The value from the option selected by the user and which is used for updating the incident record
   * @property stagedValue
   * @public
   * @param stagedOption
   * @param optionValueProperty
   * @returns {{}}
   */
  @computed('stagedOption', 'optionValueProperty')
  stagedValue(stagedOption = {}, optionValueProperty) {
    return isNone(optionValueProperty) ? stagedOption : stagedOption[optionValueProperty];
  },

  /**
   * The label from the option selected by the user and which is used for representing the new value in flash
   * messages and confirmation dialogs.
   * @property stagedLabel
   * @public
   * @param stagedOption
   * @param optionLabelProperty
   * @returns {{}}
   */
  @computed('stagedOption', 'optionLabelProperty')
  stagedLabel(stagedOption = {}, optionLabelProperty) {
    return isNone(optionLabelProperty) ? stagedOption : stagedOption[optionLabelProperty];
  },

  /**
   * Dyanmically constructed ID used to uniquely reference the confirmation modal dialog used in bulk updates
   * @property bulkConfirmationDialogId
   * @public
   * @param fieldName
   * @returns {string}
   */
  @computed('fieldName')
  bulkConfirmationDialogId(fieldName) {
    return `bulk-update-${fieldName}-${this.elementId}`;
  },

  /**
   * Indicates when true that the control will perform a bulk (i.e., more than one incident update) operation. When
   * false, the operation is a single incident update.
   * @property isBulk
   * @public
   * @param incidentId
   * @returns {boolean}
   */
  @computed('incidentId')
  isBulk(incidentId) {
    return isArray(incidentId) && (incidentId.length > 1);
  },

  /**
   * Sets up the options array on instance initialization to avoid having an object/array reference set on the
   * component prototype
   * @method init
   * @public
   */
  init() {
    this._super(...arguments);
    if (!isArray(this.get('options'))) {
      this.set('options', []);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentUpdateActionButton);