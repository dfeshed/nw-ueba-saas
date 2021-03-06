import { computed } from '@ember/object';
import Service, { inject as service } from '@ember/service';

export default Service.extend({

  eventBus: service(),

  main: 'panelA',

  actionConfig: 'app',

  incidentQueueActive: false,

  userPreferencesActive: false,

  journalPanel: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  journalPanelClass: computed('journalPanel', function() {
    return `journal-${this.get('journalPanel')}`;
  }),
  journalPanelActive: computed('journalPanel', function() {
    return this.get('journalPanel') !== 'hidden';
  }),

  contextPanel: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  contextPanelClass: computed('contextPanel', function() {
    return `context-${this.get('contextPanel')}`;
  }),

  panelA: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  panelAClass: computed('panelA', function() {
    return `panel-A-${this.get('panelA')}`;
  }),

  panelB: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  panelBClass: computed('panelB', function() {
    return `panel-B-${this.get('panelB')}`;
  }),

  panelC: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  panelCClass: computed('panelC', function() {
    return `panel-C-${this.get('panelC')}`;
  }),

  panelD: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  panelDClass: computed('panelD', function() {
    return `panel-D-${this.get('panelD')}`;
  }),

  panelE: 'hidden', // ['hidden', 'quarter', 'half', 'main', 'full']
  panelEClass: computed('panelE', function() {
    return `panel-E-${this.get('panelE')}`;
  }),

  toggleIncidentQueue() {
    this.toggleProperty('incidentQueueActive');
    this.get('eventBus').trigger('rsa-application-incident-queue-panel-will-toggle');
  },

  toggleUserPreferences() {
    this.toggleProperty('userPreferencesActive');
    this.get('eventBus').trigger('rsa-application-user-preferences-panel-will-toggle');
  },

  toggleFullWidthPanel(panel) {
    if (!this.get('isExpanded')) {
      this.set('_journalPanel', this.get('journalPanel'));
      this.set('_contextPanel', this.get('contextPanel'));
      this.set('_panelA', this.get('panelA'));
      this.set('_panelB', this.get('panelB'));
      this.set('_panelC', this.get('panelC'));
      this.set('_panelD', this.get('panelD'));
      this.set('_panelE', this.get('panelE'));

      if (panel !== 'journalPanel') {
        this.set('journalPanel', 'hidden');
      }

      if (panel !== 'contextPanel') {
        this.set('contextPanel', 'hidden');
      }

      if (panel !== 'panelA') {
        this.set('panelA', 'hidden');
      }

      if (panel !== 'panelB') {
        this.set('panelB', 'hidden');
      }

      if (panel !== 'panelC') {
        this.set('panelC', 'hidden');
      }

      if (panel !== 'panelD') {
        this.set('panelD', 'hidden');
      }

      if (panel !== 'panelE') {
        this.set('panelE', 'hidden');
      }

      this.set(panel, 'full');
      this.set('isExpanded', true);
    } else {
      this.set('journalPanel', this.get('_journalPanel'));
      this.set('contextPanel', this.get('_contextPanel'));
      this.set('panelA', this.get('_panelA'));
      this.set('panelB', this.get('_panelB'));
      this.set('panelC', this.get('_panelC'));
      this.set('panelD', this.get('_panelD'));
      this.set('panelE', this.get('_panelE'));

      this.set('_journalPanel', null);
      this.set('_contextPanel', null);
      this.set('_panelA', null);
      this.set('_panelB', null);
      this.set('_panelC', null);
      this.set('_panelD', null);
      this.set('_panelE', null);

      this.set('isExpanded', false);
    }
  }

});
