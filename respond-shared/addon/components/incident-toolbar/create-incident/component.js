import layout from './template';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { isEmpty, typeOf } from '@ember/utils';

/**
 * @class CreateIncident
 * The form (with validation) required to create an incident from one or more alerts
 *
 * @public
 */
export default Component.extend(Notifications, {
  layout,

  classNames: ['rsa-create-incident'],
  /**
   * Represents the (required) name that will be used to create the incident
   * @property name
   * @type {string}
   * @public
   */
  name: null,

  /**
   * Represents the (required) priority that will be set on the newly created incident
   * @property priority
   * @type {string}
   * @public
   */
  priority: 'LOW',

  /**
   * Represents the (optional) assignee who will be set on the newly created incident
   * @property assignee
   * @type {object}
   * @public
   */
  assignee: null,

  /**
   * Represents the (optional) category that will be set on the newly created incident
   * @property category
   * @type {object}
   * @public
   */
  categories: null,

  /**
   * Indicates whether the form is invalid. Since the form only has one field (for incident name) and that field is
   * required for incident creation, the form is only invalid if the field is empty
   * @property isInvalid
   * @type {boolean}
   * @public
   */
  @computed('name')
  isInvalid(name) {
    return isEmpty(name) || typeOf(name) === 'string' && isEmpty(name.trim());
  },

  actions: {
    handleCancel() {
      this.close();
    },
    handleCreate() {
      const { name, priority, assignee, categories } = this.getProperties('name', 'priority', 'assignee', 'categories');
      const incidentDetails = {
        name,
        priority,
        assignee,
        categories
      };
      this.createIncident(incidentDetails);
    }
  }
});
