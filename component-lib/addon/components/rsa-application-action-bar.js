import Ember from 'ember';
import layout from '../templates/components/rsa-application-action-bar';

const {
  Component,
  inject: {
    service
  },
  computed
} = Ember;


export default Component.extend({
  layout,

  eventBus: service('event-bus'),

  layoutService: service('layout'),

  classNames: ['rsa-application-action-bar'],

  incidentJournalActive: computed('layoutService.journalPanel', 'layoutService.actionConfig', function() {
    return this.get('layoutService.journalPanel') !== 'hidden' && this.get('layoutService.actionConfig') === 'incident';
  }),

  incidentOverviewActive: computed('layoutService.panelA', 'layoutService.actionConfig', function() {
    return this.get('layoutService.panelA') !== 'hidden' && this.get('layoutService.actionConfig') === 'incident';
  }),

  actions: {
    toggleIncidentQueue() {
      this.get('layoutService').toggleIncidentQueue();
    },

    toggleIncidentJournal() {
      if ((this.get('layoutService.journalPanel') === 'hidden') && (this.get('layoutService.panelA') === 'hidden')) {
        this.set('layoutService.journalPanel', 'quarter');

        if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'full') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'main');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'main') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'half');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'half') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'quarter');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'quarter') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'hidden');
        }
      } else if (this.get('layoutService.journalPanel') !== 'hidden') {
        this.set('layoutService.journalPanel', 'hidden');

        if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'main') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'full');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'half') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'main');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'quarter') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'half');
        }
      } else {
        this.set('layoutService.journalPanel', 'quarter');
        this.set('layoutService.panelA', 'hidden');
      }
    },

    toggleIncidentOverview() {
      if ((this.get('layoutService.journalPanel') === 'hidden') && (this.get('layoutService.panelA') === 'hidden')) {
        this.set('layoutService.panelA', 'quarter');

        if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'full') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'main');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'main') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'half');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'half') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'quarter');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'quarter') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'hidden');
        }
      } else if (this.get('layoutService.panelA') !== 'hidden') {
        this.set('layoutService.panelA', 'hidden');

        if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'main') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'full');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'half') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'main');
        } else if (this.get(`layoutService.${this.get('layoutService.main')}`) === 'quarter') {
          this.set(`layoutService.${this.get('layoutService.main')}`, 'half');
        }
      } else {
        this.set('layoutService.panelA', 'quarter');
        this.set('layoutService.journalPanel', 'hidden');
      }
    }

  }

});
